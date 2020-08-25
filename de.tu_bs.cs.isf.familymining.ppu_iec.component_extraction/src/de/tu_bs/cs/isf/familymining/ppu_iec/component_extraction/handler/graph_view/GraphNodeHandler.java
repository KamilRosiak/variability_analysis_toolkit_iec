package de.tu_bs.cs.isf.familymining.ppu_iec.component_extraction.handler.graph_view;

import de.tu_bs.cs.isf.e4cf.core.util.ServiceContainer;
import de.tu_bs.cs.isf.e4cf.graph.core.elements.model.GraphNode;
import de.tu_bs.cs.isf.e4cf.graph.core.plugin.IGraphHandler;
import javafx.event.EventType;

public class GraphNodeHandler implements IGraphHandler<GraphNode> {

	@Override
	public void execute(GraphNode element, EventType<?> event, ServiceContainer services) {
		System.out.println("I am an graph node example handler");
	}

}
