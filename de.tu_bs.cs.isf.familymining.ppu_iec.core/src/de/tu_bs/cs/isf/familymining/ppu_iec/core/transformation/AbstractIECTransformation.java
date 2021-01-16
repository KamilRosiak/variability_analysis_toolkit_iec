package de.tu_bs.cs.isf.familymining.ppu_iec.core.transformation;

import static de.tu_bs.cs.isf.e4cf.core.transform.TransformationHelper.ifInstanceOfThen;

import java.util.function.BiFunction;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.e4cf.core.compare.templates.AbstractContainer;
import de.tu_bs.cs.isf.e4cf.core.transform.Transformation;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.action.ActionCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.AbstractLanguageContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.LanguageImplementationOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.sfc.implementation.SFCActionContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUVariableOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.variable.VariableContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUStringTable;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.VariabilityThresholdsUtil;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;


/**
 * Helper class that supports creating hierarchical structures similar to the {@link ConfigurationResultRoot}.
 * According to the template pattern, clients implement {@link #connect(IECAbstractContainer, Node)}, 
 * {@link #connect(IECAbstractOption, Node)} and {@link #connect(String, Node)} to create and attach a new Node to 
 * the parent Node, thereby building the their specific tree.
 * 
 * @author Oliver Urbaniak
 *
 * @param <Result> denotes the result type for the transformation
 * @param <Node> denotes the node type which makes up the target tree structure
 */
public abstract class AbstractIECTransformation<Result, Node> implements Transformation<Result> {

	private static final int MAX_LABEL_LENGTH = 20;
	
	protected <T extends IECAbstractContainer<?>> void processOption(IECAbstractOption<T> option, Node parent) {
		if (option == null || parent == null) {
			return;
		}
		
		// Exclude language implementation options as they only hold a set of language containers
		boolean handled = ifInstanceOfThen(option, LanguageImplementationOption.class, (lImplContainer) -> {
			for (IECAbstractContainer<?> container : option.getAllContainer()) {
				processContainer(container, parent);
			}			
		});
		
		// variable options need special structure features
		handled |= ifInstanceOfThen(option, POUVariableOption.class, (varOption) -> {
			Node varOptionNode = connect(varOption, parent); 
			
			Node outputNode = connect("Output", varOptionNode);
			Node inputNode 	= connect("Input", 	varOptionNode);
			Node localNode 	= connect("Local", 	varOptionNode);
			
			for (VariableContainer varContainer : varOption.getAllContainer()) {
				Variable first = varContainer.getFirst();
				Variable second = varContainer.getSecond();
				BiFunction<Variable, VariableDeclaration, Boolean> varCheck = (var, decl) -> var != null && var.getScope() == decl;
				if (varCheck.apply(first, VariableDeclaration.VAR_OUTPUT) || varCheck.apply(second, VariableDeclaration.VAR_OUTPUT)) {
					processContainer(varContainer, outputNode);						
				} else if (varCheck.apply(first, VariableDeclaration.VAR_INPUT) || varCheck.apply(second, VariableDeclaration.VAR_INPUT)) {
					processContainer(varContainer, inputNode);						
				} else if (varCheck.apply(first, VariableDeclaration.VAR) || varCheck.apply(second, VariableDeclaration.VAR)) {
					processContainer(varContainer, localNode);						
				}
			}				
		});
		
		if (!handled) { // Otherwise, create a structural feature
			Node optionFeature = connect(option, parent);
			
			for (IECAbstractContainer<?> container : option.getAllContainer()) {
				processContainer(container, optionFeature);
			}					
		}
		
	}
	
	protected <T extends EObject> void processContainer(IECAbstractContainer<T> container, Node parent) {
		if (container == null || parent == null) {
			return;
		}
		
		Node node = connect(container, parent); 
		
		// Handle impl option for actions
		ifInstanceOfThen(container, ActionCompareContainer.class, (actionContainer) -> {
			processOption(actionContainer.getActionImplOption(), node);
		});
		
		// Handle action, impl, var options for actions
		ifInstanceOfThen(container, POUCompareContainer.class, (pouContainer) -> {
			processOption(pouContainer.getPouActionOption(), node);
			processOption(pouContainer.getPouImplOption(), node);
			processOption(pouContainer.getPouVarOption(), node);
		});
		
		// Handle impl option for sfc actions
		ifInstanceOfThen(container, SFCActionContainer.class, (sfcActionContainer) -> {
			processOption(sfcActionContainer.getImplOption(), node);	
		});
		
		// Handle recursive impl structure for language elements
		ifInstanceOfThen(container, AbstractLanguageContainer.class, (actionContainer) -> {
			processOption(actionContainer.getImplOption(), node);	
		});
	}

	/**
	 * Creates a new <tt>Node</tt> from a container, configures it, and attaches it to its parent.
	 * 
	 * @param <T> denotes the model type the container holds
	 * @param container
	 * @param parent the parent <tt>Node</tt> which the new node will be attached to
	 * @return A connected <tt>Node</tt>
	 */
	protected abstract <T extends EObject> Node connect(IECAbstractContainer<T> container, Node parent);

	/**
	 * Creates a new <tt>Node</tt> from an option, configures it, and attaches it to its parent.
	 * 
	 * @param <T> denotes the model type the container holds
	 * @param option the IEC Option
	 * @param parent the parent <tt>Node</tt> which the new node will be attached to
	 * @return A connected <tt>Node</tt>
	 */
	protected abstract <T extends IECAbstractContainer<?>> Node connect(IECAbstractOption<T> option, Node parent);

	/**
	 * Creates a new <tt>Node</tt> from a String, configures it, and attaches it to the parent. 
	 * 
	 * @param nodeName 
	 * @param parent the parent <tt>Node</tt> which the new node will be attached to
	 * @return A connected <tt>Node</tt>
	 */
	protected abstract Node connect(String nodeName, Node parent);
	
	protected <T extends EObject> String getLabel(IECAbstractContainer<T> container) {
		String leftLabel = container.getLeftLabel() != null ? container.getLeftLabel().trim() : null;
		String rightLabel = container.getRightLabel() != null ? container.getRightLabel().trim() : null;
		
		if (leftLabel != null && leftLabel.length() > MAX_LABEL_LENGTH)
			leftLabel = shorten(leftLabel);
		if (rightLabel != null && rightLabel.length() > MAX_LABEL_LENGTH)
			rightLabel = shorten(rightLabel);
		
		if (VariabilityThresholdsUtil.isMandatory(container)) {
			return leftLabel != null && rightLabel != null ? leftLabel : "";				
		} else if (VariabilityThresholdsUtil.isOptional(container)) {
			if (leftLabel != null) {
				return leftLabel;
			} else if (rightLabel != null) {
				return rightLabel;
			} else {
				return "";
			}
		} else {
			return PPUStringTable.VARIANT_SUBSYSTEM + " ( "+leftLabel+" / "+rightLabel+" )";	
		}
	}

	protected String shorten(String label) {
		if (label.length() > MAX_LABEL_LENGTH) {
			return label.substring(0, MAX_LABEL_LENGTH)+" ...";			
		} else {
			return label;
		}
	}

	protected <T extends AbstractContainer<?, MetricContainer>> String getLabel(IECAbstractOption<T> option) {
		return option.getLabel();
	}
}
