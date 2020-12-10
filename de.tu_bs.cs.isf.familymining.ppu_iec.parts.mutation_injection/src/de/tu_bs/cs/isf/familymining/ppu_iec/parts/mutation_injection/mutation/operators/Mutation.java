package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;

public interface Mutation extends Function<MutationContext, MutationContext> {

}
