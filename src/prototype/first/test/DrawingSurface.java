package prototype.first.test;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class DrawingSurface extends android.view.SurfaceView implements SurfaceHolder.Callback{

	public DrawingSurface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDraw(Canvas canvas){
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

}
