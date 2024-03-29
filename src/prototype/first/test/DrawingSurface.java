package prototype.first.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawingSurface extends android.view.SurfaceView implements SurfaceHolder.Callback{

	/** Members */
	private Camera camera;
	private SurfaceHolder holder;
	
	private final float[] north = {0, -90, 90};
	private ArrayList<MyPoint> targetList = new ArrayList<MyPoint>();
	private float[] current = new float[3];
	private final float distance = ContainerBox.isTab?35:10; // depends on device and hand
	
	private Bitmap icon;
	
	public DrawingSurface(Context context) {
		super(context);
		
		this.setWillNotDraw(false);
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// TODO Auto-generated constructor stub
		
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.target);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas = aquireNorth(canvas);
		canvas = showTargets(canvas);
		invalidate();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e("Surface","   creating");
		camera = Camera.open();
		try{
			camera.setPreviewDisplay(holder);	
		} catch(IOException exp){
			camera.release();
			camera = null;
		}
		Camera.Parameters para = camera.getParameters();
		Log.e("Camera","camera size = "+para.getPreviewSize().height+":"+para.getPreviewSize().width);
		Log.e("Camera","screen size = "+this.getWidth()/2+":"+this.getHeight()/2 );
		Log.e("Camera","view angel = "+para.getHorizontalViewAngle()+":"+para.getVerticalViewAngle());
		
		List<Size> sizeList = para.getSupportedPreviewSizes();
		for(int i=0;i<sizeList.size();i++){
			int avaliableX,avaliableY;
			avaliableX = sizeList.get(i).width;
			avaliableY = sizeList.get(i).height;
			Log.e("Avaliable Size",sizeList.get(i).width+":"+sizeList.get(i).height);

			if(avaliableX*this.getHeight() == this.getWidth()*avaliableY) {
				para.setPreviewSize(avaliableX, avaliableY);
				break;
			}
			
		}
		camera.setParameters(para);
		camera.startPreview();
		Log.e("Surface","   did created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
		Log.e("Surface","   did destroyed");
		
	}
	
	/** Importing data */
	public void setCurrentFace(float[] direction){
		current[0] = ContainerBox.isTab?direction[0]:((direction[0]+90)%360);
		current[1] = ContainerBox.isTab?direction[1]:ContainerBox.faceUp?(direction[2]-180):(-direction[2]);
		current[2] = ContainerBox.isTab?direction[2]:direction[1];
		//Log.e("Orientation","theta = "+direction[2]);
		}
	
	public boolean importTargetList(String list){
		// The LIST should have the form :
		// Name:Lat:Long!(next)!...
		// longitude and latitude are relative
		
		String[] entries = list.split("!");
		if(entries.length==0){
			return false;
		}
		
		for(int i=0;i<entries.length;i++){
			MyPoint target = new MyPoint();
			String[] data = entries[i].split(":");
			if(data.length!=3){
				return false;
			} else {
				target.name = data[0];
				float x,y;
				x = Float.parseFloat(data[1]);
				y = Float.parseFloat(data[2]);

				if(x>0){
					target.phi = (float) ((y>=0)? Math.atan(x/y):(Math.PI + Math.atan(x/y)));
				} else {
					target.phi = (float) ((y>=0)?(2*Math.PI + Math.atan(x/y)):(Math.PI + Math.atan(x/y)));
				}
				
				target.phi = (float) Math.toDegrees(target.phi);
				target.theta = -85; // horizontal (default)
				
				Log.e("Points",target.name+" phi = "+target.phi);
				targetList.add(target);
			}
			
		}
		
		return true;
	}
	
	/** Utilities */
	private Canvas aquireNorth(Canvas canvas){
		float [] point = new float[2];
		// point = [x,y] = [polar,roll]
		
		point[1] = north[1] - current[1];
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
		
		//Log.e("Canvas","update point @ x = "+point[0]+"  "+"y = "+point[1]);
		return canvas;
	}
	
	private Canvas showTargets(Canvas canvas){
		Paint cPaint = new Paint();
		cPaint.setColor(Color.CYAN);
		cPaint.setAlpha(127);
		
		Paint tPaint = new Paint();
		tPaint.setColor(Color.WHITE);
		
		float x,y;
		float[] point = new float[2];
		for(int i=0;i<targetList.size();i++){
			point[1] = targetList.get(i).theta - current[1];
			point[0] = targetList.get(i).phi - current[0];
			// ============
			point[0] = (point[0]+360)%360;
			if(point[0]>270) {
				point[0] = point[0] - 360;
			}
					
			point[0] = point[0]*distance;
			point[1] = point[1]*distance;
			x = this.getWidth()/2 + point[0];
			y = this.getHeight()/2 + point[1];
			//canvas.drawCircle(x, y, 50, cPaint);
			canvas.drawBitmap(icon, x-icon.getWidth()/2, y-icon.getHeight()/2, null);
			canvas.drawText(targetList.get(i).name, x, y, tPaint);
		}
		return canvas;
	}
	
	// point box
	class MyPoint {
		public float phi;
		public float theta;
		public String name;
		
	}

}
