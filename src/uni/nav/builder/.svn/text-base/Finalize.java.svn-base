package uni.nav.builder;

import java.util.ArrayList;

import android.location.Location;

public class Finalize {
	
	private double RANGE = 7; // in metres
	
	public Finalize(double range){
		// empty constructor
	}
	
	public void connectGraph(ArrayList<Node> nodes){
		
		for(int i=0; i<nodes.size();i++){
			
			Location a = nodes.get(i).getLocation();
			
			for(int j=0; j<nodes.size();j++){
			
				Location b = nodes.get(j).getLocation();
				
				if(a.distanceTo(b)<RANGE && i!=j){
					// make connection between a and b
					String s = "";
					s = nodes.get(i).getConnections();
					if(s.equals("")){
						nodes.get(i).setConnections(nodes.get(j).getNodeID());
					}
					else {
						s = s + "," + nodes.get(j).getNodeID();
						nodes.get(i).setConnections(s);
					}
				}
			}
		}
	}
}