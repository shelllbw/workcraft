package org.workcraft.plugins.son.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.workcraft.dom.Node;
import org.workcraft.plugins.son.SON;
import org.workcraft.plugins.son.elements.Time;
import org.workcraft.plugins.son.gui.TimeConsistencyDialog.Granularity;
import org.workcraft.plugins.son.util.Interval;
import org.workcraft.plugins.son.util.ScenarioRef;


public class EnhancedEstimationAlg extends EstimationAlg{

	protected SON net;
	
	public EnhancedEstimationAlg(SON net, Interval d, Granularity g, ScenarioRef s) {
		super(net, d, g, s);		
	}
	
	public void estimateFinish(Node n){
		//nearest right nodes of n with specified finish time intervals
		Collection<Time> rBoundary = new ArrayList<Time>();
		// nodes on paths from n to RBoundary nodes
		Collection<Time> rNeighbourhood = new ArrayList<Time>();
		rNeighbourhood.add((Time)n);
		
		if(scenario != null ){
			findRightBoundary((Time)n, rBoundary, rNeighbourhood);
		}
	}
	
	private void findRightBoundary (Time n, Collection<Time> boundary, Collection<Time> neighbourhood){
		Collection<Node> nodes = scenario.getNodes(net);
		// nodes used for forward boundary searching
		List<Time> working = new ArrayList<Time>();
		working.add(n);
		while(working.isEmpty()){
			List<Time> nextWorking = new ArrayList<Time>();
			for (Time t : working){
				Collection<Time> postset = getCausalPostset(t, nodes);
				if(postset.isEmpty()){
					boundary.add(t);
				}else{
					for(Time nt : postset){
						neighbourhood.add(nt);
						if(nt.getEndTime().isSpecified()){
							boundary.add(nt);
						}else{
							nextWorking.add(nt);
						}
					}
				}
			}
			working = nextWorking;
		}
	}
	
	

}
