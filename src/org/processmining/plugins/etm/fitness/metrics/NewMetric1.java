package org.processmining.plugins.etm.fitness.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.fitness.FitnessAnnotation;
import org.processmining.plugins.etm.fitness.TreeFitnessAbstract;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo.Dimension;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.pnml.exporting.PnmlExportNetToPNML;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.conversion.ProcessTree2Petrinet;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.InvalidProcessTreeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.NotYetImplementedException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.PetrinetWithMarkings;
@FitnessAnnotation
public class NewMetric1 extends TreeFitnessAbstract {
	public static final TreeFitnessInfo info = new TreeFitnessInfo(NewMetric1.class, "MonoR", "Monotonic Recall Measure",
			"This metric can call a Monotonic Measures of Recall",
			Dimension.FITNESS, true);
	private CentralRegistry registry;

	public NewMetric1(CentralRegistry registry) {
		this.registry = registry;
	}

	public NewMetric1(NewMetric1 original) {
		this.registry = original.registry;
	}
	
	
    private static String getLegalName(String prefix, double random) throws IOException {
        String tempDirectory = prefix + random;
        Path p = Paths.get(tempDirectory);
        String legalFileName = tempDirectory;
        while(Files.exists(p)) {
            legalFileName = getLegalName(prefix, Math.random());
            p = Paths.get(tempDirectory);
        }
        return legalFileName;
    }
    
    

	@SuppressWarnings("deprecation")
	public double getFitness(NAryTree candidate, List<? extends NAryTree> population) {
		//1. get a valid export name
		final String prefix = "src\\org\\processmining\\plugins\\etm\\fitness\\metrics\\eva_source\\PetriNet_";
		final String dataset = "src\\org\\processmining\\plugins\\etm\\fitness\\metrics\\eva_source\\BPI_Challenge_2012.xes";
		double code = Math.random();
		String legalPetriNet = "";
		
		try {
			legalPetriNet = getLegalName(prefix,code);
			//debug
//			System.out.println("The legal name is: " + legalPetriNet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//convert N-ary tree to process tree
		PluginContext context = this.registry.getContext();
		ProcessTree candidatePT= NAryTreeToProcessTree.convert(candidate,this.registry.getEventClasses());
		
		//convert the pt to petri net and export them
		PetrinetWithMarkings result = new PetrinetWithMarkings();
		Object[] petriNetMarkings;
		String petriFileName = "";
		try {
			result = ProcessTree2Petrinet.convert(candidatePT, false);
			context.addConnection(new InitialMarkingConnection(result.petrinet, result.initialMarking));
			context.addConnection(new FinalMarkingConnection(result.petrinet, result.finalMarking));
			petriNetMarkings = new Object[]{result.petrinet, result.initialMarking, result.finalMarking };
//			System.out.println(petriNetMarkings[1].toString().equals("")); 
//			System.out.println(result.toString());
			File f = new File(legalPetriNet);
			PnmlExportNetToPNML exporter = new PnmlExportNetToPNML();
			exporter.exportPetriNetToPNMLFile(context, result.petrinet, f);
			petriFileName = legalPetriNet+".pnml";
		} catch (NotYetImplementedException e) {
			// TODO Auto-generated catch block
			System.out.println("NotYetImplementedException");
		} catch (InvalidProcessTreeException e) {
			// TODO Auto-generated catch block
			System.out.println("InvalidProcessTreeException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//use entropia to calculate overall fitness
		
      String system = dataset;
      String model = "src\\org\\processmining\\plugins\\etm\\fitness\\metrics\\eva_source\\" + petriFileName;
      double rf = 0;
      double precession = 0;
      try {
      	 rf = Double.parseDouble(NewMetric.GetModelSystemRecall(model, system));
//      	 precession = Double.parseDouble(NewMetric.GetModelSystemPrecision(model, system));
      } catch (Exception e) {
//      	System.out.println(e.getMessage());
      }
//      return (precession+rf)/2;
		return rf;
	}
	


	public TreeFitnessInfo getInfo() {
		return info;
	}
//	
	public static String execCmd(String cmd) throws java.io.IOException {
	    java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	
	public static String GetModelSystemRecall(String model, String system) throws IOException {
	return NewMetric.execCmd("java -jar jbpt-pm-entropia-1.6.jar -emr -rel="+system+" -ret="+model+" -s -t");
	}
	
	public static String GetModelSystemPrecision(String model, String system) throws IOException {
	return NewMetric.execCmd("java -jar jbpt-pm-entropia-1.6.jar -emp -rel="+system+" -ret="+model+" -s -t");
	}
	
	public static String GetModelLogRecall(String model, String log) throws IOException {
	return NewMetric.execCmd("java -jar jbpt-pm-entropia-1.6.jar -emr -rel="+log+" -ret="+model+" -s -t");
	}
	
	public static String GetModelLogPrecision(String model, String log) throws IOException {
		return NewMetric.execCmd("java -jar jbpt-pm-entropia-1.6.jar -emp -rel="+log+" -ret="+model+" -s -t");
		}



}
