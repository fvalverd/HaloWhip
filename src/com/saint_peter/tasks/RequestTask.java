package com.saint_peter.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;


public class RequestTask extends AsyncTask<Void, Void, HttpResponse> {
	
	private RequestTaskListener listener;
	Map<String, ?> settings;

	public RequestTask(RequestTaskListener listener, Map<String, ?> settings) {
		this.listener = listener;
		this.settings = settings;
	}

	protected HttpResponse doInBackground(Void... voids) {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 8000);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost post = new HttpPost((String) settings.get("host"));
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", (String) settings.get("username")));
		pairs.add(new BasicNameValuePair("password", (String) settings.get("password")));
		pairs.add(new BasicNameValuePair("door_id", (String) settings.get("door")));
		HttpResponse response = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
			response = client.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
    }

    protected void onPostExecute(HttpResponse response) {
    	int status = 500;
    	String data = null;
    	if (response != null) {
	    	status = response.getStatusLine().getStatusCode();
	    	try {
				data = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
    	this.listener.requestTaskCallback(data, status);
    }
}
