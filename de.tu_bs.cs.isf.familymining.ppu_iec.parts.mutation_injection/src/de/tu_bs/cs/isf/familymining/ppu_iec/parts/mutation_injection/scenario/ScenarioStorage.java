package de.tu_bs.cs.isf.familymining.ppu_iec.parts.mutation_injection.scenario;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
public class ScenarioStorage {

	private static final String FILE_EXT = "project";
	
	@Inject
	private WorkspaceFileSystem fs;
	
	private Map<String, Configuration> scenarioCache = new HashMap<>(); 
	
	private String mutationDirectory = "MutationInjection";
	
	/**
	 * Searches and loads a scenario by using the resource name. The name is in the .project file at the path "configuration/resources[name]".
	 * The scenario search is only within the workspace of the started application.
	 * 
	 * @param name scenario name
	 * @return scenario configuration or none if not found
	 */
	public Optional<Configuration> loadScenario(String name) {
		Configuration config = scenarioCache.computeIfAbsent(name, this::findScenario);
		return Optional.ofNullable(config);
	}

	/**
	 * Saves the scenario under the given name. Scenarios are stored in the workspace at {@value #MUTATION_DIR_NAME}.
	 * 
	 * @param name
	 * @param scenario
	 * @throws IOException
	 */
	public void saveScenario(Configuration scenario, String name) throws IOException {
		// rename the resource wrapping the scenario
		scenario.getResources().get(0).setName(name);
		
		// ensure the mutation directory is available
		Directory root = fs.getWorkspaceDirectory();
		Optional<FileTreeElement> mutationDirOpt = root.getChildren().stream().filter(dir -> dir.isDirectory() && dir.getAbsolutePath().endsWith(getMutationDirectory())).findAny();
		FileTreeElement mutationDir = mutationDirOpt.orElse(root.create(new CreateSubdirectory(getMutationDirectory())));
		
		EMFModelLoader.save(scenario, FILE_EXT, mutationDir.getAbsolutePath()+"/"+name, FILE_EXT);
	}
	
	public String getName(Configuration config) {
		return config.getResources().get(0).getName();
	}
	
	private Configuration findScenario(String name) {
		Directory root = fs.getWorkspaceDirectory();
		ScenarioFinder scenarioFinder = new ScenarioFinder(name);
		Iterator<FileTreeElement> it = new DepthFirstTreeIterator(root);
		while (it.hasNext()) {
			it.next().accept(scenarioFinder);
		}		
		return scenarioFinder.getScenario();
	}
	
	public String getMutationDirectory() {
		return mutationDirectory;
	}

	public void setMutationDirectory(String mutationDirectory) {
		this.mutationDirectory = mutationDirectory;
	}
	
	private class ScenarioFinder implements TreeVisitor {

		private String targetName;
		private DocumentBuilder docBuilder;
		private Configuration foundScenario;
		
		public ScenarioFinder(String scenarioName) {
			this.targetName = scenarioName;
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			try {
				docBuilder = docBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void visit(File file) {
			if (file.getAbsolutePath().endsWith(FILE_EXT)) {
				Document doc;
				try {
					doc = docBuilder.parse(file.getAbsolutePath());
					XPath xpath = XPathFactory.newInstance().newXPath();
					String pathExpression = "string(/Configuration/resources[1]/@name)";
				
					String scenarioName = (String) xpath.compile(pathExpression).evaluate(doc, XPathConstants.STRING);						
					if (scenarioName.equals(targetName)) {
						Configuration sc = (Configuration) EMFModelLoader.load(file.getAbsolutePath(), file.getExtension());
						foundScenario = sc;						
					}
				} catch (SAXException | IOException e1) {
					e1.printStackTrace(); // doc builder
				} catch (XPathExpressionException e) {
					e.printStackTrace(); // xpath parser
				}
			}
		}

		@Override
		public void visit(Directory directory) {
			
		}
		
		public Configuration getScenario() {
			return foundScenario;
		}
		
	}
}
