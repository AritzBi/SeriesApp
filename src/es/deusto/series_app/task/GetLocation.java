package es.deusto.series_app.task;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import es.deusto.series_app.R;

public class GetLocation extends AsyncTask<Void, Void, String> implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	private LocationClient mLocationClient;
	private Context appContext;
	private IGetLocation iGetLocation;
	
	public GetLocation( IGetLocation iGetLocation, Context context ) {
		this.iGetLocation = iGetLocation;
		this.appContext = context;
	}
	
	@Override
	protected String doInBackground(Void... params) {
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext) != ConnectionResult.SUCCESS){
			return null;
		}
		
        mLocationClient = new LocationClient(appContext, this, this);
        mLocationClient.connect(); // Emulators with no Google Play support will fail at this point
        
        // Wait until connection
        while(!mLocationClient.isConnected());
        
		Location loc = mLocationClient.getLastLocation();
		
		String location = loc.getLatitude() + ";" + loc.getLongitude();
		Log.i("Location: ", location);
        return location;
	}

	@Override
	protected void onPostExecute(String result) {
		if(result == null )
		{
			Toast.makeText(appContext, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
		}
		else
		{
			String [] location = result.split(";");
			
			Location loc = new Location("");
			loc.setLatitude(Double.parseDouble(location[0]));
			loc.setLongitude(Double.parseDouble(location[1]));
			
			iGetLocation.processReceivedLocation(loc);
			
		}
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.i("Location client", "Connection failed");		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i("Location client", "Connected");
		// Normally, we will perform the actions here, but from this place we cannot access UI elements
	}

	@Override
	public void onDisconnected() {
		Log.i("Location client", "Disconnected");		
	}
}