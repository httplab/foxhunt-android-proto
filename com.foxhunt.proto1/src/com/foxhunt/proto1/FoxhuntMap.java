package com.foxhunt.proto1;

import android.content.Context;
import android.graphics.*;
import android.location.Location;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
	
	private Location center;

	public Location getPlayerPosition()
	{
		return playerPosition;
	}

	public void setPlayerPosition(Location playerPosition)
	{
		this.playerPosition = playerPosition;
		if(centerOnPlayer)
		{
			center = playerPosition;
			projection.center = playerPosition;
		}
		invalidate();
	}

	private Location playerPosition;

	private double scale = 10.0;
	private ArrayList<Fox> foxes;
	final Projection projection = new MercatorProjection(center,scale);

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
		projection.setCenter(center);
		this.center = center;
		invalidate();
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
		projection.setScale(scale);
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



		Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_red16);
		
		if(foxes !=null)
		{
			for(Fox fox : foxes)
			{
				DrawFox(fox,canvas,icon);
			}
		}

		if(playerPosition!=null)
		{
			Paint p = new Paint();
			p.setARGB(255,230,230,230);

			double x = projection.getXCoord(playerPosition)+width/2;
			double y = projection.getYCoord(playerPosition)+height/2;

			canvas.drawCircle((float)x, (float)y, 3, p);
			Paint radius = new Paint();
			radius.setARGB(20,255,127,0);

			canvas.drawCircle((float)x, (float)y, (float) projection.getLength(playerPosition.getAccuracy()),radius);
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
		
		double x = projection.getXCoord(f.getLocation()) +width/2;
		double y = projection.getYCoord(f.getLocation()) + height/2;
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
				double lat = projection.getLatitude(-scrolledX/2,-scrolledY/2);
				double lon = projection.getLongitude(-scrolledX/2,-scrolledY/2);

				Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
				loc.setLatitude(lat);
				loc.setLongitude(lon);

				setCenter(loc);
				touchStartX = event.getX();
				touchStartY = event.getY();


				return true;
		}

		return true;
	}
}
