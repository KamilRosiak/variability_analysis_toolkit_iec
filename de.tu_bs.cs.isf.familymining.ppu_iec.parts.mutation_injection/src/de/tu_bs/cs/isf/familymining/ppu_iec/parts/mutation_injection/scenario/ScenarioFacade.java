package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.tu_bs.cs.isf.e4cf.core.file_structure.FileTreeElement;
import de.tu_bs.cs.isf.e4cf.core.file_structure.WorkspaceFileSystem;
import de.tu_bs.cs.isf.e4cf.core.file_structure.components.Directory;
import de.tu_bs.cs.isf.e4cf.core.file_structure.components.File;
import de.tu_bs.cs.isf.e4cf.core.file_structure.components.operations.CreateSubdirectory;
import de.tu_bs.cs.isf.e4cf.core.file_structure.tree.util.DepthFirstTreeIterator;
import de.tu_bs.cs.isf.e4cf.core.file_structure.tree.util.TreeVisitor;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.rcp_e4.EMFModelLoader.impl.EMFModelLoader;

/**
 * Scenario in-/output based on the scenario resource name as specified in the PPU IEC project xml.
 * 
 * @author Oliver Urbaniak
 *
 */
@Singleton
@Creatable
public class ScenarioFacade {

	private static final String FILE_EXT = ".project";
	private static final String MUTATION_DIR_NAME = "MutationInjection";
	
	@Inject
	WorkspaceFileSystem fs;
	
	Map<String, Configuration> scenarioByName = new HashMap<>(); 
	
	@PostConstruct
	public void setupScenarios() {
		Directory root = fs.getWorkspaceDirectory();
		TreeVisitor scenarioExtractor = new ScenarioExtractor();
		Iterator<FileTreeElement> it = new DepthFirstTreeIterator(root);
		while (it.hasNext()) {
			it.next().accept(scenarioExtractor);
		}
	}
	
	public Optional<Configuration> loadScenario(String name) {
		return Optional.ofNullable(scenarioByName.get(name));
	}
	
	
	public void saveScenario(String name, Configuration scenario) throws IOException {
		// ensure the mutation directory is available
		Directory root = fs.getWorkspaceDirectory();
		Optional<FileTreeElement> mutationDirOpt = root.getChildren().stream().filter(dir -> dir.isDirectory() && dir.getAbsolutePath().endsWith(MUTATION_DIR_NAME)).findAny();
		FileTreeElement mutationDir = mutationDirOpt.orElse(root.create(new CreateSubdirectory(MUTATION_DIR_NAME)));
		
		scenario.getResources().get(0).setName(name);
		EMFModelLoader.save(scenario, FILE_EXT, mutationDir.getAbsolutePath(), FILE_EXT);
	}
	
	public String getMutantDirectoryName() {
		return MUTATION_DIR_NAME;
	}
	
	private class ScenarioExtractor implements TreeVisitor {

		@Override
		public void visit(File file) {
			try {
				Configuration sc = (Configuration) EMFModelLoader.load(file.getAbsolutePath(), file.getExtension());
				String scenarioName = sc.getResources().get(0).getName();
				scenarioByName.putIfAbsent(scenarioName, EcoreUtil.copy(sc));
			} catch (Exception e) {
				 
			}
		}

		@Override
		public void visit(Directory directory) {
			
		}
		
	}
}
