package com.foxhunt.proto1;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 09.01.12
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class TextSettingView extends RelativeLayout
{
	private OnClickListener _onClickListener;

	protected TextView getTxtLabel()
	{
		return  (TextView )findViewById(R.id.txtLabel);
	}
	
	protected TextView getTxtValue()
	{
		return (TextView) findViewById(R.id.txtValue);
	}

	protected Button getBtnEdit()
	{
		return (Button) findViewById(R.id.btnEdit);
	}
	
	
	public void setLabel(String label)
	{
		getTxtLabel().setText(label);
	}

	public String getLabel()
	{
		return (String) getTxtLabel().getText();
	}

	public void setValue(String value)
	{
		getTxtValue().setText(value);
	}

	public String getValue()
	{
		return (String) getTxtValue().getText();
	}

	public void setOnClickListeter(OnClickListener listener)
	{
		_onClickListener = listener;
	}

	public TextSettingView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,  attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.text_settings_view,this,true);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.com_foxhunt_proto1_TextSettingView);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; ++i)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
				case R.styleable.com_foxhunt_proto1_TextSettingView_label:
					String value = a.getString(attr);
					setLabel(value);
				case R.styleable.com_foxhunt_proto1_TextSettingView_value:
					String value1 = a.getString(attr);
					setValue(value1);
			}
		}
		a.recycle();
		
		getBtnEdit().setOnClickListener(new OnClickListener()
		{
			@Override public void onClick(View view)
			{

				AlertDialog.Builder alert = new AlertDialog.Builder(TextSettingView.this.getContext());

				alert.setTitle(TextSettingView.this.getLabel());
				final EditText input = new EditText(TextSettingView.this.getContext());
				input.setText(TextSettingView.this.getValue());
				input.selectAll();
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						TextSettingView.this.setValue(value);
						if(TextSettingView.this._onClickListener!=null)
							_onClickListener.onClick(TextSettingView.this);
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

				alert.show();
			}
		});
	}

	public TextSettingView(Context context) {
		this(context, null);
	}

	public TextSettingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
}
