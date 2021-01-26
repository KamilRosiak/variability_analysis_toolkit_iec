package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.contribution;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import de.tu_bs.cs.isf.e4cf.core.preferences.interfaces.IPreferencePage;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;

public class MutationPreferencePage implements IPreferencePage {
    private static final String MUATION_PREF_PAGE_FXML = "/view/prefPage.fxml";
	@Override
	public void createPage(CTabFolder parent, ServiceContainer services) {
		CTabItem tab = new CTabItem(parent, SWT.NONE);
		tab.setText("Mutation");
		Composite page = new Composite(parent,SWT.None);
		page.setLayout(new FillLayout(SWT.VERTICAL));
	

        
        tab.setControl(page);
	}

	@Override
	public void store() {
		// TODO Auto-generated method stub
		
	}

}
