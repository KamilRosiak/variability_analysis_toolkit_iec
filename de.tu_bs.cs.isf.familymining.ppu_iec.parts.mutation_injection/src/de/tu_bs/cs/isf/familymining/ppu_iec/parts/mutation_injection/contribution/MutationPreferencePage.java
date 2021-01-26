package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.contribution;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tu_bs.cs.isf.e4cf.core.preferences.interfaces.IPreferencePage;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.PreferencesUtil;
import de.tu_bs.cs.isf.e4cf.core.preferences.util.key_value.KeyValueNode;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenarios.MutationST;

public class MutationPreferencePage implements IPreferencePage {
	private KeyValueNode numberRunNode;
	private KeyValueNode numberMutationsNode;
	private int DEFAUL_REPLAY_PERIOD = 100;

	@Override
	public void createPage(CTabFolder parent, ServiceContainer services) {
		CTabItem tab = new CTabItem(parent, SWT.NONE);
		tab.setText("Mutation");
		Composite page = new Composite(parent, SWT.None);
		page.setLayout(new GridLayout(1, false));
		tab.setControl(page);

		numberRunNode = PreferencesUtil.getValueWithDefault(MutationST.BUNDLE_NAME, MutationST.NUMBER_RUNS_PREF,
				DEFAUL_REPLAY_PERIOD);
		createKeyValuePairView("Number of Mutation Evaluation Runs", page, numberRunNode);
		numberMutationsNode = PreferencesUtil.getValueWithDefault(MutationST.BUNDLE_NAME,
				MutationST.NUMBER_MUTATIONS_PREF, DEFAUL_REPLAY_PERIOD);
		createKeyValuePairView("Number of Mutations per Run", page, numberMutationsNode);

	}

	private void createKeyValuePairView(String label, Composite page, KeyValueNode keyValue) {
		Group grp = new Group(page, SWT.NONE);
		grp.setLayout(new GridLayout(2, false));

		Label replayPeriodLabel = new Label(grp, SWT.NONE);
		replayPeriodLabel.setText(label);
		replayPeriodLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Text replayPeriodText = new Text(grp, SWT.SINGLE | SWT.BORDER);
		replayPeriodText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		replayPeriodText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		replayPeriodText.setText(keyValue.getStringValue());
		replayPeriodText.addListener(SWT.SELECTED, e -> {
			try {
				keyValue.setValue(Integer.parseInt(replayPeriodText.getText()));
			} catch (Exception e2) {
				System.out.println("not a number");
			}

		});
	}

	@Override
	public void store() {
		PreferencesUtil.storeKeyValueNode(numberRunNode);
		PreferencesUtil.storeKeyValueNode(numberMutationsNode);
	}

}
