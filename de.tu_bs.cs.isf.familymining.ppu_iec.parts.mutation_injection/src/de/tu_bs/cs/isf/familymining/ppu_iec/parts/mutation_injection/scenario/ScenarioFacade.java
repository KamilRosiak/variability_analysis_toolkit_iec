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
public class ScenarioFacade {

	private static final String FILE_EXT = "project";
	private static final String MUTATION_DIR_NAME = "MutationInjection";
	
	@Inject
	WorkspaceFileSystem fs;
	
	Map<String, Configuration> scenarioCache = new HashMap<>(); 
	
	public Optional<Configuration> loadScenario(String name) {
		Configuration config = scenarioCache.computeIfAbsent(name, (scenarioName) -> findScenario(scenarioName));
		return Optional.ofNullable(config);
	}

	public void saveScenario(String name, Configuration scenario) throws IOException {
		// ensure the mutation directory is available
		Directory root = fs.getWorkspaceDirectory();
		Optional<FileTreeElement> mutationDirOpt = root.getChildren().stream().filter(dir -> dir.isDirectory() && dir.getAbsolutePath().endsWith(MUTATION_DIR_NAME)).findAny();
		FileTreeElement mutationDir = mutationDirOpt.orElse(root.create(new CreateSubdirectory(MUTATION_DIR_NAME)));
		
		EMFModelLoader.save(scenario, FILE_EXT, mutationDir.getAbsolutePath()+"/"+name, FILE_EXT);
	}
	
	public String getMutantDirectoryName() {
		return MUTATION_DIR_NAME;
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
