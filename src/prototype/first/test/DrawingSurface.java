package prototype.first.test;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawingSurface extends android.view.SurfaceView implements SurfaceHolder.Callback{

	/** Members */
	private Camera camera;
	private SurfaceHolder holder;
	
	private final float[] north = {0, 0, 90};
	private ArrayList<float[]> targetList = new ArrayList<float[]>();
	private float[] current = new float[3];
	private final float distance = 50;
	
	public DrawingSurface(Context context) {
		super(context);
		current = north;
		this.setWillNotDraw(false);
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas = aquireNorth(canvas);
		canvas = showTargets(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			Log.e("IO Exception catch", "Did come into exception");
			camera.release();
			camera = null;
		}
		Camera.Parameters para = camera.getParameters();
		para.setPreviewSize(this.getWidth()/2, this.getHeight()/2);
		camera.setParameters(para);
		camera.startPreview();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
	}
	
	/** Importing data */
	public void setCurrentFace(float[] direction){
		current = direction;
	}
	
	public boolean importTargetList(String list){
		// The LIST should have the form :
		// Long:Lat!(next)!...
		// longitude and latitude are relative
		
		String[] entries = list.split("!");
		for(int i=0;i<entries.length;i++){
			float[] position = new float[2];
			targetList.add(position);
		}
		
		return true;
	}
	
	/** Utilities */
	private Canvas aquireNorth(Canvas canvas){
		float [] point = new float[2];
		// point = [x,y] = [polar,roll]
		
		point[1] = north[2] - current[2];
		if(current[0]>180){
			point[0] = north[0] - (current[0] - 360);
			
		} else {
			point[0] = north[0] - current[0];
			
		}
		point[0] = point[0]*distance;
		point[1] = point[1]*distance;
		
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAlpha(127);
		
		float x,y;
		y = canvas.getHeight()/2 + point[1];
		x = canvas.getWidth()/2 + point[0];
		
		canvas.drawCircle(x, y, 30, paint);
		paint.setAlpha(5);
		paint.setColor(Color.WHITE);
		canvas.drawText("N", x, y, paint);
		
		return canvas;
	}
	
	private Canvas showTargets(Canvas canvas){
		Paint paint = new Paint();
		paint.setAlpha(127);
		paint.setColor(Color.RED);
		
		float x,y;
		for(int i=0;i<targetList.size();i++){
			x = this.getWidth()/2 + targetList.get(i)[0];
			y = this.getHeight()/2 + targetList.get(i)[1];
			canvas.drawCircle(x, y, 30, paint);
		}
		return canvas;
	}

}
