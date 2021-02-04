package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

import java.util.List;
import java.util.Random;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.OrganizationType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;

@Creatable
@Singleton
public class POUGenerator {
	private static final ConfigurationFactory configFac = ConfigurationFactory.eINSTANCE;
	
	/**
	 * Returns a generated POU without implementation.
	 * @return
	 */
	public static POU generateRandomPOU() {
		POU pou = configFac.createPOU();
		pou.setIdentifier(StringGenerator.getRandomStringOfLenght(50));
		pou.setType(getRandomOrganizationType());
		pou.setReturnType(getRandomElementaryDataType());
		pou.setDeclaration(configFac.createDeclaration());
		return pou;
	}

	/**
	 * Returns a random Organization Type
	 * 
	 * @return
	 */
	public static OrganizationType getRandomOrganizationType() {
		List<OrganizationType> values = OrganizationType.VALUES;
		Random r = new Random();
		return values.get(r.nextInt(values.size()));
	}

	/**
	 * Returns a random Organization Type
	 * 
	 * @return
	 */
	public static ElementaryDataType getRandomElementaryDataType() {
		List<ElementaryDataType> values = ElementaryDataType.VALUES;
		Random r = new Random();
		return values.get(r.nextInt(values.size()));
	}

}
