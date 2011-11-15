package prototype.first.test;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class CameraMode extends Activity {
	
	/** Members */
	private DrawingSurface mSurface;
	
	private SensorManager manager = ContainerBox.topManager;
	private Sensor sensor = ContainerBox.topSensor;
	private SensorEventListener listener;
	
	private boolean called = false;
	private boolean visited = false;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurface = new DrawingSurface(this);
        setContentView(mSurface);
        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.e("CameraMode","onResume called");
    	listener = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				mSurface.setCurrentFace(event.values);
				float para = event.values[1];
				
				if(Math.abs(para)>50){
					visited = true;
				}
				
				if(Math.abs(para)<10&&!called&&visited){
					Log.e("Sensor","back : para = "+para);
					CameraMode.this.finish();
					called = true;
				}
				//Log.e("Sensor","now para = "+para);
			}
    		
    	};
    	
    	manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    	called = false;
    	visited = false;
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	manager.unregisterListener(listener);
    	Log.e("CameraMode"," done onPause");
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.e("CameraMode"," done onDestroy");
    }
    
   
}
