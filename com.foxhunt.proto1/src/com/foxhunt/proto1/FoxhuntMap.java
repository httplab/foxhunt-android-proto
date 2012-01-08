package com.foxhunt.proto1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 08.01.12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntMap extends View
{
	private int _width;
	private int _height;
	
	private Location _center;
	private double _scale=20.0;
	private ArrayList<Fox> _foxes;	
	
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
		_center = center;
		invalidate();
	}

	public void setScale(double scale)
	{
		_scale = scale;
		invalidate();
	}

	public void setFoxes(ArrayList<Fox> foxes)
	{
		_foxes = foxes;
		invalidate();
	}

	@Override protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setARGB(255,230,230,230);
		canvas.drawCircle(_width / 2, _height / 2, 3, p);
		
		Bitmap icon = BitmapFactory.decodeResource(this.getResources(),R.drawable.fox_red16);
		
		if(_foxes!=null)
		{
			for(Fox fox : _foxes)
			{
				DrawFox(fox,canvas,icon,p);
			}
		}
	}
	
	private void DrawFox(Fox f, Canvas canvas, Bitmap icon, Paint p)
	{
		Location sameLat = new Location(f.getLocation());
		sameLat.setLatitude(_center.getLatitude());

		Location sameLon = new Location(f.getLocation());
		sameLon.setLongitude(_center.getLongitude());

		double x = sameLat.distanceTo(_center)/_scale + _width/2;
		double y = -sameLon.distanceTo(_center)/_scale + _height/2;
		canvas.drawBitmap(icon,(float)x,(float)y,p);
		canvas.drawText(f.getName(),(float)x+10,(float) y+5,p);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		_width = View.MeasureSpec.getSize(widthMeasureSpec);
		_height = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(_width, _height);
	}
}
