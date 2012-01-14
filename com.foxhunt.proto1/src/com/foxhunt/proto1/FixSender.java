package com.foxhunt.proto1;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 07.01.12
 * Time: 1:43
 * To change this template use File | com.foxhunt.proto1.Settings | File Templates.
 */
public class FixSender {
	private String _fixUrl;
	private  TelephonyManager _telephonyManager;
	private int userId;

    public interface FixResponseListener{
        void OnFixResponse(ArrayList<Fox> foxes);
    }

    private ArrayList<FixResponseListener> _responseListeners = new ArrayList<FixResponseListener>();

    public String getFixUrl() {
        return _fixUrl;
    }

    private void setFixUrl(String _fixUrl) {
        this._fixUrl = _fixUrl;
    }



    public void AddFixResponseListener(FixResponseListener fixResponseListener){
        _responseListeners.add(fixResponseListener);
    }

    public  FixSender(String fixUrl, TelephonyManager telephonyManager, int userId){
        this._fixUrl = fixUrl;
	    this._telephonyManager = telephonyManager;
	    this.userId = userId;
    }
    
    public void SendFix(Location location)
    {
        new SendFixTask(this).execute(location);
    }

    private class SendFixTask extends AsyncTask<Location, Integer, ArrayList<Fox>>{
        FixSender _owner;

        public SendFixTask(FixSender owner)
        {
            _owner = owner;
        }

        @Override
        protected ArrayList<Fox> doInBackground(Location... locations) {
            HttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(_fixUrl);
            HttpPost httpPost = new HttpPost(_fixUrl);
	        HttpResponse response = null;
	        try {
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


		        nameValuePairs.add(new BasicNameValuePair("lat", String.format(Locale.US, "%f",locations[0].getLatitude())));
		        nameValuePairs.add(new BasicNameValuePair("lon", String.format(Locale.US, "%f",locations[0].getLongitude())));

		        if(locations[0].hasAltitude())
		            nameValuePairs.add(new BasicNameValuePair("alt", String.format(Locale.US, "%f",locations[0].getAltitude())));

		        if(locations[0].hasAccuracy())
		            nameValuePairs.add(new BasicNameValuePair("acc", String.format(Locale.US, "%f",locations[0].getAccuracy())));

		        nameValuePairs.add(new BasicNameValuePair("client_time", String.format("%tT",locations[0].getTime())));

		        if(locations[0].hasSpeed())
		            nameValuePairs.add(new BasicNameValuePair("speed", String.format(Locale.US, "%f",locations[0].getSpeed())));

		        if(locations[0].hasBearing())
		            nameValuePairs.add(new BasicNameValuePair("bearing", String.format(Locale.US, "%f",locations[0].getBearing())));

		        nameValuePairs.add(new BasicNameValuePair("provider_id", locations[0].getProvider().equals("gps") ? "1" : "2"));

		        nameValuePairs.add(new BasicNameValuePair("device_id",_telephonyManager.getDeviceId()));
		        //nameValuePairs.add(new BasicNameValuePair("device_id","1"));
		        nameValuePairs.add(new BasicNameValuePair("sim_id",_telephonyManager.getSimSerialNumber()));
		        nameValuePairs.add(new BasicNameValuePair("line_number",_telephonyManager.getLine1Number()));
		        nameValuePairs.add(new BasicNameValuePair("sim_operator_name",_telephonyManager.getSimOperatorName()));
		        nameValuePairs.add(new BasicNameValuePair("user_id",Integer.toString(userId)));

		        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        response = client.execute(httpPost);


	        } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
	        } catch (IOException e) {
		        String z = e.getMessage();

	        }
	        
	        if(response!=null)
	        {
		        try
		        {
		            String s = HttpHelper.getResponseBody(response.getEntity());
			        return ParseFoxes(s);
		        }
		        catch (ParseException ex)
		        {
		            return null;
		        }
		        catch (IOException ex)
		        {
		            return  null;
		        }
	        }
	        else
	        {
		        return null;
	        }
        }




	    private ArrayList<Fox> ParseFoxes(String s)
	    {
		    try
		    {
			    
		        JSONArray obj = new JSONArray(s);
			    int len = obj.length();
			    ArrayList<Fox> res = new ArrayList<Fox>();
			    for(int i=0; i<len; i++)
			    {
					JSONObject foxStr = obj.getJSONObject(i).getJSONObject("fox");
					Fox fox = new Fox();
				    fox.setId(foxStr.getInt("id"));
				    fox.setName(foxStr.getString("name"));
				    fox.setLat(foxStr.getDouble("lat"));
				    fox.setLon(foxStr.getDouble("lon"));
				    res.add(fox);
			    }
			    
			    return res;
		    }
		    catch (JSONException ex)
		    {
			        return  null;
		    }

	    }

        @Override
        protected void onPostExecute(ArrayList<Fox> foxes) {
            super.onPostExecute(foxes);    //To change body of overridden methods use File | com.foxhunt.proto1.Settings | File Templates.
            for(FixResponseListener  rl :  _owner._responseListeners)
            {
                rl.OnFixResponse(foxes);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();    //To change body of overridden methods use File | com.foxhunt.proto1.Settings | File Templates.
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | com.foxhunt.proto1.Settings | File Templates.
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);    //To change body of overridden methods use File | com.foxhunt.proto1.Settings | File Templates.
        }
    }
}
