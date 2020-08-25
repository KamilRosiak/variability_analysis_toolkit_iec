package de.tu_bs.cs.isf.familymining.ppu_iec.core.util;

import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.MetricContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.compare.metric.util.MetricContainerSerializer;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution.DefaultSettingContribution;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUStringTable;

public class IECCompareUtil {
	
	/**
	 * This method returns the default metric if defined else null;
	 */
	public static MetricContainer getDefaultMetric() {
		KeyValueNode metricValue = PreferencesUtil.getValueWithDefault(PPUStringTable.BUNDLE_NAME, DefaultSettingContribution.DEFAULT_METRIC_PREF, "");
		MetricContainer metric = MetricContainerSerializer.decode(metricValue.getStringValue());
		return metric;
	}
}
