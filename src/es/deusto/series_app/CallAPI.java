package es.deusto.series_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CallAPI extends AsyncTask<String, Void, String>{

	private Context appContext;
	private ICallAPI iCallAPI;
	
	public CallAPI ( Context context, ICallAPI callAPI )
	{
		appContext = context;
		iCallAPI = callAPI;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String response = makeGetCall ( params[0] );
		
		return response;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if ( result == null )
		{
			Toast.makeText(appContext, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
		}
		else
		{
			iCallAPI.parseCallResponse(result);
		}
		
		
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