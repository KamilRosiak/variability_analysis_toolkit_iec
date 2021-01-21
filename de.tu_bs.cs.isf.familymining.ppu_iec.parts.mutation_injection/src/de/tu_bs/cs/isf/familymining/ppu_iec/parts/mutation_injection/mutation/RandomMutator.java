package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators.Mutation;

public class RandomMutator implements Mutator {
	private final int maxMutationCount;
	
	private final ArrayList<Mutation> mutations;
	private final int[] mutationChance;
	
	private boolean[] mutationMiss;

	public RandomMutator(int maxMutationCount, Map<Mutation, Integer> mutationsWithChance) {
		this.maxMutationCount = maxMutationCount;
		this.mutations = new ArrayList<>(mutationsWithChance.keySet());
		
		this.mutationChance = new int[mutationsWithChance.size()];
		for (int i = 0; i < mutationsWithChance.size(); i++) {
			Mutation m = this.mutations.get(i);
			mutationChance[i] = mutationsWithChance.get(m) + (i > 0 ? mutationChance[i-1] : 0);			
		}
		this.mutationMiss = new boolean[mutationsWithChance.size()];
	}

	public RandomMutator(int maxMutationCount, List<Mutation> mutations) {
		this.maxMutationCount = maxMutationCount;
		this.mutations = new ArrayList<>(mutations);
		
		this.mutationChance = new int[mutations.size()];
		for (int i = 0; i < mutationChance.length; i++) {
			mutationChance[i] = i+1;
		}
		this.mutationMiss = new boolean[mutations.size()];
	}
	
	@Override
	public void mutate(MutationContext ctx) {
		Arrays.fill(mutationMiss, false);
		
		int mutationCount = 0;
		MutationContext curCtx = ctx;
		do {
			int randIndex = pickMutationIndex();
			MutationContext newCtx = mutations.get(randIndex).apply(curCtx);
			if (!newCtx.equals(curCtx)) {
				mutationCount++;
				Arrays.fill(mutationMiss, false);
			} else {
				mutationMiss[randIndex] = true;
			}
			
			curCtx = newCtx;
		} while (mutationCount < maxMutationCount && !allTrue(mutationMiss));
	}


	private boolean allTrue(boolean[] booleans) {
		for (int i = 0; i < booleans.length; i++) {
			if (!booleans[i]) {
				return false;
			}
		}
		return true;
	}

	private int pickMutationIndex() {
		Random r = new Random();
		int accMutationChance = mutationChance[mutations.size() - 1];
		int randInt = r.nextInt(accMutationChance);
		for (int i = 0; i < mutations.size(); i++) {
			int chance = mutationChance[i];
			if (randInt < chance) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public MutatorType getMutatorType() {
		return MutatorType.MIXED;
	}
}
