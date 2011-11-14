package prototype.first.test;

import android.app.Activity;
import android.os.Bundle;

public class CameraMode extends Activity {
	
	/** Members */
	private DrawingSurface mSurface;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mSurface);
        
    }
}
