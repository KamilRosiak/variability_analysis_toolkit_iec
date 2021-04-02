package de.tu_bs.isf.familymining.ppu_iec.export.factories.sfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SimpleAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.StepQualifier;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.InVariable;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.JumpStep;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.Transition.Condition;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Connection;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointIn;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointOut;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Position;

/**
 * Exports instances of {@link SequentialFunctionChart} to instances of
 * {@link Body.SFC}, an Ecore to PLCOpen XML conversion.<br>
 * <br>
 * As the Ecore model is much more dense than the PLCOpen XML format, some of
 * the data needs to be generated. The {@code Body.SFC.SelectionConvergence} and
 * {@code Body.SFC.SelectionDivergence} elements interconnect a step with more
 * than 2 transitions. Since the model tracks these connections as references on
 * {@code Step}, these elements must be generated. A {@code Body.SFC.JumpStep}
 * is an element that jumps to another step upon activation. This info is
 * encapsulated in a {@code Transition}. While {@code AbstractAction} represents
 * the {@code Body.SFC.ActionBlock.Action} elements, it is stored directly in
 * the step. The PLCOpen XML schema groups these elements under a
 * {@code Body.SFC.ActionBlock} which must be generated as well.<br>
 * <br>
 * The PLCOpen XML schema can be downloaded on
 * <i>https://plcopen.org/technical-activities/xml-exchange</i> by scrolling to
 * the bottom and clicking the button <i>Download full specification</i>.<br>
 * For more information about the structure of the language see
 * <i>https://www.plcopen.org/system/files/downloads/tc6_xml_v201_technical_doc.pdf</i>.
 * 
 * 
 * @see SFCNodeCallback
 * @author Oliver Urbaniak
 * 
 */
public class SfcExporter {

	private static final int DEFAULT_POS_X = 0;
	private static final int DEFAULT_POS_Y = 0;
	private static final String FORMAL_PARAM_STEP = "sfc";

	/**
	 * Assigns ids for generated elements.
	 */
	private IdService idService;

	/**
	 * Maps local ids to the sfc elements in their xml dom representation
	 */
	private Map<Integer, Object> sfcDomMapping = new HashMap<>();

	/**
	 * Creates an instance of {@code Body.SFC}.<br>
	 * <br>
	 * This SFC export is step-centric which means that the SFC graph is created by
	 * processing step after step. Since all transitions are directly connected to
	 * at least one step, this will produce a complete graph.<br>
	 * <br>
	 * 
	 * <pre>
	 * {@code 
	 * <SFC>
	 *   ...
	 * </SFC>}
	 * </pre>
	 * 
	 * @param sfc the SFC Ecore model instance
	 * @return an instance of PLCOpen xml sfc root
	 */
	public Body.SFC createSfc(SequentialFunctionChart sfc) {
		Body.SFC sfcBody = new Body.SFC();

		idService = new IdService(sfc);
		sfcDomMapping.clear();

		Optional<Step> initStep = sfc.getSteps().stream().filter(Step::getInitialStep).findFirst();
		if (!initStep.isPresent()) {
			throw new RuntimeException("There is no initial step specified.");
		}

		List<Step> steps = new ArrayList<>(sfc.getSteps());
		steps.sort((s1, s2) -> {
			int r = Boolean.compare(s2.getInitialStep(), s1.getInitialStep());
			return r != 0 ? r : Integer.compare(s1.getLocal_ID(), s2.getLocal_ID());
		});

		for (Step step : steps) {
			processStep(step, sfcBody);
		}

		return sfcBody;
	}

	/**
	 * Creates an instance of {@code Body.SFC.Step} including the connected
	 * transitions and con-/divergences.<br>
	 * <br>
	 * 
	 * <pre>
	 * {@code 
	 * <step localId="..." name="..." initialStep="...">
	 *   <position x="0" y="0"/>
	 *   <connectionPointIn>
	 *     <connection refLocalId="..." formalParameter="sfc"/>
	 *   </connectionPointIn>
	 *   <connectionPointOut formalParameter="sfc"/>
	 * </step>}
	 * </pre>
	 * 
	 * @param step    the step Ecore model element
	 * @param sfcBody the PLCOpen xml root for sfc
	 */
	private void processStep(Step step, Body.SFC sfcBody) {
		// instantiate step and attach local properties
		Body.SFC.Step stepInstance = (Body.SFC.Step) sfcDomMapping.get(step.getLocal_ID());
		if (stepInstance == null) {
			stepInstance = new Body.SFC.Step();
			stepInstance.setLocalId(toLocalId(step.getLocal_ID()));
			stepInstance.setName(step.getName());
			stepInstance.setInitialStep(step.getInitialStep());
			stepInstance.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));

			stepInstance.setConnectionPointIn(new ConnectionPointIn());
			Body.SFC.Step.ConnectionPointOut connOut = new Body.SFC.Step.ConnectionPointOut();
			connOut.setFormalParameter(FORMAL_PARAM_STEP);
			stepInstance.setConnectionPointOut(connOut);
		}

		handleIncomingReferences(step, sfcBody, stepInstance);

		sfcBody.getCommentOrErrorOrConnector().add(stepInstance);

		if (!step.getActions().isEmpty()) {
			sfcBody.getCommentOrErrorOrConnector().add(createActionBlock(step.getActions(), step.getLocal_ID()));
		}

		handleOutgoingReferences(step, sfcBody, stepInstance);
	}

	/**
	 * Connects all incoming transitions with the step instance. The transitions and
	 * other prerequisite elements are created as needed and added to the sfc body.
	 * Note that new transitions will not be completed.
	 * Only after both steps around it are processed, the transition will be complete.<br>
	 * 
	 * @param step         the step Ecore model element
	 * @param sfcBody      the PLCOpen xml root for sfc
	 * @param stepInstance the step in PLCOpen format
	 */
	private void handleIncomingReferences(Step step, Body.SFC sfcBody, Body.SFC.Step stepInstance) {
		if (step.getIncomingTransitions().size() == 1) {
			// case 1: a single predecessor transition
			Transition predecessorTransition = step.getIncomingTransitions().get(0);

			// check for existing transition instance or create a new one
			Body.SFC.Transition predecessorTransitionInstance = createTransition(predecessorTransition, sfcBody);

			// interconnect step and transition
			ConnectionPointIn stepConnIn = createInConnections(false, predecessorTransition.getLocal_ID());
			stepInstance.setConnectionPointIn(stepConnIn);

			if (!sfcBody.getCommentOrErrorOrConnector().contains(predecessorTransitionInstance)) {
				sfcBody.getCommentOrErrorOrConnector().add(predecessorTransitionInstance);
			}
			sfcDomMapping.putIfAbsent(predecessorTransition.getLocal_ID(), predecessorTransitionInstance);
		} else if (step.getIncomingTransitions().size() > 1) {
			// case 2: multiple transitions converge (selection convergence) onto onto this
			// step
			Deque<Transition> predecessorTransitionQueue = new ArrayDeque<>(step.getIncomingTransitions());

			// Preallocate the outermost transition
			Transition outermostTransition = predecessorTransitionQueue.pollLast();
			Body.SFC.Transition outerTransitionInstance = createTransition(outermostTransition, sfcBody);
			if (!sfcBody.getCommentOrErrorOrConnector().contains(outerTransitionInstance)) {
				sfcBody.getCommentOrErrorOrConnector().add(outerTransitionInstance);
			}
			sfcDomMapping.putIfAbsent(outermostTransition.getLocal_ID(), outerTransitionInstance);

			int predecessorId = outermostTransition.getLocal_ID();
			do {
				// Allocate the inner transition
				Transition innerTransition = predecessorTransitionQueue.pollLast();
				Body.SFC.Transition innerTransitionInstance = createTransition(innerTransition, sfcBody);
				if (!sfcBody.getCommentOrErrorOrConnector().contains(innerTransitionInstance)) {
					sfcBody.getCommentOrErrorOrConnector().add(innerTransitionInstance);
				}
				sfcDomMapping.putIfAbsent(innerTransition.getLocal_ID(), innerTransitionInstance);

				// combine inner transition and predecessor with a new selection convergence
				Body.SFC.SelectionConvergence convergence = new Body.SFC.SelectionConvergence();
				convergence.setLocalId(idService.claimId());
				convergence.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));
				List<Body.SFC.SelectionConvergence.ConnectionPointIn> convConnIn = createConvergenceConnections(true,
						predecessorId, innerTransition.getLocal_ID());
				convergence.getConnectionPointIn().addAll(convConnIn);
				sfcBody.getCommentOrErrorOrConnector().add(convergence);

				predecessorId = convergence.getLocalId().intValue();
			} while (predecessorTransitionQueue.size() >= 1);

			// Connect this step with the recent convergence
			stepInstance.setConnectionPointIn(createInConnections(false, predecessorId));
		}
	}

	/**
	 * Connects all outgoing transitions with the step instance. The transitions and
	 * other prerequisite elements are created as needed and added to the sfc body.
	 * Note that new transitions will not be completed in this method.
	 * Only after both steps around it are processed, the transition will be complete.<br>
	 * 
	 * @param step         the step ecore model element
	 * @param sfcBody      the xml root for sfc
	 * @param stepInstance the step in PLCOpen format
	 */
	private void handleOutgoingReferences(Step step, Body.SFC sfcBody, Body.SFC.Step stepInstance) {
		// only handle forward references - we also do not handle simultaneous
		// divergences
		if (step.getOutgoingTransitions().size() == 1) {
			// case 1: a single successor transition
			Transition successorTransition = step.getOutgoingTransitions().get(0);
			Body.SFC.Transition transitionInstance = createTransition(successorTransition, sfcBody);

			// interconnect step and transition
			ConnectionPointIn transitionConnIn = createInConnections(true, step.getLocal_ID());
			transitionInstance.setConnectionPointIn(transitionConnIn);

			if (!sfcBody.getCommentOrErrorOrConnector().contains(transitionInstance)) {
				sfcBody.getCommentOrErrorOrConnector().add(transitionInstance);
			}
			sfcDomMapping.putIfAbsent(successorTransition.getLocal_ID(), transitionInstance);
		} else if (step.getOutgoingTransitions().size() > 1) {
			// case 2: a selection divergence with multiple transitions
			// --> constructs a divergence tree with transitions as a leaves and divergences
			// as inner nodes

			Deque<Transition> successorTransitionQueue = new ArrayDeque<>(step.getOutgoingTransitions());
			int predecessorId = step.getLocal_ID();
			do {
				// create divergence and attach incoming predecessor connection:
				// [predecessor] <- [divergence]
				Body.SFC.SelectionDivergence divergence = new Body.SFC.SelectionDivergence();
				divergence.setLocalId(idService.claimId());
				divergence.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));
				divergence.setConnectionPointIn(createInConnections(false, predecessorId));

				Body.SFC.SelectionDivergence.ConnectionPointOut connOut = new Body.SFC.SelectionDivergence.ConnectionPointOut();
				connOut.setFormalParameter(FORMAL_PARAM_STEP);
				divergence.getConnectionPointOut().add(connOut);
				divergence.getConnectionPointOut().add(connOut);

				sfcBody.getCommentOrErrorOrConnector().add(divergence);

				// create the successor transition and attach it to the divergence:
				// [divergence] <- [transition]
				Transition curTransition = successorTransitionQueue.poll();
				Body.SFC.Transition curTransitionInstance = createTransition(curTransition, sfcBody);
				ConnectionPointIn curTransitionConnIn = createInConnections(true, divergence.getLocalId().intValue());
				curTransitionInstance.setConnectionPointIn(curTransitionConnIn);

				if (!sfcBody.getCommentOrErrorOrConnector().contains(curTransitionInstance)) {
					sfcBody.getCommentOrErrorOrConnector().add(curTransitionInstance);
				}
				sfcDomMapping.putIfAbsent(curTransition.getLocal_ID(), curTransitionInstance);

				predecessorId = divergence.getLocalId().intValue();
			} while (successorTransitionQueue.size() > 1);

			// create final transition, attach it to the last divergence:
			// [divergence] <- [final transition]
			Transition finalTransition = successorTransitionQueue.poll();
			Body.SFC.Transition finalTransitionInstance = createTransition(finalTransition, sfcBody);
			ConnectionPointIn finalTransitionConnIn = createInConnections(true, predecessorId);
			finalTransitionInstance.setConnectionPointIn(finalTransitionConnIn);
			if (!sfcBody.getCommentOrErrorOrConnector().contains(finalTransitionInstance)) {
				sfcBody.getCommentOrErrorOrConnector().add(finalTransitionInstance);
			}

			sfcDomMapping.put(finalTransition.getLocal_ID(), finalTransitionInstance);
		}
	}

	/**
	 * Creates an independent transition without connections to other
	 * con-/divergences or steps (excluding jumps as they are part of the SFC Ecore
	 * model). If the transition holds a condition, an input variable in an SFC
	 * network, an inVar element is added to the <i>sfcBody</i>. <br>
	 * <br>
	 * 
	 * <pre>
	 * {@code 
	 * <transition localId="...">
	 *   <position x="0" y="0"/>
	 *   <connectionPointIn>
	 *     <connection refLocalId="..." formalParameter="sfc"/>
	 *   </connectionPointIn>
	 *   <condition>
	 *     <connectionPointIn>
	 *       <connection refLocalId="..."/>
	 *     </connectionPointIn>
	 *   </condition>
	 * </transition>}
	 * </pre>
	 * 
	 * @param transition the ecore model transition
	 * @param sfcBody    the xml root for sfc
	 */
	private Body.SFC.Transition createTransition(Transition transition, Body.SFC sfcBody) {
		// check for an already created transition with the same id
		Body.SFC.Transition predecessorTransitionInstance = (Body.SFC.Transition) sfcDomMapping
				.get(transition.getLocal_ID());
		if (predecessorTransitionInstance != null) {
			return predecessorTransitionInstance;
		}

		// create a new transition with its attributes, conditions and jump connection
		Body.SFC.Transition transitionInstance = new Body.SFC.Transition();
		transitionInstance.setLocalId(toLocalId(transition.getLocal_ID()));
		transitionInstance.setConnectionPointIn(new ConnectionPointIn());
		transitionInstance.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));

		if (transition.getCondition() != null && !transition.getCondition().isEmpty()) {
			InVariable inVarInstance = createInVariable(transition.getCondition());
			if (!sfcBody.getCommentOrErrorOrConnector().contains(inVarInstance)) {
				sfcBody.getCommentOrErrorOrConnector().add(inVarInstance);
			}

			Condition condition = new Condition();
			ConnectionPointIn conditionConnIn = new ConnectionPointIn();
			Connection conditionConnection = new Connection();
			conditionConnection.setRefLocalId(inVarInstance.getLocalId());
			conditionConnIn.getConnection().add(conditionConnection);
			condition.setConnectionPointIn(conditionConnIn);
			transitionInstance.setCondition(condition);
		}

		// we try to maintain the original order by placing the transition between its
		// "inVariable" and "jumpStep" (if available)
		if (transition.isIsJump()) {
			if (transition.getTargetStep().size() != 1) {
				throw new RuntimeException("The number of target steps for a jump is expected to be 1.");
			}

			// search for a jump step already created before, if not available -> create a
			// new one
			Step jumpTarget = transition.getTargetStep().get(0);
			JumpStep jumpStep = findJumpStep(transition, sfcBody).orElseGet(() -> createJumpStep(jumpTarget.getName()));

			Connection jumpConn = new Connection();
			jumpConn.setRefLocalId(toLocalId(transition.getLocal_ID()));
			jumpStep.getConnectionPointIn().getConnection().add(jumpConn);

			sfcBody.getCommentOrErrorOrConnector().add(transitionInstance);
			sfcBody.getCommentOrErrorOrConnector().add(jumpStep);
		}
		if (transition.getTargetStep().size() > 1) {
			throw new RuntimeException("The export does not handle simultaneous divergences");
		}

		return transitionInstance;
	}

	/**
	 * 
	 * <pre>
	 * {@code 
	 * <inVariable localId="...">
	 *   <position x="0" y="0"/>
	 *   <connectionPointOut/>
	 *   <expression>...</expression>
	 * </inVariable>}
	 * </pre>
	 * 
	 * @param expression
	 * @return
	 */
	private Body.SFC.InVariable createInVariable(String expression) {
		Body.SFC.InVariable inVarInstance = new Body.SFC.InVariable();
		inVarInstance.setLocalId(idService.claimId());
		inVarInstance.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));
		inVarInstance.setConnectionPointOut(new ConnectionPointOut());
		inVarInstance.setExpression(expression);

		return inVarInstance;
	}

	/**
	 * Creates a connectionPointIn element with a connection element for each
	 * refLocalId.
	 * 
	 * <pre>
	 * {@code 
	 * <connectionPointIn>
	 * 	<connection refLocalId="..." (formalParameter"...")>
	 * 	... 
	 * </connectionPointIn>}
	 * </pre>
	 * 
	 * @param includeParameterFlag
	 * @param refLocalIds
	 * @return
	 */
	private ConnectionPointIn createInConnections(boolean includeParameterFlag, int... refLocalIds) {
		ConnectionPointIn connPointIn = new ConnectionPointIn();
		for (int refLocalId : refLocalIds) {
			Connection conn = new Connection();
			conn.setFormalParameter(includeParameterFlag ? FORMAL_PARAM_STEP : null);
			conn.setRefLocalId(toLocalId(refLocalId));
			connPointIn.getConnection().add(conn);
		}

		return connPointIn;
	}

	/**
	 * Creates a list of connectionPointIn elements for each refLocalId attached to
	 * a connection element.
	 * 
	 * <pre>
	 * {@code 
	 * <connectionPointIn>
	 * 	<connection refLocalId="..." (formalParameter"...")>
	 * </connectionPointIn>
	 * <connectionPointIn>
	 * 	...
	 * </connectionPointIn>
	 * ...} <br>
	 * 
	 * </pre>
	 * 
	 * @param includeParameterFlag
	 * @param refLocalIds
	 * @return a set of {@code <connectionPointIn>...</connectionPointIn>}
	 */
	private List<Body.SFC.SelectionConvergence.ConnectionPointIn> createConvergenceConnections(
			boolean includeParameterFlag, int... refLocalIds) {
		List<Body.SFC.SelectionConvergence.ConnectionPointIn> connPoints = new ArrayList<>();
		for (int refLocalId : refLocalIds) {
			Body.SFC.SelectionConvergence.ConnectionPointIn connPointIn = new Body.SFC.SelectionConvergence.ConnectionPointIn();
			Connection conn = new Connection();
			conn.setFormalParameter(includeParameterFlag ? FORMAL_PARAM_STEP : null);
			conn.setRefLocalId(toLocalId(refLocalId));
			connPointIn.getConnection().add(conn);
			connPoints.add(connPointIn);
		}

		return connPoints;
	}

	/**
	 * Converts a list of {@link AbstractAction}s back to an instance of
	 * {@code <actionBlock>...</actionBlock>}.
	 * 
	 * @param actionBlock action block subject to conversion
	 * @param refLocalId  the step reference
	 * @return {@code <actionBlock>...</actionBlock>}
	 */
	public Body.SFC.ActionBlock createActionBlock(List<AbstractAction> actions, int refLocalId) {
		Body.SFC.ActionBlock actionBlockInstance = new Body.SFC.ActionBlock();
		actionBlockInstance.setLocalId(idService.claimId());
		actionBlockInstance.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));

		ConnectionPointIn connIn = new ConnectionPointIn();
		Connection conn = new Connection();
		conn.setRefLocalId(toLocalId(refLocalId));
		connIn.getConnection().add(conn);
		actionBlockInstance.setConnectionPointIn(connIn);

		for (AbstractAction action : actions) {
			Body.SFC.ActionBlock.Action actionInstance = createAction(action);
			actionBlockInstance.getAction().add(actionInstance);
		}

		return actionBlockInstance;
	}

	/**
	 * Converts an {@link AbstractAction} back to an {@link Body.SFC.ActionBlock.Action}.
	 * 
	 * @param action action subject to conversion
	 * @return {@code <action>...</action>}
	 */
	public Body.SFC.ActionBlock.Action createAction(AbstractAction action) {
		Body.SFC.ActionBlock.Action actionInstance = new Body.SFC.ActionBlock.Action();
		actionInstance.setLocalId(toLocalId(action.getLocalId()));
		actionInstance.setDuration("");
		actionInstance.setIndicator("");
		actionInstance.setRelPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));
		actionInstance.setConnectionPointOut(new ConnectionPointOut());

		if (action.getQualifier() != StepQualifier.UNDEFINED) {
			actionInstance.setQualifier(action.getQualifier().toString());
		}

		if (action instanceof SimpleAction) {
			SimpleAction simpleAction = (SimpleAction) action;
			Body.SFC.ActionBlock.Action.Reference refVariableInstance = new Body.SFC.ActionBlock.Action.Reference();
			refVariableInstance.setName(simpleAction.getCondition().getName());
			actionInstance.setReference(refVariableInstance);
		} else {
			// complex actions may reference other actions in the same or in other pous
			ComplexAction complexAction = (ComplexAction) action;
			Action pouAction = complexAction.getPouAction();
			Variable pouVar = complexAction.getPouVariable();

			if (pouAction != null) {
				Body.SFC.ActionBlock.Action.Reference refActionInstance = new Body.SFC.ActionBlock.Action.Reference();
				if (pouVar != null) {
					refActionInstance.setName(String.format("%s.%s", pouVar.getName(), pouAction.getName()));
				} else {
					refActionInstance.setName(String.format("%s", pouAction.getName()));
				}
				actionInstance.setReference(refActionInstance);
			} else {
				throw new RuntimeException("The complex action has no reference on a POU action.");
			}
		}

		return actionInstance;
	}

	/**
	 * Creates an instance of {@link Body.SFC.JumpStep}.
	 * 
	 * @param targetName the target step name
	 * @return {@code <jumpStep>...</jumpStep>}
	 */
	private JumpStep createJumpStep(String targetName) {
		JumpStep jumpStep = new JumpStep();
		jumpStep.setLocalId(idService.claimId());
		jumpStep.setPosition(createPosition(DEFAULT_POS_X, DEFAULT_POS_Y));
		jumpStep.setTargetName(targetName);
		ConnectionPointIn jumpConnIn = new ConnectionPointIn();
		jumpStep.setConnectionPointIn(jumpConnIn);

		return jumpStep;
	}

	/**
	 * Creates an instance of {@link Position}.
	 * 
	 * @param x 
	 * @param y 
	 * @return {@code <position>...</position>}
	 */
	private Position createPosition(int x, int y) {
		Position pos = new Position();
		pos.setX(BigDecimal.valueOf(x));
		pos.setY(BigDecimal.valueOf(y));
		return pos;
	}

	/**
	 * Searches for an already parsed jump step that the specified transition is
	 * connected to. A transition in the ecore model does not have an explicit jump
	 * step. It connects directly to the jump target step as a target step. Since an
	 * OpenPLC jump step can have more than one source transition, we need to be
	 * able to find already parsed jumps steps to avoid duplication.
	 * 
	 * @param transition the transition which targets a step by jumping to it
	 * @param sfc        the DOM representation for the SFC structure
	 * @return
	 */
	private Optional<JumpStep> findJumpStep(Transition transition, Body.SFC sfc) {
		List<JumpStep> jumpSteps = sfc.getCommentOrErrorOrConnector().stream().filter(obj -> obj instanceof JumpStep)
				.map(obj -> (JumpStep) obj).collect(Collectors.toList());

		List<String> targetStepNames = transition.getTargetStep().stream().filter(target -> target.getName() != null)
				.map(Step::getName).collect(Collectors.toList());

		return jumpSteps.stream().filter(jumpStep -> targetStepNames.contains(jumpStep.getTargetName())).findFirst();
	}

	private BigInteger toLocalId(int n) {
		return BigInteger.valueOf(n);
	}

	/**
	 * Provides ids on request. The provided ids start at 0 and are restricted by the ids already reserved in an SFC.
	 * 
	 * @author Oliver Urbaniak
	 */
	private class IdService {
		private int curId = 0;
		private Set<Integer> reservedIds = new TreeSet<>();

		public IdService(SequentialFunctionChart sfc) {
			reservedIds.clear();
			sfc.getSteps().stream().map(Step::getLocal_ID).forEach(reservedIds::add);
			sfc.getSteps().stream().flatMap(step -> step.getActions().stream()).mapToInt(AbstractAction::getLocalId)
					.forEach(reservedIds::add);
			sfc.getTransitions().stream().map(Transition::getLocal_ID).forEach(reservedIds::add);
		}

		public BigInteger claimId() {
			while (reservedIds.contains(curId)) {
				curId++;
			}
			reservedIds.add(curId);
			return toLocalId(curId);
		}
	}
}
