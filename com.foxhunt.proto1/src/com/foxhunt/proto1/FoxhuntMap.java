package com.foxhunt.proto1;

import android.content.Context;
import android.graphics.*;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.foxhunt.proto1.entity.Fox;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 08.01.12
 * Time: 18:48
 * To change this template use File | com.foxhunt.proto1.Settings | File Templates.
 */
public class FoxhuntMap extends View
{
	private int width;
	private int height;
	
	private double centerX;
	private double centerY;

	public boolean isCenterOnPlayer()
	{
		return centerOnPlayer;
	}

	public void setCenterOnPlayer(boolean centerOnPlayer)
	{
		this.centerOnPlayer = centerOnPlayer;
		if(centerOnPlayer)
		{
			setCenter(playerPosition);
		}
	}

	private boolean centerOnPlayer=true;
	
	private float touchStartX;
	private float touchStartY;

	public Location getPlayerPosition()
	{
		return playerPosition;
	}

	public void setPlayerPosition(Location playerPosition)
	{
		this.playerPosition = playerPosition;
		if(centerOnPlayer)
		{
			setCenter(playerPosition);
		}
		invalidate();
	}

	private Location playerPosition;

	private double scale = 10.0;
	private ArrayList<Fox> foxes;
	final Projection projection = new MercatorProjection(scale);

	public FoxhuntMap(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,  attrs, defStyle);
	}

	public FoxhuntMap(Context context) {
		this(context, null);
	}

	public FoxhuntMap(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public void setCenter(Location center)
	{   
		if(center==null)
		{
			centerX=0;
			centerY=0;
			invalidate();
			return;
		}
		centerX = projection.getXCoord(center);
		centerY = projection.getYCoord(center);
		invalidate();
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		double oldScale=this.scale;
		this.scale = scale;
		projection.setScale(scale);
		centerX = centerX/scale*oldScale;
		centerY = centerY/scale*oldScale;
		invalidate();

	}

	public void setFoxes(ArrayList<Fox> foxes)
	{
		this.foxes = foxes;
		invalidate();
	}

	@Override protected void onDraw(Canvas canvas)
	{
		Paint backgr = new Paint();
		backgr.setARGB(255,0,0,255);
		backgr.setShader(new LinearGradient(0,0,width,height,Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));
		canvas.drawPaint(backgr);


        
		 
        
        
        Bitmap redIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_red16);
        Bitmap blueIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_blue16);
        Bitmap grayIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_gray16);
		
		if(foxes !=null)
		{
			for(Fox fox : foxes)
			{
                switch(fox.getType())
                {
                    case Fox.RED_FOX:
                        DrawFox(fox,canvas,redIcon);
                        break;
                    case Fox.BLUE_FOX:
                        DrawFox(fox,canvas,blueIcon);
                        break;
                    case Fox.GRAY_FOX:
                        DrawFox(fox,canvas,grayIcon);
                        break;
                }

			}
		}

		if(playerPosition!=null)
		{
			Paint p = new Paint();
			p.setARGB(255,230,230,230);

			double x = projection.getXCoord(playerPosition)-centerX+width/2;
			double y = -projection.getYCoord(playerPosition)+centerY+height/2;

			canvas.drawCircle((float)x, (float)y, 3, p);
			Paint radius = new Paint();
			radius.setARGB(20,255,127,0);

			canvas.drawCircle((float)x, (float)y, (float) (playerPosition.getAccuracy()/scale),radius);
		}



	}

	private void DrawZoomTools()
	{
		Bitmap minusBtn = BitmapFactory.decodeResource(this.getResources(),R.drawable.btn_minus_default);
		Bitmap plusBtn = BitmapFactory.decodeResource(this.getResources(),R.drawable.btn_plus_default);




	}

	private void DrawFox(Fox f, Canvas canvas, Bitmap icon)
	{
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.WHITE);
		
		double x = projection.getXCoord(f.getLocation()) -centerX + width/2;
		double y = -projection.getYCoord(f.getLocation()) + centerY + height/2;
		canvas.drawBitmap(icon,(float)x-8,(float)y-15,p);
		canvas.drawText(f.getName(),(float)x+22-16,(float) y-5,p);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = View.MeasureSpec.getSize(widthMeasureSpec);
		height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				touchStartX = event.getX();
				touchStartY = event.getY();
				return true;
			case MotionEvent.ACTION_MOVE:
				setCenterOnPlayer(false);
				float scrolledX = event.getX() -touchStartX;
				float scrolledY = event.getY() - touchStartY;

				centerX -=scrolledX;
				centerY +=scrolledY;

				touchStartX = event.getX();
				touchStartY = event.getY();

				invalidate();
				return true;
		}

		return true;
	}
}
