package es.deusto.series_app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ShareActionProvider;

public class SeriesListActivity extends ListActivity implements ICallAPI,OnQueryTextListener {

	private static String URL_API_SERIES = "http://pythontest-aritzbi.rhcloud.com/api/series";
	
	private List<Serie> lstSeries;
	
	private SerieAdapter serieAdapter;
	
	private Context appContext;
	
	private ActionMode mActionMode = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		generateSeriesList();
		
		appContext = getApplicationContext();
		
		if ( lstSeries == null )
			lstSeries = new ArrayList<Serie>();
		
		serieAdapter = new SerieAdapter(this, R.layout.serie_row, R.id.serie_name, lstSeries);
		
		setListAdapter(serieAdapter);
	
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		    // Called when the user long-clicks an item on the list
			public boolean onItemLongClick(AdapterView<?> parent, View row, int position, long rowid) {
		        if (mActionMode != null) {
		            return false;
		        }

		        // Important: to marked the editing row as activated
		        getListView().setItemChecked(position, true);

		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = SeriesListActivity.this.startActionMode(mActionModeCallback);
		        mActionMode.setTag(position);
		        return true;
		    }
		});
		
		getListView().setTextFilterEnabled(true);
	
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        
			// Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.serie_bar, menu);
	        
	        MenuItem menuShare = (MenuItem) menu.findItem(R.id.mnu_serie_share);
	        ShareActionProvider shareProv = (ShareActionProvider) menuShare.getActionProvider();
	        shareProv.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
	       
	        Serie selectedSerie = lstSeries.get(SeriesListActivity.this.getListView().getCheckedItemPosition() );
	        
	        Intent intent = new Intent(Intent.ACTION_SEND);
	        intent.setType("text/plain");
	        if ( selectedSerie != null )
	        {
	        	if ( selectedSerie.getNombre() != null && selectedSerie.getCadena() != null )
	        	{
	        		intent.putExtra(Intent.EXTRA_TEXT, "\"" + selectedSerie.getNombre() + "\" en " + selectedSerie.getCadena() );
	        	}
	        }
	        shareProv.setShareIntent(intent);
	        Log.i("Series", "Viene 4");
	        return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			
			SeriesListActivity.this.getListView().setEnabled(false);
			
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			
			if (SeriesListActivity.this.getListView().getCheckedItemPosition() >= 0)
			{
				SeriesListActivity.this.getListView().setItemChecked(SeriesListActivity.this.getListView().getCheckedItemPosition(),
						false);
			}
			SeriesListActivity.this.getListView().setEnabled(true);
	        
			mActionMode = null;
		}
		
	};
	
	private void generateSeriesList() {
		CallAPI callAPI = new CallAPI(getApplicationContext(), this);
		callAPI.execute(URL_API_SERIES);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.series_list, menu);
		MenuItem menuItem = menu.findItem(R.id.mnu_search_serie);
		if ( menuItem != null )
		{
			SearchView searchView = (SearchView) menuItem.getActionView();
			searchView.setOnQueryTextListener(this);
		}
		return super.onCreateOptionsMenu(menu);
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
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		if ( position >= 0 )
		{
			Serie selectedSerie = (Serie) lstSeries.get(position);
			Intent intent = new Intent(this,SerieDetailActivity.class);
			intent.putExtra( Constantes.INFO_SERIE, selectedSerie );
			startActivity(intent);
		}
		
		super.onListItemClick(listView, view, position, id);
	}


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
				Log.e("Series", Constantes.URL_RAIZ_BANNERS_SERIES + bannerPath );
				serie.setBannerPath( Constantes.URL_RAIZ_BANNERS_SERIES + bannerPath);
				serie.setId(id);
				serie.setNombre(nombre);
				
				listaSeries.add(serie);
			}
			
		} catch (JSONException e) {
			Log.e("Error", "Parsing json " + e.getMessage());
		}
		if ( listaSeries.size() > 0 )
		{
			lstSeries = listaSeries;
			serieAdapter.setArraySeries(lstSeries);
		}
		Log.i("Series",""+ lstSeries);
		serieAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		if (TextUtils.isEmpty(newText)) {
	        SeriesListActivity.this.serieAdapter.getFilter().filter(newText);
	    }
	    else {
	    	SeriesListActivity.this.serieAdapter.getFilter().filter(newText);
	    }

	    return true;
	}
}
