package de.tu_bs.isf.familymining.ppu_iec.export.factories;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.isf.familymining.ppu_iec.export.xsd_objects.Project.Types.Pous.Pou;

/**
 * {@code <action>..</action>} factory.
 */
@Creatable
@Singleton
public class ActionFactory {

	/**
	 * {@code <body>..</body>} factory.
	 */
	@Inject
	BodyFactory bodyFactory;
	
	public Pou.Actions.Action createAction(Action action) {
		Pou.Actions.Action actionInstance = new Pou.Actions.Action();
		actionInstance.setName(action.getName());
		actionInstance.setBody(bodyFactory.createBody(action.getImplementation()));
		
		return actionInstance;
		
	}
}
