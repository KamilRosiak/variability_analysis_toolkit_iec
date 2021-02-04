package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.operators;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.emf.ecore.EObject;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.MutationContext;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.POUImpl;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.impl.ResourceImpl;

/**
 * Base class for POU mutations.
 * @author Kamil Rosiak
 *
 */
public abstract class POUMutation implements Mutation{
	private Randomization randomly;
	
	@PostConstruct
	public void postConstruct() {
		this.setRandomly(new Randomization());
	}
	
	/**
	 * Predicate for POUImpl.class 
	 * @return
	 */
	protected Predicate<EObject> pouContainers() {
		return (eobject) -> Stream.of(POUImpl.class).anyMatch(clazz -> clazz.isAssignableFrom(eobject.getClass()));
	}
	
	/**
	 * Return a list with all POUImpl objects from the context that is shuffled
	 * @param mutCtx
	 * @return
	 */
	protected List<POUImpl> getRandomizedPOUs(MutationContext mutCtx) {
		// collect all pou container from the context
		List<POUImpl> randomizedPOUContainers = mutCtx.getCtxObjects().stream().filter(pouContainers()).map(e -> (POUImpl)e)
				.collect(Collectors.toList());
		// Shuffle the list
		Collections.shuffle(randomizedPOUContainers);
		return randomizedPOUContainers;
	}

	protected List<EObject> getRandomResource(MutationContext ctx) {
		return ctx.getCtxObjects().stream().filter(e -> e instanceof ResourceImpl).map(e -> (ResourceImpl)e).collect(Collectors.toList());
	}
	
	/**
	 * returns a random resource form a configuration
	 * @param config
	 * @return
	 */
	public ResourceImpl getResource(Configuration config) {
		if(!config.getResources().isEmpty()) {
			Collections.shuffle(config.getResources());
			return (ResourceImpl) config.getResources().get(0);
		} else {
			return null;
		}
	}

	/**
	 * Returns a random selected POUImpl from the list
	 * @param pousWithActions
	 * @return
	 */
	protected POUImpl getRandomPOU(List<POUImpl> pousWithActions) {
		int randIndex = getRandomly().nextInt(pousWithActions.size());
		return pousWithActions.get(randIndex);
	}
	
	public Randomization getRandomly() {
		return randomly;
	}

	public void setRandomly(Randomization randomly) {
		this.randomly = randomly;
	}

	
	
}
