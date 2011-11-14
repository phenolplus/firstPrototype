package prototype.first.test;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

public class FirstPrototypeActivity extends Activity {
    
	/** Members */
	private SensorManager manager;
	private Sensor sensor;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor!=null){
        	
        }
    }
    
    @Override
    public void onResume(){
    	if(sensor!=null){
        	
        }
    }
}