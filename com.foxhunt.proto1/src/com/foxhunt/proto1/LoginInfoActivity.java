package com.foxhunt.proto1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 13.01.12
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
public class LoginInfoActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		SharedPreferences sp = getSharedPreferences(Main.PREFS_NAME,0);
		int userId = sp.getInt("user_id", -1);
		
		if(userId>=0)
		{
			EditText tw = (EditText) findViewById(R.id.txtUserId);
			tw.setText(Integer.toString(userId));
		}
		
	}

	public void btnSave_Click(View v)
	{
		EditText tw = (EditText) findViewById(R.id.txtUserId);
		try
		{
			String textValue =tw.getText().toString();
			int userId = Integer.parseInt(textValue);
			if(userId>=0)
			{
				SharedPreferences.Editor editor = getSharedPreferences(Main.PREFS_NAME, 0).edit();
				editor.putInt("user_id", userId);
				editor.commit();
			}
			else
			{
				throw new Exception("Number must be nonnegative.");
			}
		}
		catch (Exception e)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("User Id");
			builder.setMessage("Wrong format");
			builder.setNeutralButton("OK", null);
			builder.show();
		}
	}
}