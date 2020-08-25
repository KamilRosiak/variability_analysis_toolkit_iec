package de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.handler.graph_view;

import de.tu_bs.cs.isf.e4cf.core.file_structure.util.Pair;
import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.e4cf.graph.core.elements.model.GraphEdge;
import de.tu_bs.cs.isf.e4cf.graph.core.plugin.IGraphHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;

public class ShowFMOnEdgeSelectionHandler implements IGraphHandler<GraphEdge> {
	
	@Override
	public void execute(GraphEdge edge, EventType<?> event, ServiceContainer services) {
		if (event.getName().equals(MouseEvent.MOUSE_CLICKED.getName())) {
			Pair<String, String> pouPair = new Pair<>(edge.getSource().getTitle(), edge.getTarget().getTitle());
			
			// TODO: remove hard code, instead create an extension point for registering selection handlers from outside
			services.partService.getPart("de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.part.ProjectCompare").setToBeRendered(true);
			services.eventBroker.send("SHOW_POU_PAIR_AS_FAMILY_MODEL", pouPair);			
		}
		
	}

}
