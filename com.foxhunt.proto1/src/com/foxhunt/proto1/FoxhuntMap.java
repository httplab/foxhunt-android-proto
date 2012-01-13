package com.foxhunt.proto1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.util.AttributeSet;
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
	
	private Location center;
	private double scale =1000.0;
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
		super.onDraw(canvas);    //To change body of overridden methods use File | com.foxhunt.proto1.Settings | File Templates.
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setARGB(255,230,230,230);
		canvas.drawCircle(width / 2, height / 2, 3, p);
		
		Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_red16);
		
		if(foxes !=null)
		{
			for(Fox fox : foxes)
			{
				DrawFox(fox,canvas,icon,p);
			}
		}
	}
	
	private void DrawFox(Fox f, Canvas canvas, Bitmap icon, Paint p)
	{
		double x = projection.getXCoord(f.getLocation()) +width/2;
		double y = projection.getYCoord(f.getLocation()) + height/2;
		canvas.drawBitmap(icon,(float)x,(float)y,p);
		canvas.drawText(f.getName(),(float)x+10,(float) y+5,p);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = View.MeasureSpec.getSize(widthMeasureSpec);
		height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
