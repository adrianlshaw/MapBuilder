package uni.nav.builder;

import android.location.Location;

public class Node {
 
	private Location location;
	private String buildingName = "";
	private String deptName = "";
	private String code = "";
	private String connections = "";
	private String nodeID;
	private static String newIdPart1 = "A";
	private static int newIdPart2 = 0;
	
	public Node(Location l){
		location = l;
		nodeID = newIdPart1 + newIdPart2;
		newIdPart2++;
		
		if(newIdPart2==Integer.MAX_VALUE-1){
			newIdPart1=newIdPart1+newIdPart1;
		}
	}
	
	public Location getLocation() {
		return location;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}

	public void setConnections(String connections) {
		this.connections = connections;
	}

	public String getConnections() {
		return connections;
	}

	public String getNodeID() {
		return nodeID;
	}
	
}
