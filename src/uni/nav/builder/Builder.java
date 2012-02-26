package uni.nav.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Builder extends Activity implements LocationListener, Listener {
    
	/*
	 * Some UI components
	 */
	private TextView text, nodeCount;
	private LocationManager manager;
	private ProgressBar progressBar;
	private TextView console;
	private Button startButton, stopButton, addPlaceButton;
	public static int MAX_NODES = 300; // the maximum number of nodes
					   // to be recorded.
	
	/*
	 * Some default values in metres.
	 */
	private static int MIN_DISTANCE = 5;
	private static int MAX_DISTANCE = 30;
	private static int CONNECT_RANGE = 6;
	
	Context context = this;
	
	private ArrayList<Node> nodeArray = new ArrayList<Node>();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        fetchPreferences();
        setupViews();
    }
	/*
	 * Retrieves application preferences from the SharedPreferences
	 * file on the filesystem.   
	 */

    public void fetchPreferences(){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	String range = preferences.getString("range_preference", "6");
    	CONNECT_RANGE = Integer.valueOf(range);
    	
    	String mindistance = preferences.getString("min_plot_distance_preference", "5");
    	MIN_DISTANCE = Integer.valueOf(mindistance);
    	
    	String maxnodes = preferences.getString("max_nodes_preference", "300");
    	MAX_NODES = Integer.valueOf(maxnodes);
    	Log.i("NavMap", "Preference RMinMax:"+range+mindistance+maxnodes);
    }
    
	/*
	 * This sets up the graphical interface and its listeners.
	 */
    public void setupViews(){
        text = (TextView) findViewById(R.id.builder_lastlocation);
        progressBar = (ProgressBar) findViewById(R.id.builder_progress_bar);
        progressBar.setMinimumWidth(50);
        progressBar.setMinimumHeight(50);
    	progressBar.setVisibility(ProgressBar.INVISIBLE);
        console = (TextView) findViewById(R.id.console);
        console.setTextColor(Color.GREEN);
        console.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        startButton = (Button) findViewById(R.id.button1);
        stopButton = (Button) findViewById(R.id.button2);
        addPlaceButton = (Button) findViewById(R.id.button3);
        nodeCount = (TextView) findViewById(R.id.nodeCount);
        addPlaceButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        Button forceAddPoint = (Button) findViewById(R.id.forceAddPoint);
	// Force Add Point button
        forceAddPoint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(lastLocation!=null){
					addLocation(lastLocation);
					Toast.makeText(context, "Node added", Toast.LENGTH_SHORT).show();					
				}
			}
        });
        // Start Button
        startButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startButton.setEnabled(false);
				requestLocation();
				stopButton.setEnabled(true);
			}
		});       
        final Context context = this;
	// Add Place Button
        addPlaceButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final Location theLastLocation = lastLocation;
				if(theLastLocation!=null){
					AddPoiDialog dialog = new AddPoiDialog(context, theLastLocation, nodeArray);
					dialog.show();					
				}							
			}
		});
	// Stop Button
        stopButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				stopLocationUpdates();
				firstTime = true;
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				writeToXML();
			}


        });
    }
    
    public void stopLocationUpdates(){
    	try{
        	manager.removeUpdates(this);
    	}
    	catch(NullPointerException e) {
			Toast.makeText(this, ""+e.toString()+" "+e.getMessage(), Toast.LENGTH_LONG).show();    		
    	}
    	progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
    
    public void requestLocation(){
    	progressBar.setVisibility(ProgressBar.VISIBLE);
    	text.setText("GPS Status: Not ready");        
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        Location a = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if(a!=null){
            Toast.makeText(this, "Last location: "+a.getLatitude()+" "+a.getLongitude(), 
            		Toast.LENGTH_SHORT);        	
        } 	
    }
    
    private Location lastLocation;
    private boolean firstTime = true;
    private int locationCount = 0;
    boolean preciseEnough = false;
	
	/*
	 * GPS takes a while to triangulate your location, so it is necessary to ignore
	 * the first several readings, which are typically broad and inaccurate. This is
	 * why there is a variable called preciseEnough. Once this is true, it is acceptable
	 * to accept readings for the rest of the Activity lifetime.
	 */    
	@Override
	public void onLocationChanged(Location location) {
		
		final int WAIT_FOR_N_LOCATIONS = 5;
				
		if(preciseEnough==true){
			
			if(firstTime){
				firstTime = false;
				lastLocation = location;
		        addPlaceButton.setEnabled(true);
		        nodeArray.add(new Node(location));
				nodeCount.setText("Node count: "+nodeArray.size());
				updateConsoleView(location);				
				return ;
			}
			updateConsoleView(location);
			
			/*
			 * If there are more nodes than our set limit, then stop the
			 * recording immediately and disable the stop button.
			 */
			if(nodeArray.size()>=MAX_NODES){
				stopLocationUpdates();
				firstTime = true;
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				writeToXML();	// begin writing to disk
				progressBar.setVisibility(ProgressBar.INVISIBLE);
				return ;
			}
			
			float distance = location.distanceTo(lastLocation);
				
			/* If still within recording limits, then plot the latest node */
			if(distance>MIN_DISTANCE && distance<MAX_DISTANCE){
				Node node = new Node(location);
				nodeArray.add(node);
				nodeCount.setText("Node count: "+nodeArray.size());
				lastLocation = location;
			}
		}
		
		else{
			locationCount++;
			if(locationCount>WAIT_FOR_N_LOCATIONS){
				preciseEnough = true;
			}			
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		text.setText("GPS Status: Disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {		
		text.setText("GPS Status: Enabled");		
	}

	@Override
	protected void onStop() {
		try {
			manager.removeUpdates(this);
		}
		catch(NullPointerException e){

		}
		super.onStop();	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		if(event==GpsStatus.GPS_EVENT_FIRST_FIX){
			Toast.makeText(this, "GPS found your location", Toast.LENGTH_LONG);
			text.setText("GPS Status: Ready");
		}
		if(event==GpsStatus.GPS_EVENT_STARTED){
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
		if(event==GpsStatus.GPS_EVENT_STOPPED){
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
	}

    private String locations = "";

	public void updateConsoleView(Location arg0){
		text.setText("GPS Status: Ready");
		locations = locations + arg0.getLatitude()+" "+arg0.getLongitude()+"\n";
		console.setText(locations);
	}

	private ProgressDialog progress;
	private void writeToXML() {
						
		progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setCancelable(false);
		progress.setMax(100);
		progress.setProgress(0);
		progress.setMessage("Creating graph network...");
		progress.show();

		WriterThread write = new WriterThread();
		write.start();
		
	}


	/*
	 * Writing the recorded nodes to disk may take a while, and should be put in 
	 * a concurrent thread, so that it does not affect the responsiveness of the 
	 * system thread that handles the UI. 
	 */
	class WriterThread extends Thread{
		
		private Handler handler = new Handler(){
						
			int number = 0;
			public void handleMessage(Message msg){
				
				number++;
				progress.setProgress(msg.arg1);
				
				if(number==1){
					progress.setMessage("Finalising graph...");					
				}
				else if(number==2){
					progress.setMessage("Writing to XML...");					
				}
				else {
					progress.setMessage("Writing to KML...");
				}
				
				if(number>=3){
					progress.dismiss();	
				}
			}
		};
		
		public void run(){

			Finalize f = new Finalize(CONNECT_RANGE);
			f.connectGraph(nodeArray);
			increment();
			WriteXML writer = new WriteXML(nodeArray);
			increment();
			WriteKML kmlWriter = new WriteKML(nodeArray);
			increment();			
		        handler.sendEmptyMessage(0);
		}
		
		public void increment(){
			Message msg = handler.obtainMessage();
			msg.arg1=33;
			handler.sendMessage(msg);
		}
		
	}
			
	private ArrayList<ArrayList<Node>> nodesSoFar = new ArrayList<ArrayList<Node>>();
	
	public void readCacheFile(){
		
		FileInputStream fis;
		ObjectInputStream in;
		
		try {
			
			fis = new FileInputStream("mapbuilder.tmp");
			in = new ObjectInputStream(fis);
			nodesSoFar = (ArrayList<ArrayList<Node>>) in.readObject();
			in.close();
			nodewriteCacheFile();
			
		}
		catch(ClassNotFoundException clex){
			writeCacheFile();
		}
		catch(IOException e){
			Toast.makeText(this, "IOException reading cache: "+e.toString(), Toast.LENGTH_SHORT);
		}
		
	}
	
	private void writeCacheFile(){
		
		File root = Environment.getExternalStorageDirectory();
	    File file = new File(root, "mapbuilder.tmp");
	    
	    if(!root.canWrite()){
			Log.i("NavMap", "Cannot write to file-system");
	    	return ; 
	    }
	    
	    FileOutputStream fos;
	    ObjectOutputStream out;
	    
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(nodesSoFar);
			out.close();
		}
		catch(Exception e){
			Toast.makeText(this, "Exception when caching: "+e.toString(), Toast.LENGTH_SHORT);			
		}
	}

	@Override
	public void onLowMemory() {
		stopLocationUpdates();
		firstTime = true;
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		writeToXML();		
		super.onLowMemory();
	}
	
	public void addLocation(Location l){
		nodeArray.add(new Node(l));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		menu.add("Preferences").setIcon(R.drawable.ic_menu_preferences);
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getTitle().equals("Preferences")){
			Intent intent = new Intent(Builder.this, BuilderPreferences.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}	
}
