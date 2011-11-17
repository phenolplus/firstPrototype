package prototype.first.test;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public final class ContainerBox {
	/** Pass your global data with this box 
	    Put top_ in front of your variable name */
	
	// pass-by
	public static SensorManager topManager;
	public static Sensor topSensor;
	public static String visablePoints;
	public static boolean isTab = false;
	
	// constant
	public static final float visableRange = 200;
}
