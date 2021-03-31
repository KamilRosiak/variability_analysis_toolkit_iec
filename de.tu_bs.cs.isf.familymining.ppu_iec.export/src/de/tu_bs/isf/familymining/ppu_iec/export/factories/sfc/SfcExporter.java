package de.tu_bs.isf.familymining.ppu_iec.export.factories.sfc;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.AbstractAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SimpleAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.StepQualifier;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.InVariable;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.JumpStep;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Body.SFC.Transition.Condition;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Connection;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointIn;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.ConnectionPointOut;

public class SfcExporter {

	private static final String FORMAL_PARAM_STEP = "sfc";
	private static final int UNKNOW_ID = 1000;

	private IdService idService;

	/**
	 * Maps local ids to the sfc elements in their xml dom representation
	 */
	private Map<Integer, Object> sfcDomMapping = new HashMap<>();

	public Body.SFC createSfc(SequentialFunctionChart sfc) {
		Body.SFC sfcBody = new Body.SFC();

		// claim ids reserved by existing steps, transitions and actions to prevent id
		// collision
		idService = new IdService(sfc);

		// before parsing the steps, convert all the transitions without their
		// connections
		sfcDomMapping.clear();

		Optional<Step> initStep = sfc.getSteps().stream().filter(Step::getInitialStep).findFirst();
		if (!initStep.isPresent()) {
			throw new RuntimeException("There is no initial step specified.");
		}

		List<Step> steps = sfc.getSteps();
		steps.sort((s1, s2) -> {
			int r = Boolean.compare(s1.getInitialStep(), s2.getInitialStep());
			if (r != 0) {
				return r;
			} else {
				return Integer.compare(s1.getLocal_ID(), s2.getLocal_ID());
			}
		});

		for (Step step : steps) {
			processStep(step, sfcBody);
		}

		return sfcBody;
	}

	public void processStep(Step step, Body.SFC sfcBody) {
		// instantiate step and attach local properties
		Body.SFC.Step stepInstance = (Body.SFC.Step) sfcDomMapping.get(step.getLocal_ID());
		if (stepInstance == null) {
			stepInstance = new Body.SFC.Step();
			stepInstance.setLocalId(toLocalId(step.getLocal_ID()));
			stepInstance.setName(step.getName());
		}

		handleIncomingReferences(step, sfcBody, stepInstance);

		sfcBody.getCommentOrErrorOrConnector().add(stepInstance);
		sfcBody.getCommentOrErrorOrConnector().add(createActionBlock(step.getActions(), step.getLocal_ID()));

		handleOutgoingReferences(step, sfcBody, stepInstance);
	}

	/**
	 * Connects all incoming transitions with the step instance. The transitions and
	 * all prerequisite elements are also added to the sfc body.
	 * 
	 * @param step
	 * @param sfcBody
	 * @param stepInstance
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

			sfcBody.getCommentOrErrorOrConnector().add(predecessorTransitionInstance);
			sfcDomMapping.putIfAbsent(predecessorTransition.getLocal_ID(), predecessorTransitionInstance);
		} else if (step.getOutgoingTransitions().size() > 1) {
			// case 2: multiple transitions converge (selection convergence) onto onto this
			// step
			Deque<Transition> predecessorTransitionQueue = new ArrayDeque<>(step.getOutgoingTransitions());

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
	 * Connects all outgoing connections with the step instance. The newly encountered transitions are
	 * created on-the-fly and added to the xml dom mapping
	 * 
	 * @param step
	 * @param sfcBody
	 * @param stepInstance
	 */
	private void handleOutgoingReferences(Step step, Body.SFC sfcBody, Body.SFC.Step stepInstance) {
		// only handle forward references - we also do not handle simultaneous
		// divergences
		if (step.getOutgoingTransitions().size() == 1) {
			// case 1: a single successor transition
			Transition successorTransition = step.getOutgoingTransitions().get(0);

			Body.SFC.Transition transitionInstance = new Body.SFC.Transition();
			transitionInstance.setLocalId(toLocalId(successorTransition.getLocal_ID()));

			// interconnect step and transition
			ConnectionPointIn transitionConnIn = new ConnectionPointIn();
			Connection transitionConn = new Connection();
			transitionConn.setFormalParameter(FORMAL_PARAM_STEP);
			transitionConn.setRefLocalId(stepInstance.getLocalId());
			transitionConnIn.getConnection().add(transitionConn);
			transitionInstance.setConnectionPointIn(transitionConnIn);

			sfcBody.getCommentOrErrorOrConnector().add(stepInstance);
			if (!sfcBody.getCommentOrErrorOrConnector().contains(transitionInstance)) {				
				sfcBody.getCommentOrErrorOrConnector().add(transitionInstance);
			}
			sfcDomMapping.putIfAbsent(successorTransition.getLocal_ID(), transitionInstance);
		} else if (step.getOutgoingTransitions().size() > 1) {
			// case 2: a selection divergence with multiple transitions
			// --> constructs a divergence tree with transitions as a leaves and divergences
			// as inner nodes

			sfcBody.getCommentOrErrorOrConnector().add(stepInstance);

			Deque<Transition> successorTransitionQueue = new ArrayDeque<>(step.getOutgoingTransitions());
			int predecessorId = step.getLocal_ID();
			do {
				// create divergence and attach incoming predecessor connection: [predecessor]
				// <- [divergence]
				Body.SFC.SelectionDivergence divergence = new Body.SFC.SelectionDivergence();
				divergence.setLocalId(idService.claimId());
				divergence.setConnectionPointIn(createInConnections(false, predecessorId));

				// create the successor transition and attach it to the divergence: [divergence]
				// <- [transition]
				Transition curTransition = successorTransitionQueue.poll();
				Body.SFC.Transition curTransitionInstance = new Body.SFC.Transition();
				curTransitionInstance.setLocalId(toLocalId(curTransition.getLocal_ID()));
				ConnectionPointIn curTransitionConnIn = new ConnectionPointIn();
				Connection curTransitionConn = new Connection();
				curTransitionConn.setFormalParameter(FORMAL_PARAM_STEP);
				curTransitionConn.setRefLocalId(divergence.getLocalId());
				curTransitionConnIn.getConnection().add(curTransitionConn);
				
				if (!sfcBody.getCommentOrErrorOrConnector().contains(curTransitionInstance)) {
					curTransitionInstance.setConnectionPointIn(curTransitionConnIn);					
				}
				sfcDomMapping.putIfAbsent(curTransition.getLocal_ID(), curTransitionInstance);

				predecessorId = divergence.getLocalId().intValue();
			} while (successorTransitionQueue.size() > 1);

			// create final transition and attach it to the last divergence: [divergence] <- [final transition]
			Transition finalTransition = successorTransitionQueue.poll();
			createTransition(finalTransition, sfcBody);
			
			Body.SFC.Transition finalTransitionInstance = new Body.SFC.Transition();
			finalTransitionInstance.setLocalId(toLocalId(finalTransition.getLocal_ID()));
			ConnectionPointIn finalTransitionConnIn = new ConnectionPointIn();
			Connection finalTransitionConn = new Connection();
			finalTransitionConn.setFormalParameter(FORMAL_PARAM_STEP);
			finalTransitionConn.setRefLocalId(toLocalId(predecessorId));
			finalTransitionConnIn.getConnection().add(finalTransitionConn);
			finalTransitionInstance.setConnectionPointIn(finalTransitionConnIn);
			sfcDomMapping.put(finalTransition.getLocal_ID(), finalTransitionInstance);
		}
	}

	/**
	 * Creates an independent transition without connections to other
	 * con-/divergences or steps (excluding jumps as they are simplified in the sfc
	 * ecore model).
	 * 
	 * @param transition the ecore model transition
	 * @param sfcBody    the xml root for sfc
	 */
	public Body.SFC.Transition createTransition(Transition transition, Body.SFC sfcBody) {
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

		if (transition.getCondition() != null && !transition.getCondition().isEmpty()) {
			InVariable inVar = new InVariable();
			BigInteger inVarId = idService.claimId();
			inVar.setLocalId(inVarId);
			inVar.setExpression(transition.getCondition());

			Condition condition = new Condition();
			ConnectionPointIn conditionConnIn = new ConnectionPointIn();
			Connection conditionConnection = new Connection();
			conditionConnection.setRefLocalId(inVarId);
			conditionConnIn.getConnection().add(conditionConnection);
			condition.setConnectionPointIn(conditionConnIn);
			transitionInstance.setCondition(condition);
		}

		// TODO: jump step part needs to be rewritten in order to support convergence handling!!!
		
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

			// the ecore model does not express jump steps as steps, it marks the transitions 
			sfcBody.getCommentOrErrorOrConnector().add(transitionInstance);
			sfcBody.getCommentOrErrorOrConnector().add(jumpStep);
		}
		if (transition.getTargetStep().size() > 1) {
			throw new RuntimeException("The export does not handle simultaneous divergences");
		}

		return transitionInstance;
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
	 * @return
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

	private void addOutConnection(Body.SFC.Transition transition, int localId) {
		if (transition.getConnectionPointOut() == null) {
			ConnectionPointOut connOut = new ConnectionPointOut();
			transition.setConnectionPointOut(connOut);
		}

		Connection conn = new Connection();
		conn.setFormalParameter(FORMAL_PARAM_STEP);
		conn.setRefLocalId(toLocalId(localId));
	}

	private Body.SFC.InVariable createInVariable(int localId, String expression) {
		Body.SFC.InVariable inVariableInstance = new Body.SFC.InVariable();
		inVariableInstance.setLocalId(toLocalId(localId));
		inVariableInstance.setExpression(expression);
		return inVariableInstance;
	}

	/**
	 * Converts a list of {@link AbstractAction}s back to an instance of
	 * {@code <actionBlock>...</actionBlock>}.
	 * 
	 * @param actionBlock action block subject to conversion
	 * @param refLocalId  the step reference
	 * @return {@code <actionBlock>...</actionBlock>}
	 */
	public Body.SFC.ActionBlock createActionBlock(List<AbstractAction> actionBlock, int refLocalId) {
		Body.SFC.ActionBlock actionBlockInstance = new Body.SFC.ActionBlock();

		// TODO: The id from actionBlock is not retained in the model and is selected
		// arbitrarily atm
		actionBlockInstance.setLocalId(toLocalId(UNKNOW_ID));

		ConnectionPointIn connIn = new ConnectionPointIn();
		Connection conn = new Connection();
		conn.setRefLocalId(toLocalId(refLocalId));
		connIn.getConnection().add(conn);
		actionBlockInstance.setConnectionPointIn(connIn);

		return actionBlockInstance;
	}

	/**
	 * Converts an {@link AbstractAction} back to an instance of
	 * {@code <action>...</action>}.
	 * 
	 * @param action action subject to conversion
	 * @return {@code <action>...</action>}
	 */
	public Body.SFC.ActionBlock.Action createAction(AbstractAction action) {
		Body.SFC.ActionBlock.Action actionInstance = new Body.SFC.ActionBlock.Action();
		actionInstance.setLocalId(toLocalId(action.getLocalId()));

		if (action.getQualifier() != StepQualifier.UNDEFINED) {
			actionInstance.setQualifier(action.getQualifier().toString());
		}

		if (action instanceof SimpleAction) {
			SimpleAction simpleAction = (SimpleAction) action;
			Body.SFC.ActionBlock.Action.Reference refVariableInstance = new Body.SFC.ActionBlock.Action.Reference();
			refVariableInstance.setName(simpleAction.getActionVariable().getName());
			actionInstance.setReference(refVariableInstance);
		} else {
			ComplexAction complexAction = (ComplexAction) action;
			Action pouAction = complexAction.getPouAction();
			if (pouAction.eContainer() instanceof POU) {
				POU complexActionPou = (POU) pouAction.eContainer();
				Body.SFC.ActionBlock.Action.Reference refActionInstance = new Body.SFC.ActionBlock.Action.Reference();
				refActionInstance
						.setName(String.format("%s.%s", complexActionPou.getIdentifier(), pouAction.getName()));
				actionInstance.setReference(refActionInstance);
			} else {
				throw new RuntimeException(
						String.format("No POU can be found for the complex action %s.", pouAction.getName()));
			}
		}

		return actionInstance;
	}

	private JumpStep createJumpStep(String targetName) {
		JumpStep jumpStep = new JumpStep();
		jumpStep.setLocalId(idService.claimId());
		jumpStep.setTargetName(targetName);
		ConnectionPointIn jumpConnIn = new ConnectionPointIn();
		jumpStep.setConnectionPointIn(jumpConnIn);

		return jumpStep;
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

	private class IdService {
		int curId = 0;
		Set<Integer> reservedIds = new TreeSet<>();

		public IdService(SequentialFunctionChart sfc) {
			reservedIds.clear();
			sfc.getSteps().stream().map(Step::getLocal_ID).forEach(reservedIds::add);
			sfc.getSteps().stream().flatMap(step -> step.getActions().stream()).map(AbstractAction::getLocalId)
					.forEach(reservedIds::add);
			sfc.getTransitions().stream().map(Transition::getLocal_ID).forEach(reservedIds::add);
		}

		public BigInteger claimId() {
			while (reservedIds.contains(curId)) {
				curId++;
			}
			return toLocalId(curId);
		}
	}
}
