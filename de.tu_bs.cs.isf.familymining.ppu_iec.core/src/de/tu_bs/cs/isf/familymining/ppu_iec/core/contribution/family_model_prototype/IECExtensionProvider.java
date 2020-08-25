package de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.family_model_prototype;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.e4cf.core.stringtable.E4CStringTable;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.ExtensionProvider;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationPackage;

public class IECExtensionProvider implements ExtensionProvider {

	@Override
	public String getExtension(EClass eclass) {
		if (EcoreUtil.equals(eclass, ConfigurationPackage.eINSTANCE.getConfiguration())) {
			return E4CStringTable.FILE_ENDING_CONFIGURATION;
		}
		return null;
	}

}
