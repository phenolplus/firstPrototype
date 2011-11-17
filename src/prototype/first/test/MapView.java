package prototype.first.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {
	
	/** Members */
	private String[] names;
	private float[] x,y;
	private float ch,cw;
	private final float mag = 10;
	private int num=0;
	
	public MapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/** Control signal */
	public void readMap(ArrayList<HashMap<String,String>> readin){
		num = readin.size() + 1;
		names = new String[num];
		x = new float[num];
		y = new float[num];
		
		names[0] = "Self";
		x[0] = cw;
		y[0] = ch;
		
		for(int i=0;i<readin.size();i++){
			names[i+1] = readin.get(i).get("Name");
			y[i+1] = y[0] - mag*Float.parseFloat(readin.get(i).get("Data").split(":")[2]);
			x[i+1] = x[0] + mag*Float.parseFloat(readin.get(i).get("Data").split(":")[1]);
		}
		invalidate();
		
	}
	
	public void setCenter(float w,float h){
		ch = h/2;
		cw = w/2;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		Paint self,tar,white,blue;
		
		self = new Paint();
		self.setColor(Color.RED);
		
		tar = new Paint();
		tar.setColor(Color.CYAN);
		tar.setAlpha(200);
		
		white = new Paint();
		white.setColor(Color.WHITE);
		white.setStyle(Paint.Style.STROKE);
		
		blue = new Paint();
		blue.setColor(Color.BLUE);
		blue.setAlpha(100);
		blue.setStrokeWidth(8);
		
		// radar
		canvas.drawCircle(cw, ch, ContainerBox.visableRange, white);
		canvas.drawText("Radar Mode ! White circle is visable range.", 30, 30, white);
		
		// links
		for(int i=0;i<(num-1);i++){
			canvas.drawLine(x[i], y[i], x[i+1], y[i+1], blue);
		}
		
		// points
		for(int i=0;i<num;i++){
			canvas.drawCircle(x[i], y[i], 10, (i==0)?self:tar);
		}
		
		// names
		for(int i=1;i<num;i++){
			canvas.drawText(names[i], x[i]+5, y[i]-5, white);
		}
		
		super.onDraw(canvas);
	}

}
