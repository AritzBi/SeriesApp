package es.deusto.series_app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SeriesListActivity extends Activity implements ICallAPI {

	private static String URL_API_SERIES = "http://pythontest-aritzbi.rhcloud.com/api/series";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		generateSeriesList();
		
		setContentView(R.layout.activity_series_list);
	}
	
	private void generateSeriesList() {
		CallAPI callAPI = new CallAPI(getApplicationContext(), this);
		callAPI.execute(URL_API_SERIES);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.series_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void parseCallResponse(String response) {
		List<Serie> listaSeries = new ArrayList<Serie>();
		try {
			JSONObject json = new JSONObject( response );
			JSONArray series = json.getJSONArray("results");
			
			for ( int i = 0; i < series.length(); i++ )
			{
				JSONObject jsonSerie = series.getJSONObject(i);
				String cadena = jsonSerie.getString("network");
				String descripcion = jsonSerie.getString("overview");
				String bannerPath = jsonSerie.getString("banner");
				String id = jsonSerie.getString("id");
				String nombre = jsonSerie.getString("name");
				
				Serie serie = new Serie();
				serie.setCadena(cadena);
				serie.setDescripcion(descripcion);
				serie.setBannerPath(bannerPath);
				serie.setId(id);
				serie.setNombre(nombre);
				
				listaSeries.add(serie);
			}
			
		} catch (JSONException e) {
			Log.e("Error", "Parsing json " + e.getMessage());
		}
	}
}
