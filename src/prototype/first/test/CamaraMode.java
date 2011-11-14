package prototype.first.test;

import android.app.Activity;
import android.os.Bundle;

public class CamaraMode extends Activity {
	
	/** Members */
	DrawingSurface mSurface;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mSurface);
        
    }
}
