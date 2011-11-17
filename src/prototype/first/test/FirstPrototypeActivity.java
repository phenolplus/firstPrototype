package prototype.first.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FirstPrototypeActivity extends Activity {

	/** Members */
	private SensorManager manager;
	private Sensor sensor;
	private SensorEventListener listener;

	private boolean called = false;

	private MapView mapView;
	private ListView pointListView;

	private ArrayList<HashMap<String, String>> pointList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private int cursor = -1;
	
	private float myX = 0,myY = 0;
	
	private View setData;
	private SharedPreferences savedPoints;
	private SharedPreferences.Editor editor;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ContainerBox.isTab = (Build.VERSION.SDK_INT > 10);
		Log.e("Version","APL level = "+Build.VERSION.SDK_INT);
		
		mapView = (MapView) findViewById(R.id.mapView);
		pointListView = (ListView) findViewById(R.id.listView);

		// set up sensors
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		ContainerBox.topManager = manager;
		ContainerBox.topSensor = sensor;
		
		buildList();
		mapView.setCenter(this.getWindowManager().getDefaultDisplay().getWidth()
				,this.getWindowManager().getDefaultDisplay().getHeight()*3/4);
		mapView.readMap(pointList);
		Log.e("MapView",""+mapView.getWidth()+" : "+mapView.getHeight());
	}
	
	/** System works */
	@Override
	public void onResume() {
		super.onResume();
		if (sensor != null) {
			listener = new SensorEventListener() {

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub

					float para = ContainerBox.isTab?event.values[1]:event.values[2];
					if (Math.abs(para) > 45 && !called) {
						called = true;
						Intent intent = new Intent();
						intent.setClass(FirstPrototypeActivity.this,
								CameraMode.class);
						FirstPrototypeActivity.this.quickPass();
						startActivity(intent);
					}

				}

			};
			manager.registerListener(listener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
			called = false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		manager.unregisterListener(listener);
		saveList();
	}

	/** Menu Control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add("Add Next");
		menu.add("Read");
		menu.add("Delete");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getTitle().toString().contentEquals("Add Next")) {
			addNewPoint();
		}
		if (item.getTitle().toString().contentEquals("Read")) {
			readStory();
		}
		if (item.getTitle().toString().contentEquals("Delete")) {
			deletePoint();
		}
		return true;
	}

	/** Dialog control */
	private void addNewPoint() {
		LayoutInflater infla = LayoutInflater.from(this);
		setData = infla.inflate(R.layout.popup, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add New Point");
		builder.setView(setData);
		
		// Cancel
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				FirstPrototypeActivity.this.cursor = -1;
				Log.e("MapView",""+mapView.getWidth()+" : "+mapView.getHeight());
			}
			
		});
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameIn = (EditText) setData.findViewById(R.id.nameField);
				EditText xIn = (EditText) setData.findViewById(R.id.x_in);
				EditText yIn = (EditText) setData.findViewById(R.id.y_in);
				
				String name;
				float x,y;
				name = nameIn.getText().toString();
				x = Float.parseFloat(xIn.getText().toString());
				y = Float.parseFloat(yIn.getText().toString());
				
				HashMap<String,String> item = new HashMap<String,String>();
				putDataToHash(item,name,x,y);
				
				
				if(cursor>0){
					pointList.add(cursor, item);
				} else {
					pointList.add(item);
				}
				FirstPrototypeActivity.this.adapter.notifyDataSetChanged();
				FirstPrototypeActivity.this.cursor = -1;
				mapView.readMap(pointList);
			}
			
		});
		
		builder.show();
	}

	private void deletePoint() {
		if(cursor<0){
			Toast.makeText(this, "Select first !!", Toast.LENGTH_SHORT).show();
		} else {
			pointList.remove(cursor);
			adapter.notifyDataSetChanged();
			mapView.readMap(pointList);
		}
		cursor = -1;
	}
	

	private void readStory() {
		Toast.makeText(this, "Nothing to say", Toast.LENGTH_SHORT).show();
		cursor = -1;
	}

	/** Build up list */
	private void putDataToHash(HashMap<String,String> item,String name, float x, float y) {
		item.put("Data", name+":"+x+":"+y);
		item.put("Name", name);
		item.put("Distance", ""+Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2))));
		item.put("Value", "x = "+x+" : y = "+y);
		item.put("xCord", ""+x);
		item.put("yCord", ""+y);
	}
	
	private void buildList() {
		savedPoints = getSharedPreferences("PointLocationList", Context.MODE_PRIVATE);
		String list = savedPoints.getString("LocationList", "North:10:0.1!");
		
		String[] entries = list.split("!");
		
		for(int i=0;i<entries.length;i++){
			HashMap<String,String> item = new HashMap<String,String>();
			String [] place = entries[i].split(":");
			float x,y;
			x = Float.parseFloat(place[1]);
			y = Float.parseFloat(place[2]);
			putDataToHash(item,place[0],x,y);
			pointList.add(item);
		}
		adapter = new SimpleAdapter(this, pointList, R.layout.pointitem, new String[] {"Name","Value"},new int[] {R.id.pointName,R.id.pointLocation});
		pointListView.setAdapter(adapter);
		
		pointListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				FirstPrototypeActivity.this.cursor = arg2;
			}
			
		});
		
	}

	private void saveList() {
		// store list
		String write = "";
		savedPoints = getSharedPreferences("PointLocationList",Context.MODE_PRIVATE);
		editor = savedPoints.edit();
		for(int i=0;i<pointList.size();i++){
			write = write + pointList.get(i).get("Data") + "!";
		}
		editor.putString("LocationList", write);
		editor.commit();
		
	}
	
	private void quickPass(){
		String pass = "";
		for(int i=0;i<pointList.size();i++){
			float range = Float.parseFloat(pointList.get(i).get("Distance"));
			if(range<(ContainerBox.visableRange*ContainerBox.visableRange)) {
				float rx,ry;
				rx = Float.parseFloat(pointList.get(i).get("xCord")) - myX;
				ry = Float.parseFloat(pointList.get(i).get("yCord")) - myY;
				pass = pass + pointList.get(i).get("Name") + ":" + rx + ":" + ry + "!";
			}
		}
		ContainerBox.visablePoints = pass;
		Log.e("Pass data","Pass = "+pass);
	}
}