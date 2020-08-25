package de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.family_model_prototype;

import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.ArtefactFilter;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.languageelement.Comment;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.If;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Expression;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtextexpression.Parameter;

public class IECArtefactFilter implements ArtefactFilter {

	@Override
	public Boolean apply(Object obj) {
		boolean accepted = true;
		accepted &= !Configuration.class.isInstance(obj);
		accepted &= !Comment.class.isInstance(obj);
		
		// ST
		accepted &= !If.class.isInstance(obj);
		
		// expressions
		accepted &= !Expression.class.isInstance(obj);
		accepted &= !Parameter.class.isInstance(obj);
		
		return accepted;
	}

}
