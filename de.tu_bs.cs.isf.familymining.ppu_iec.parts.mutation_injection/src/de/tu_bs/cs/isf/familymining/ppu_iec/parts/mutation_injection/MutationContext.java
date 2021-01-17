package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.MutationPair;

public class MutationContext {

	private final EObject originalRoot;
	private final EObject mutatedRoot;

	private List<EObject> ctxObjects = new ArrayList<>();

	/**
	 * Signals if the eobject tree structure has been modified.
	 */
	private boolean changedTreeStructure = false;

	/**
	 * Maps original to mutated scenario objects. The map is localized to all
	 * scenario objects contained in the list of ctx objects. The map does not keep
	 * track of inserted or removed objects resulting through mutation.
	 */
	private BiMap<EObject, EObject> interProjectMapping = HashBiMap.create(); // original object -> mutated object

	private List<EObject> removals = new ArrayList<>();
	private List<EObject> insertions = new ArrayList<>();

	public MutationContext(EObject originalRoot, EObject mutatedRoot) {
		super();
		this.originalRoot = originalRoot;
		this.mutatedRoot = mutatedRoot;
	}

	public List<EObject> getCtxObjects() {
		return ctxObjects;
	}

	/**
	 * Log the mutated object. Creates the connection between original and mutated
	 * scenario object.
	 * 
	 * @param mutObject
	 */
	public void logChange(EObject mutObject) {
		if (originalRoot == null) {
			return;
		}

		String mutObjectFragment = EcoreUtil.getRelativeURIFragmentPath(mutatedRoot, mutObject);
		EObject origObject = EcoreUtil.getEObject(originalRoot, mutObjectFragment);

		interProjectMapping.putIfAbsent(origObject, mutObject);
	}

	public void logRemoval(EObject originalScenarioObject) {
		if (originalRoot == null) {
			return;
		}

		// check for valid removal
		if (originalScenarioObject.eContainingFeature() == null) {
			removals.add(originalScenarioObject);
		} else {
			throw new IllegalArgumentException("Object " + originalScenarioObject + " was not removed from its tree.");
		}
	}

	public void logInsertion(EObject mutObject) {
		if (originalRoot == null) {
			return;
		}

		// check for valid removal
		if (mutObject.eContainingFeature() != null) {
			insertions.add(mutObject);
		} else {
			throw new IllegalArgumentException("Object " + mutObject + " was not inserted into the tree.");
		}
	}

	/**
	 * Finds the mutated scenario object using the original counterpart.
	 * 
	 * @param originalScenarioObject
	 * @return mutated version of original scenario object or none if removed as a
	 *         consequence of mutation
	 * @throws UnknownObjectException if the object is not known within this
	 *                                context.
	 */
	public Optional<EObject> getMutated(EObject originalScenarioObject) throws UnknownObjectException {
		if (interProjectMapping.containsKey(originalScenarioObject)) {
			return Optional.ofNullable(interProjectMapping.get(originalScenarioObject));
		} else if (removals.contains(originalScenarioObject)) {
			return Optional.empty();
		} else {
			throw new UnknownObjectException(originalScenarioObject, this);
		}
	}

	/**
	 * Finds the original scenario object using the mutated counterpart.
	 * 
	 * @param mutatedScenarioObject
	 * @return original scenario object from which the mutated version was created
	 *         or none if the mutated object was newly generated.
	 * @throws UnknownObjectException if the object is not known within this
	 *                                context.
	 */
	public Optional<EObject> getOriginal(EObject mutatedScenarioObject) throws UnknownObjectException {
		if (interProjectMapping.inverse().containsKey(mutatedScenarioObject)) {
			return Optional.ofNullable(interProjectMapping.get(mutatedScenarioObject));
		} else if (insertions.contains(mutatedScenarioObject)) {
			return Optional.empty();
		} else {
			throw new UnknownObjectException(mutatedScenarioObject, this);
		}
	}

	public boolean sharesElementsWith(MutationContext other) {
		List<EObject> thisObjs = this.getCtxObjects();
		List<EObject> otherObjs = other.getCtxObjects();

		for (EObject ctxObject : otherObjs) {
			if (thisObjs.contains(ctxObject)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasChangedTreeStructure() {
		return changedTreeStructure;
	}

	public void setChangedTreeStructure(boolean changedTreeStructure) {
		this.changedTreeStructure = changedTreeStructure;
	}

	public int mutatedEObjects() {
		return interProjectMapping.size() + insertions.size() + removals.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutationContext other = (MutationContext) obj;
		if (ctxObjects == null) {
			if (other.ctxObjects != null)
				return false;
		} else if (!EcoreUtil.equals(ctxObjects, other.ctxObjects))
			return false;
		return true;
	}

	/**
	 * Deep copy of mutation context.<br>
	 * <br>
	 * <b>Note</b>: copies the entire containment tree of eobjects.
	 */
	@Override
	public MutationContext clone() {
		MutationContext clone = new MutationContext(originalRoot, mutatedRoot);
		clone.getCtxObjects().clear();
		clone.getCtxObjects().addAll(ctxObjects.stream().map(EcoreUtil::copy).collect(Collectors.toList()));

		return clone;
	}

	public List<MutationPair> getMutationPairs() {
		List<MutationPair> mutPairs = new ArrayList<>();
		interProjectMapping.entrySet().forEach(entry -> mutPairs.add(new MutationPair(entry.getKey(), entry.getValue())));
		removals.forEach(removed -> mutPairs.add(new MutationPair(null, removed)));
		insertions.forEach(inserted -> mutPairs.add(new MutationPair(inserted, null)));
		return mutPairs;
	}

}
