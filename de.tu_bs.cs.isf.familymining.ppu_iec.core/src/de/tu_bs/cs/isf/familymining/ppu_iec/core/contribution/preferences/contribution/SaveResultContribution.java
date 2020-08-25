package de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.preferences.contribution;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.gui.LabeledDecsion;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.familymining.ppu_iec.core.string_table.PPUStringTable;

public class SaveResultContribution {
	public static final String SAVE_AS_RESULT_PREF = "SAVE_RESULT_OPTION";
	public static final String SAVE_AS_RESULT_DESCRIPTION = "Save the result after comparing.";
	private KeyValueNode saveAsValue;
	
	
	public SaveResultContribution(Composite parent) {
		createControl(parent);
	}
	
	private void createControl(Composite parent) {
		saveAsValue =  PreferencesUtil.getValue(PPUStringTable.BUNDLE_NAME, SAVE_AS_RESULT_PREF);
		new LabeledDecsion(parent, SWT.None,SAVE_AS_RESULT_DESCRIPTION , saveAsValue);
	}
	
	public KeyValueNode getKeyValueNode() {
		return saveAsValue;
	}
}
