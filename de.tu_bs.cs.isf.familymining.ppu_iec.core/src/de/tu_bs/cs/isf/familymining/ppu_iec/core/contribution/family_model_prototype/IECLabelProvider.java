package de.tu_bs.cs.isf.familymining.ppu_iec.core.contribution.family_model_prototype;

import de.tu_bs.cs.isf.e4cf.core.transform.Transformation;
import de.tu_bs.cs.isf.e4cf.family_model_view.prototype.LabelProvider;
import de.tu_bs.cs.isf.familymining.ppu_iec.code_gen.st.StructuredTextToStringExporter;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Action;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.ArrayVariable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Configuration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Declaration;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Location;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.POU;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Resource;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Struct;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Task;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.configuration.Variable;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Diagram;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.DiagramType;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.FBPort;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Jump;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Network;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Port;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.diagram.Return;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.functionblockdiagram.FBDElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.ladderdiagram.LLElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.languageelement.Comment;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.languageelement.LanguageElement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.ComplexAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SequentialFunctionChart;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.SimpleAction;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Step;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.StepQualifier;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.sequentialfunctionchart.Transition;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Case;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.CaseBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.ConditionalBlock;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.Statement;
import de.tu_bs.cs.isf.familymining.ppu_iec.ppuIECmetaModel.structuredtext.StructuredText;

public class IECLabelProvider implements LabelProvider {

	private Transformation<String> STConverter = new StructuredTextToStringExporter();
	
	public IECLabelProvider() {
		
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof Configuration) {
			return "Configuration("+((Configuration) object).getIdentifier()+")";
		} else if (object instanceof Resource) {
			return "Resource("+((Resource) object).getName()+")";
		} else if (object instanceof ArrayVariable) {
			return "Variable("+((ArrayVariable) object).getName()+"[])";
		} else if (object instanceof Variable) {
			return "Variable("+((Variable) object).getName()+")";
		} else if (object instanceof POU) {
			return "POU("+((POU) object).getIdentifier()+")";
		} else if (object instanceof Task) {
			return "Task("+((Task) object).getName()+")";
		} else if (object instanceof Action) {
			return "Action("+((Action) object).getName()+")";
		} else if (object instanceof Struct) {
			return "Struct("+((Struct) object).getName()+")";
		} else if (object instanceof Declaration) {
			return "Declaration";
		} else if (object instanceof Comment) {
			return "Comments";
		} else if (object instanceof Location) {
			return "Location("+((Location) object).getType()+", "+((Location) object).getDataType()+")";
		}
		
		// Diagram -> FBD & LD
		if (object instanceof Diagram) {
			if (((Diagram) object).getType() == DiagramType.LADDER_DIAGRAM) {
				return "Implementation: LD("+((Diagram) object).getLabel()+")";
			} else {				
				return "Implementation: FBD("+((Diagram) object).getLabel()+")";
			}
		} else if (object instanceof Network) {
			return ((Network) object).getLabel();
		} else if (object instanceof Jump) {
			return "Jump("+((Jump) object).getJumpLabel()+") to "+((Jump) object).getLocalId();
		} else if (object instanceof Return) {
			return "Return to POU "+((Return) object).getTargetPOU().getIdentifier();
		} else if (object instanceof FBPort) {
			return "Port("+((FBPort) object).getPortVariable().getSymbol()+")";
		} else if (object instanceof Port) {
			return "Port";
		} else if (object instanceof FBDElement) {
			FBDElement fbdElement = (FBDElement) object;
			return fbdElement.getFBDElementType().toString()+"("+fbdElement.getName()+")";
		} else if (object instanceof LLElement) {
			LLElement llElement = (LLElement) object;
			return llElement.getTransitElementType().toString()+"("+llElement.getLabeledVariable().getSymbol()+", "+ llElement.getStatus().toString()+")";
		} 
		
//		 else if (object instanceof FBDPOU) {
//				return ((FBDPOU) object).getName();
//			} else if (object instanceof StandardFunction) {
//				return ((StandardFunction) object).getName();
//			} else if (object instanceof StandardFunctionBlock) {
//				return ((StandardFunctionBlock) object).getName();
//			} else if (object instanceof ExecutionBlock) {
//				return ((StandardFunction) object).getName();
//			} 
		
		// SFC
		if (object instanceof SequentialFunctionChart) {
			return "Implementation: SFC("+((SequentialFunctionChart) object).getLabel()+")"; 
		} else if (object instanceof Step) {
			Step step = (Step) object;
			if (step.getQualifier() == StepQualifier.UNDEFINED) {
				return "Step("+((Step) object).getName()+")"; 				
			} else {
				return "Step("+((Step) object).getName()+", "+((Step) object).getQualifier().toString()+")"; 
			}
		} else if (object instanceof Transition) {
			return "Transition("+((Transition) object).getLocal_ID()+", "+((Transition) object).getCondition()+")"; 
		} else if (object instanceof SimpleAction) {
			return "Action("+((SimpleAction) object).getActionVariable().getName()+")"; 
		} else if (object instanceof ComplexAction) {
			return "POU Action("+((ComplexAction) object).getPouAction().getName()+")"; 
		} else if (object instanceof SequentialFunctionChart) {
			return ((SequentialFunctionChart) object).getLabel(); 
		}
		
		// ST
		if (object instanceof StructuredText) {
			return "Implementation: ST("+((StructuredText) object).getLabel()+")";
		} else if (object instanceof Statement) {
			String text = STConverter.apply(object);
			int cutIndex = text.indexOf("\n");
			if (cutIndex < 0) {
				cutIndex = text.length();
			}
			return text.substring(0, cutIndex).trim();
		}
		
		return "<no label>";
	}
}
