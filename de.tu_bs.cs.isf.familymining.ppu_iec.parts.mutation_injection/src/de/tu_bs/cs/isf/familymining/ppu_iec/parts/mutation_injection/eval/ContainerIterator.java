package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.eval;

import static de.tu_bs.cs.isf.e4cf.core.transform.TransformationHelper.ifInstanceOfThen;

import java.util.function.BiFunction;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.action.ActionCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.AbstractLanguageContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.LanguageImplementationOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.language.sfc.implementation.SFCActionContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.pou.POUVariableOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractOption;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.variable.VariableContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;

public abstract class ContainerIterator {

	public <T extends EObject> void iterate(IECAbstractContainer<T> container) {
		process(container);
	}

	protected <T extends IECAbstractContainer<?>> void processOption(IECAbstractOption<T> option) {
		if (option == null) {
			return;
		}

		// Exclude language implementation options as they only hold a set of language
		// containers
		boolean handled = ifInstanceOfThen(option, LanguageImplementationOption.class, (lImplContainer) -> {
			for (IECAbstractContainer<?> container : option.getAllContainer()) {
				processContainer(container);
			}
		});

		// variable options need special structure features
		handled |= ifInstanceOfThen(option, POUVariableOption.class, (varOption) -> {
			process(varOption);

			for (VariableContainer varContainer : varOption.getAllContainer()) {
				Variable first = varContainer.getFirst();
				Variable second = varContainer.getSecond();
				BiFunction<Variable, VariableDeclaration, Boolean> varCheck = (var, decl) -> var != null
						&& var.getScope() == decl;
				if (varCheck.apply(first, VariableDeclaration.VAR_OUTPUT)
						|| varCheck.apply(second, VariableDeclaration.VAR_OUTPUT)) {
					processContainer(varContainer);
				} else if (varCheck.apply(first, VariableDeclaration.VAR_INPUT)
						|| varCheck.apply(second, VariableDeclaration.VAR_INPUT)) {
					processContainer(varContainer);
				} else if (varCheck.apply(first, VariableDeclaration.VAR)
						|| varCheck.apply(second, VariableDeclaration.VAR)) {
					processContainer(varContainer);
				}
			}
		});

		if (!handled) { // Otherwise, create a structural feature
			process(option);

			for (IECAbstractContainer<?> container : option.getAllContainer()) {
				processContainer(container);
			}
		}

	}

	protected <T extends EObject> void processContainer(IECAbstractContainer<T> container) {
		if (container == null) {
			return;
		}

		process(container);

		// Handle impl option for actions
		ifInstanceOfThen(container, ActionCompareContainer.class, (actionContainer) -> {
			processOption(actionContainer.getActionImplOption());
		});

		// Handle action, impl, var options for actions
		ifInstanceOfThen(container, POUCompareContainer.class, (pouContainer) -> {
			processOption(pouContainer.getPouActionOption());
			processOption(pouContainer.getPouImplOption());
			processOption(pouContainer.getPouVarOption());
		});

		// Handle impl option for sfc actions
		ifInstanceOfThen(container, SFCActionContainer.class, (sfcActionContainer) -> {
			processOption(sfcActionContainer.getImplOption());
		});

		// Handle recursive impl structure for language elements
		ifInstanceOfThen(container, AbstractLanguageContainer.class, (actionContainer) -> {
			processOption(actionContainer.getImplOption());
		});
	}

	protected abstract <T extends EObject> void process(IECAbstractContainer<T> container);

	protected abstract <T extends IECAbstractContainer<?>> void process(IECAbstractOption<T> option);
}
