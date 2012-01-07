package com.foxhunt.proto1;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 07.01.12
 * Time: 1:43
 * To change this template use File | Settings | File Templates.
 */
public class FixSender {
    public interface FixResponseListener{
        void OnFixResponse(Fox[] foxes);
    }

    private ArrayList<FixResponseListener> _responseListeners = new ArrayList<FixResponseListener>();

    public String getFixUrl() {
        return _fixUrl;
    }

    private void setFixUrl(String _fixUrl) {
        this._fixUrl = _fixUrl;
    }

    private String _fixUrl;

    public void AddFixResponseListener(FixResponseListener fixResponseListener){
        _responseListeners.add(fixResponseListener);
    }

    public  FixSender(String fixUrl){
        this._fixUrl = fixUrl;
    }
    
    public void SendFix(Location location)
    {
        new SendFixTask(this).execute(location);
    }

    private class SendFixTask extends AsyncTask<Location, Integer, Fox[]>{
        FixSender _owner;

        public SendFixTask(FixSender owner)
        {
            _owner = owner;
        }

        @Override
        protected Fox[] doInBackground(Location... locations) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(_fixUrl);

	        try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("lat", String.format("%f",locations[0].getLatitude())));
		        nameValuePairs.add(new BasicNameValuePair("lon", String.format("%f",locations[0].getLongitude())));
		        nameValuePairs.add(new BasicNameValuePair("alt", String.format("%f",locations[0].getAltitude())));
		        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = client.execute(httpPost);

	        } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
	        } catch (IOException e) {
		        // TODO Auto-generated catch block
	        }
	        
	        return new Fox[0];
        }

        @Override
        protected void onPostExecute(Fox[] foxes) {
            super.onPostExecute(foxes);    //To change body of overridden methods use File | Settings | File Templates.
            for(FixResponseListener  rl :  _owner._responseListeners)
            {
                rl.OnFixResponse(foxes);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
