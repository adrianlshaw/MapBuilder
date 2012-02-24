package uni.nav.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class WriteXML {

	private FileWriter writer = null;
	private File root = Environment.getExternalStorageDirectory();
	private File file = new File(root, "custom_graph.xml");

	public WriteXML(ArrayList<Node> nodes) {

		Log.i("NavMap", "Starting XML write process");

		NumberFormat nf = new DecimalFormat("##.########");

		if (canWrite()) {
			
						
			try {
				writer = new FileWriter(file);
				writer.write("<nodes>");

				int counter = 0;
				for (Node n : nodes) {
					writer.write("<node ");
					writer.write("name=\"" + n.getNodeID() + "\" ");

					double geolat = n.getLocation().getLatitude();
					double geolong = n.getLocation().getLongitude();

					String strGeoLat = nf.format(geolat);
					String strGeoLon = nf.format(geolong);

					writer.write("geolat=\"" + strGeoLat + "\" ");
					writer.write("geolong=\"" + strGeoLon + "\" ");

					if (!n.getBuildingName().equalsIgnoreCase("")) {
						writer.write("buildingname=\"" + n.getBuildingName()
								+ "\" ");
					}
					if (!n.getCode().equalsIgnoreCase("")) {
						writer.write("code=\"" + n.getCode() + "\" ");
					}
					if (!n.getDeptName().equalsIgnoreCase("")) {
						writer.write("deptname=\"" + n.getDeptName() + "\" ");
					}
					if (!n.getConnections().equalsIgnoreCase("")) {
						writer.write("connectedto=\"" + n.getConnections()
								+ "\" ");
					}

					writer.write(" />\n");
					writer.flush();
					Log.i("NavMap", "Log4");

				}

				writer.write("\n</nodes>");
				Log.i("NavMap", "XML wrote");

			} catch (IOException e) {
				Log.i("NavMap", "IOException:" + e.getMessage());
			}

			finally {
				try {
					writer.close();
				} catch (IOException e) {
				} catch (NullPointerException en) {
				}
			}
		}
	}

	public boolean canWrite() {
		if (!root.canWrite()) {
			Log.i("NavMap", "Cannot write to file-system");
			return false;
		} else {
			return true;
		}
	}

	public void writeHeader() {
		if (canWrite()) {
			try {
				writer = new FileWriter(file);
				writer.write("<nodes>\n");
				writer.close();

			} catch (IOException e) {
			}
		}
	}

	public void writeFooter() {
		if (canWrite()) {
			try {
				writer = new FileWriter(file);
				writer.write("</nodes>\n");
				writer.close();

			} catch (IOException e) {
			}
		}
	}

	public void writeNodes(ArrayList<Node> nods) {

		NumberFormat nf = new DecimalFormat("##.########");
		try {
			writer = new FileWriter(file);
			for (Node n : nods) {
				writer.write("<node ");
				writer.write("name=\"" + n.getNodeID() + "\" ");

				double geolat = n.getLocation().getLatitude();
				double geolong = n.getLocation().getLongitude();

				String strGeoLat = nf.format(geolat);
				String strGeoLon = nf.format(geolong);

				writer.write("geolat=\"" + strGeoLat + "\" ");
				writer.write("geolong=\"" + strGeoLon + "\" ");

				if (!n.getBuildingName().equalsIgnoreCase("")) {
					writer.write("buildingname=\"" + n.getBuildingName()
							+ "\" ");
				}
				if (!n.getCode().equalsIgnoreCase("")) {
					writer.write("code=\"" + n.getCode() + "\" ");
				}
				if (!n.getDeptName().equalsIgnoreCase("")) {
					writer.write("deptname=\"" + n.getDeptName() + "\" ");
				}
				if (!n.getConnections().equalsIgnoreCase("")) {
					writer.write("connectedto=\"" + n.getConnections() + "\" ");
				}
				writer.write(" />\n");
				writer.flush();
			}

			writer.close();
		} catch (IOException e) {
		}
	}
}