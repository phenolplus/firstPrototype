package prototype.first.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class FirstPrototypeActivity extends Activity {
    
	/** Members */
	private SensorManager manager;
	private Sensor sensor;
	private SensorEventListener listener;
	private float roll = 0;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        ContainerBox.topManager = manager;
        ContainerBox.topSensor = sensor;
     
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(sensor!=null){
        	listener = new SensorEventListener(){

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					roll = event.values[2];
					Log.e("========= Sensor Listener","Pitch = "+roll);
					if(Math.abs(roll)>45){
						Intent intent = new Intent();
						intent.setClass(FirstPrototypeActivity.this, CameraMode.class);
						startActivity(intent);
					}
				}
        		
        	};
        	manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	manager.unregisterListener(listener);
    }
}