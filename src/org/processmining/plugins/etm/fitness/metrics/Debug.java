package org.processmining.plugins.etm.fitness.metrics;

import java.util.List;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.fitness.TreeFitnessAbstract;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo;
import org.processmining.plugins.etm.fitness.TreeFitnessInfo.Dimension;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.conversion.ProcessTree2Petrinet;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.InvalidProcessTreeException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.NotYetImplementedException;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.PetrinetWithMarkings;
import org.processmining.processtree.test.TestProcessTree;

public class Debug extends TreeFitnessAbstract {

	public static final TreeFitnessInfo info = new TreeFitnessInfo(Debug.class, "debug", "For debugging",
			"This metric for debug",
			Dimension.PRECISION, true);
	private CentralRegistry registry;

	public Debug(CentralRegistry registry) {
		this.registry = registry;
	}

	public Debug(Debug original) {
		this.registry = original.registry;
	}
	
  
    

	@SuppressWarnings("deprecation")
	public double getFitness(NAryTree candidate, List<? extends NAryTree> population) {
		ProcessTree processtree = TestProcessTree.getExampleTree();
//		System.out.println("The process tree is: " + processtree.toString()); 
		PluginContext context = this.registry.getContext();

		
		//convert the pt to petri net and export them
		PetrinetWithMarkings examplePetriM = new PetrinetWithMarkings();
		Object[] petriNetMarkings;
		try {
			examplePetriM = ProcessTree2Petrinet.convert(processtree, true);
			context.addConnection(new InitialMarkingConnection(examplePetriM.petrinet, examplePetriM.initialMarking));
			context.addConnection(new FinalMarkingConnection(examplePetriM.petrinet, examplePetriM.finalMarking));
			petriNetMarkings = new Object[]{examplePetriM.petrinet, examplePetriM.initialMarking, examplePetriM.finalMarking };
			System.out.println("The petrinet is: " + examplePetriM.petrinet); 

		} catch (NotYetImplementedException e) {
			// TODO Auto-generated catch block
			System.out.println("NotYetImplementedException");
		} catch (InvalidProcessTreeException e) {
			// TODO Auto-generated catch block
			System.out.println("InvalidProcessTreeException");
		}
		return candidate.size();
	}
	
	public TreeFitnessInfo getInfo() {
		return info;
	}
}
