package es.deusto.series_app.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import es.deusto.series_app.R;

public class CallAPI extends AsyncTask<String, Void, JSONObject>{

	private Context appContext;
	private ICallAPI iCallAPI;
	private boolean isGetOperation;
	private Map<String,String> parametros;
	private boolean isJSONResponse;
	
	public CallAPI ( Context context, ICallAPI callAPI )
	{
		appContext = context;
		iCallAPI = callAPI;
		isGetOperation = true;
		isJSONResponse = true;
	}
	
	public void setGetOrPost ( boolean isGetOperation )
	{
		this.isGetOperation = isGetOperation;
	}
	
	public void setParametros ( Map<String,String> parametros )
	{
		this.parametros = parametros;
	}
	
	public void setJsonResponse ( boolean isJSONResponse )
	{
		this.isJSONResponse = isJSONResponse;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		String response = null;
		if ( isGetOperation )
		{
			response = makeGetCall ( params[0] );
		}
		else
		{
			response = makePostCall( params[0], parametros );
		}

		Log.i("Response CallAPI", response );
		
		JSONObject jsonObject = null;
		if ( isJSONResponse )
		{
			try {
				jsonObject = new JSONObject(response);
			} catch (JSONException e) {
				Log.e("Error CallAPI", e.getMessage());
			}
		}
		else
		{
			jsonObject = new JSONObject();
		}
		return jsonObject;
	}
	
	
	
    @Override
	protected void onPostExecute(JSONObject result) {
		Log.i("Coming Response", ""+ result );
		if ( result == null )
		{
			Toast.makeText(appContext, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Log.i("CallAPI", "Correct calling to ParseCallResponse" );
			if ( iCallAPI != null )
			{
				iCallAPI.parseCallResponse(result);
			}
		}
	}

    private String makePostCall (String path, Map<String,String> params) {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(path);

        //convert parameters into JSON object
        JSONObject holder = null;
		try {
			holder = getJsonObjectFromMap(params);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //passes the results to a string builder/entity
        StringEntity se = null;
		try {
			se = new StringEntity(holder.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page 
        ResponseHandler responseHandler = new BasicResponseHandler();
        String response = null;
        try {
			response = httpclient.execute(httpost, responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return response;
    }
    
    private static JSONObject getJsonObjectFromMap(Map<String,String> params) throws JSONException {

        //Stores JSON
        JSONObject holder = new JSONObject();

        for ( String key : params.keySet() )
        {
        	String value = params.get(key);
        	holder.put(key, value);
        }
        
        return holder;
    }
    
	private static String makeGetCall(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            	Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }

}
