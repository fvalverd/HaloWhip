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
		String url = (String) settings.get("host");
		String username = (String) settings.get("username");
		String password = (String) settings.get("password");
		String door = (String) settings.get("door");
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 8000);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost post = new HttpPost(url);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("username", username));
		pairs.add(new BasicNameValuePair("password", password));
		pairs.add(new BasicNameValuePair("door_id", door));
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
