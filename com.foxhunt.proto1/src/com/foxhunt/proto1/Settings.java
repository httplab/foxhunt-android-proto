package com.foxhunt.proto1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 09.01.12
 * Time: 19:36
 * To change this template use File | com.foxhunt.proto1.Settings | File Templates.
 */
public class Settings extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		final TextSettingView setFixUrl = (TextSettingView) findViewById(R.id.setFixUrl);

		SharedPreferences sp = getSharedPreferences(Main.PREFS_NAME,0);
		setFixUrl.setValue(sp.getString("fix_url", getResources().getString(R.string.fix_url)));
		setFixUrl.setOnClickListeter(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				SharedPreferences.Editor editor = getSharedPreferences(Main.PREFS_NAME, 0).edit();
				editor.putString("fix_url", setFixUrl.getValue());
				editor.commit();
			}
		});
	}
}