package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.generators;

import java.util.Random;

import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.mutation.Randomization;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ConfigurationFactory;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.VariableDeclaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.ElementaryDataType;
/**
 * Helper class for the generation of variables 
 * @author Kamil Rosiak
 *
 */
public class VariableGenerator {
	private static ConfigurationFactory factory = ConfigurationFactory.eINSTANCE;
	private static Random r = new Randomization();
	
	public static Variable generateVariable() {
		Variable var = factory.createVariable();
		var.setName(StringGenerator.getRandomStringOfLenght(10));
		var.setType(getRandomElementaryDataType());
		var.setScope(getRandomVariableDeclaration());
		return var;
	}
	
	/**
	 * returns a random ElementaryDataType
	 */
	public static ElementaryDataType getRandomElementaryDataType() {
		return ElementaryDataType.VALUES.get(r.nextInt(ElementaryDataType.VALUES.size()));
	}
	/**
	 * returns a random VariableDeclaration
	 */
	public static VariableDeclaration getRandomVariableDeclaration() {
		return VariableDeclaration.VALUES.get(r.nextInt(VariableDeclaration.VALUES.size()));
	}

}
