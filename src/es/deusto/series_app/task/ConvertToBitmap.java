package es.deusto.series_app.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.deusto.series_app.R;
import es.deusto.series_app.R.string;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

public class ConvertToBitmap extends AsyncTask<List<String>, Void, Map<String,Bitmap>> {

	private Context appContext;
	
	private IConvertToBitmap iConvertToBitmap;
	
	public ConvertToBitmap ( Context context, IConvertToBitmap iConvertToBitmap )
	{
		this.appContext = context;
		this.iConvertToBitmap = iConvertToBitmap;
	}

	@Override
	protected Map<String, Bitmap> doInBackground(List<String>... params) {
		
		Map<String,Bitmap> bitMapsForBannersPath = new HashMap<String, Bitmap>();
		List<String> bannerPaths = params[0];
		
		Bitmap bitmap = null;
		for ( String bannerPath: bannerPaths )
		{
			 bitmap = getBitmapFromURL(bannerPath);
			 if ( bannerPath != null && bitmap != null )
			 {
				 bitMapsForBannersPath.put ( bannerPath, bitmap ); 
			 }
		}
	
		return bitMapsForBannersPath;
	}
	
	@Override
	protected void onPostExecute(Map<String, Bitmap> result) {
		if ( result == null )
		{
			Toast.makeText(appContext, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
		}
		else
		{
			iConvertToBitmap.processBitmapsForBannersPath(result);
		}
		
		
	}
	
	public Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

}
