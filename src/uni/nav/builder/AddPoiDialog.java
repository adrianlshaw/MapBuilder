package uni.nav.builder;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class AddPoiDialog extends Dialog implements OnClickListener {

	private Button submit;
	private Button cancelButton;
	private Button photoButton;
	private TextView buildingNameBox;
	private TextView codeBox;
	private TextView deptNameBox;
	private Location location;
	private ArrayList<Node> nodes;
	private Context context;
	
	public AddPoiDialog(Context context, Location l, ArrayList<Node> mainNodes) {
		super(context);
		this.context=context;
		nodes = mainNodes;
		this.setCancelable(false);
		this.setContentView(R.layout.add_poi);
		this.setTitle("Add new Point of Interest");
		buildingNameBox = (TextView) findViewById(R.id.buildingNameBox);
		codeBox = (TextView) findViewById(R.id.codeBox);
		deptNameBox = (TextView) findViewById(R.id.departmentBox);
		submit = (Button) findViewById(R.id.dialog_addpoi_button);
		cancelButton = (Button) findViewById(R.id.dialog_cancel_button);
		photoButton = (Button) findViewById(R.id.dialog_photo_btn);
		photoButton.setOnClickListener(this);
		submit.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		location = l;
	}

	@Override
	public void onClick(View v) {
		
		if(v.equals(photoButton)){
			
		}
		
		if(v.equals(submit)){
			String s = ""+buildingNameBox.getText();
			s.trim();
			
			if(!s.equalsIgnoreCase("") && buildingNameBox.getText().length()>0){
				Node node = new Node(location);
				node.setBuildingName(buildingNameBox.getText()+"");
				
				if(codeBox.getText().length()>0){
					node.setCode(""+ codeBox.getText());
				}

				if(deptNameBox.getText().length()>0){
					node.setDeptName(""+deptNameBox.getText());
				}				
				nodes.add(node);
				Toast.makeText(context, "Added POI", Toast.LENGTH_SHORT).show();
				dismiss();
			}
			else {
				Toast.makeText(context, "You must enter a building name", Toast.LENGTH_LONG).show();
			}
		}		
		if(v.equals(cancelButton)){
			dismiss();
		}
	}
}