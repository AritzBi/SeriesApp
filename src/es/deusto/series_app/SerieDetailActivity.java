package es.deusto.series_app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import es.deusto.series_app.activity.EpisodioDetailActivity;
import es.deusto.series_app.adapter.ExpandableListAdapter;
import es.deusto.series_app.database.EpisodioDAO;
import es.deusto.series_app.vo.Episodio;

public class SerieDetailActivity extends Activity implements ICallAPI,OnChildClickListener,IConvertToBitmap{

	private ExpandableListView episodios;
	private ExpandableListAdapter expandableListAdapter;
	private List<Episodio> lstEpisodios;
	private EpisodioDAO episodioDAO;
	
	ImageView imageView;
	private Serie serie;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_serie_detail);
		
		episodios = (ExpandableListView) findViewById(R.id.listEpisodios);
		
		episodioDAO = new EpisodioDAO(this);
		
		episodioDAO.open(); 
	
		Serie serie = null;
		
		if (getIntent().getExtras() != null) 
		{
			serie = (Serie) getIntent().getExtras().getSerializable(Constantes.INFO_SERIE);
			Log.i("Serie recibida",""+serie);
			
			if (serie != null)
			{
				setSerie ( serie );
				
				lstEpisodios = episodioDAO.findBySerieId(serie.getId());
				Log.i("Retrieved episodios", ""+ episodios);
				
				if ( lstEpisodios == null || lstEpisodios.size() < 1 )
				{
					getEpisodiosFromSerie ( serie );
				}
			}
		}
		
		if ( lstEpisodios == null )
		{
			lstEpisodios = new ArrayList<Episodio>();
		}
		
		expandableListAdapter = new ExpandableListAdapter(SerieDetailActivity.this, lstEpisodios);
		
		episodios.setAdapter(expandableListAdapter);
		
		episodios.setOnChildClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void setSerie ( Serie serie )
	{
		this.serie = serie;
		imageView = (ImageView) findViewById(R.id.serieBanner);
		if ( serie.getBannerPath() != null )
		{
			ConvertToBitmap convertToBitmap = new ConvertToBitmap(getApplicationContext(), this);
			List<String> enlaces = new ArrayList<String>();
			enlaces.add( serie.getBannerPath( ) );
			convertToBitmap.execute( enlaces );
		}
		
		
	}
	
	private void getEpisodiosFromSerie ( Serie serie )
	{
		String url = Constantes.URL_API_EPISODIOS + serie.getId();
		CallAPI callAPI = new CallAPI(getApplicationContext(), this);
		Log.i("URL", url);
		callAPI.execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.serie_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void parseCallResponse(JSONObject json) {
		
		List<Episodio> listaEpisodios = null;
		
		try {
			JSONArray episodios = json.getJSONArray("results");
			listaEpisodios = JSONParser.parseEpisodios ( episodios, serie.getId() );
		}
		catch ( JSONException e )
		{
			Log.e("Error", "Parsing json " + e.getMessage());
		}
		
		if ( listaEpisodios.size() > 0 )
		{
			//Save the data over the database
			for ( Episodio episodio : listaEpisodios )
			{
				episodioDAO.addEpisodio(episodio);
			}
			lstEpisodios = listaEpisodios;
			expandableListAdapter.setEpisodios(listaEpisodios);
			expandableListAdapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		Episodio episodio = (Episodio) expandableListAdapter.getChild(groupPosition, childPosition);
		if ( episodio != null )
		{
			Intent intent = new Intent(this,EpisodioDetailActivity.class);
			intent.putExtra( Constantes.INFO_EPISODIO, episodio );
			startActivity(intent);
		}
		
		return false;
	}

	@Override
	public void processBitmapsForBannersPath(
			Map<String, Bitmap> bitmapsBannersPath) {
		
		if ( serie != null )
		{
			if ( bitmapsBannersPath.containsKey(serie.getBannerPath()))
			{
				Bitmap bitmap = bitmapsBannersPath.get(serie.getBannerPath());
				Log.i("Image View", "" + imageView);
				Log.i("Bitmap", "" + bitmap);
				imageView.setImageBitmap(bitmap);
			}
		}
		
	}
	
	@Override
	protected void onResume() {
		episodioDAO.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		episodioDAO.close();
		super.onPause();
	}
}
