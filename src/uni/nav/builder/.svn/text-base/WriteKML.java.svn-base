package uni.nav.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class WriteKML {

	private File root = Environment.getExternalStorageDirectory();
	private FileWriter writer = null;
	private File file = new File(root, "custom_graph.kml");
	private final String TAB = "        ";
	
	public WriteKML(ArrayList<Node> nodes){

//		Toast.makeText(context, "Writing KML", Toast.LENGTH_SHORT).show();
		
		Log.i("NavMap", "Starting GeoRSS write process");

//		NumberFormat nf = new DecimalFormat("##.########");
		
		if(canWrite()){
			try {
				writer = new FileWriter(file);
//				writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				writer.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n");
				writer.write("<Document>\n");
				writer.write(TAB+TAB+"<Style id=\"yellowLineGreenPoly\"><LineStyle><color>7f00ffff</color><width>4</width></LineStyle><PolyStyle><color>7f00ff00</color></PolyStyle></Style>\n");
				

				for(int i=0; i<nodes.size();i++){
					Log.i("NavMap", "New node");					

					Node node = nodes.get(i);
					if(!node.getConnections().equalsIgnoreCase("")){
						String[] connections = node.getConnections().split(",");
						ArrayList<Location> locations = new ArrayList<Location>();
						for(int j=0;j<connections.length;j++){
							for(int k=0;k<nodes.size();k++){
								Log.i("NavMap", "i="+i+" j="+j+" k="+k);
								if(nodes.get(k).getNodeID().equalsIgnoreCase(connections[j])){
									locations.add(nodes.get(k).getLocation());
									break;
								}
							}
						}
						int idCounter = 0;
						for(Location loc : locations){
							writer.write(TAB+"<Placemark>\n");
							writer.write(TAB+TAB+"<name>"+"Connection_"+node.getNodeID()+"_"+idCounter+"</name>\n");
							idCounter=idCounter+1;
							writer.write(TAB+TAB+"<description></description>\n");
							writer.write(TAB+TAB+"<styleUrl>#yellowLineGreenPoly</styleUrl>\n");
							writer.write(TAB+TAB+"<LineString>\n");
							writer.write(TAB+TAB+"<coordinates>\n");
							writer.write(TAB+TAB+TAB+node.getLocation().getLatitude()+","+node.getLocation().getLongitude()+",0\n");
							writer.write(TAB+TAB+TAB+loc.getLatitude()+","+loc.getLongitude()+",0\n");
							writer.write(TAB+TAB+"</coordinates>\n");
							writer.write(TAB+TAB+"</LineString>\n");
							writer.write(TAB+"</Placemark>\n");
						}
					}
					
					writer.write(TAB+"<Placemark>\n");
					writer.write(TAB+TAB+"<name>"+node.getNodeID()+"</name>\n");
					writer.write(TAB+TAB+"<description>"+node.getBuildingName()+" "+node.getCode()+"</description>\n");
					writer.write(TAB+TAB+"<Point>\n");
					writer.write(TAB+TAB+TAB+"<coordinates>"+node.getLocation().getLongitude()+","+node.getLocation().getLatitude()+",0"+"</coordinates>\n");
					writer.write(TAB+TAB+"</Point>\n");
					writer.write(TAB+"</Placemark>\n");
					writer.flush();

				}
				writer.write("</Document>");
				writer.write("</kml>");
				writer.close();

			} catch (IOException e){
				try {
					writer.close();
				}
				catch(IOException er){
					// already open
				}
			}
		}
		
	}

	public boolean canWrite(){
		if (!root.canWrite()){
			Log.i("NavMap", "Cannot write to file-system");
			return false;
		} else {
			return true;
		}
	}
}