package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationContextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.MutationInjection;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.injection.SingleContextFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Mutator;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Type2Mutator;

public class MutationInjectionController {

	private MutationInjection mutationInjection;
	
	@PostConstruct
	public void init() {
		demo();
	}
	
	@Inject
	public void fillContext(IEclipseContext context) {
		context.set("type2", new Type2Mutator());
		context.set("single", new SingleContextFactory());
		
		mutationInjection = ContextInjectionFactory.make(MutationInjection.class, context);
	}
	
	public void demo() {
		try {
			String scenario24_small = "scenario24_small";
			mutationInjection.generateMutant(scenario24_small, "-mutated");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
