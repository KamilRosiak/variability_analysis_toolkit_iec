package de.tu_bs.cs.isf.familymining.ppu_iec.core.transformation;

import org.eclipse.swt.widgets.TreeItem;

import FeatureDiagram.FeatureDiagramm;
import de.tu_bs.cs.isf.e4cf.core.transform.Transformation;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.util.FamilyModelTransformUtil;

public class LegacyConfigurationResultToFeatureModelTransform implements Transformation<FeatureDiagramm> {

	@Override
	public FeatureDiagramm apply(Object rootItemObject) {
		if (rootItemObject instanceof TreeItem) {
			TreeItem rootItem = (TreeItem) rootItemObject;
			return FamilyModelTransformUtil.transformToFeatureDiagram(rootItem);			
		}
		return null;
	}

	@Override
	public boolean canTransform(Object object) {
		return object != null && object instanceof TreeItem;
	}

}
