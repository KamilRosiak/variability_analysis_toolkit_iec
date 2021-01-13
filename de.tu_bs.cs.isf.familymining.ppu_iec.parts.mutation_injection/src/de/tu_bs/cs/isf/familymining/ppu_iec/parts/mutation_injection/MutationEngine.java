package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import de.tu_bs.cs.isf.e4cf.core.compare.templates.ResultElement;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.CompareEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.ConfigurationResultRoot;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.model.ModelCompareContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.solution.templates.IECAbstractContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

@Creatable
public class MutationEngine {
	private ServiceContainer services;
	private CompareEngine compareEngine;
	
	@Inject
	public MutationEngine(ServiceContainer services, CompareEngine compareEngine) {
		this.services = services;
	}
	
	
	public void startMutation(Configuration seed) {
		
	}
	
	
	
	private List<IECAbstractContainer<?>> findChanges(ConfigurationResultRoot result) {
		List<IECAbstractContainer<?>> list = new ArrayList<IECAbstractContainer<?>>();
		for(ModelCompareContainer container : result.getChildren()) {
			checkAttributeSimilarity(container.getResults());
	
			
		}
		
		
		
		return list;
	}
	
	private boolean checkAttributeSimilarity(List<ResultElement> list) {
		return true;
	}
	
	
}
