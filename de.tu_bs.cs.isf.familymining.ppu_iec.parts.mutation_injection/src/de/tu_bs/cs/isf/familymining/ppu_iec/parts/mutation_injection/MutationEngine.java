package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.CompareEngine;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;

@Creatable
public class MutationEngine {

	
	private ServiceContainer services;
	private CompareEngine compareEngine;
	
	@Inject
	public MutationEngine(ServiceContainer services) {
		this.services = services;
	}
	
	
	public void startMutation(Configuration seed) {
		
	}
	
	
	
	
	

	
	
	
	

	
	
}
