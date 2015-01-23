package es.deusto.series_app.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import es.deusto.series_app.R;
import es.deusto.series_app.activity.SeriesListActivity;
import es.deusto.series_app.database.SerieFavoritaDAO;
import es.deusto.series_app.login.Session;
import es.deusto.series_app.preferences.MySettingsFragment;
import es.deusto.series_app.vo.Serie;

public class SerieAdapter extends BaseAdapter implements Filterable {

	
	private Context context;
	private int resource;
	private List<Serie> series;
	private List<Serie> orig;
	private Filter mFilter;
	private Filter filterFavouritesSeries;
	private Filter filterShowConludedSeries;
	
	public SerieAdapter ( Context context, int resource, int textViewResourceId,
			List<Serie> series )
	{
		super();
		this.context = context;
		this.resource = resource;
		this.series = series;
	}
	
	public void setArraySeries ( List<Serie> series ) {
		this.series = series;
	}
	@Override
	public int getCount() {
		return series.size();
	}

	@Override
	public Object getItem(int position) {
		return series.get(position);
	}
	
	public void notifyDataSetChanged() {
	        super.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null ) 
		{
			convertView=LayoutInflater.from(context).inflate(resource, parent, false);
		}
		Serie serie = series.get(position);
		
		Map<String,Bitmap> bitmaps = SeriesListActivity.bitmapsForBannerPaths;
		
		if ( bitmaps != null )
		{
			if ( bitmaps.containsKey(serie.getBannerPath() ) )
			{
				ImageView imageView = (ImageView) convertView.findViewById(R.id.serie_banner);
				imageView.setImageBitmap(bitmaps.get(serie.getBannerPath()));
			}
		}
		
		TextView serieName = (TextView) convertView.findViewById(R.id.serie_name);
		TextView serieCadena = (TextView) convertView.findViewById(R.id.serie_cadena);
		
		serieName.setText(serie.getNombre());
		serieCadena.setText(serie.getCadena() );
		
		return convertView;
	}


	@Override
	public Filter getFilter() {
		if ( mFilter == null )
			mFilter = new SerieFilter();
		return mFilter;
	}
	
	public Filter getFavouriteSeriesFilter() {
		if ( filterFavouritesSeries == null )
			filterFavouritesSeries = new FavouriteSeriesFilter();
		return filterFavouritesSeries;
	}
	
	public Filter getShowConcludedSeriesFilter() {
		if ( filterShowConludedSeries == null )
			filterShowConludedSeries = new FinishedSeriesFilter();
		return filterShowConludedSeries;
	}
	
	private class FavouriteSeriesFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
	        
			final FilterResults oReturn = new FilterResults();
            final ArrayList<Serie> results = new ArrayList<Serie>();
            
            if (orig == null)
                orig = series;
            if (constraint != null) {
            	SerieFavoritaDAO serieFavoritaDAO = new SerieFavoritaDAO(context);
            	serieFavoritaDAO.open();
            	Session session = new Session(context);
            	List<String> seriesFavoritas = serieFavoritaDAO.findSeriesFavroitesByUserId( session.getId() );
            	
            	 if (orig != null && orig.size() > 0) {
            		 for ( final Serie serie : orig )
            		 {
            			 if ( seriesFavoritas.contains(serie.getId()) )
            			 {
            				 results.add(serie);
            			 }
            		 }
            	 }
            	 
                 oReturn.values = results;
                 oReturn.count = results.size();
            	serieFavoritaDAO.close();
            }
            else
            {
            	oReturn.values = orig;
            }
            
			return oReturn;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
	        Log.i("Favourite Result: ", ""+results.count);
	        Log.i("Favourite Result Values: ", ""+results.values);
	        
	        series = (ArrayList<Serie>) results.values;
	        notifyDataSetChanged();
		}
		
	}
	
	private class FinishedSeriesFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
	        
			final FilterResults oReturn = new FilterResults();
            final ArrayList<Serie> results = new ArrayList<Serie>();
            if (orig == null)
                orig = series;
            if (constraint != null) {
            	for ( final Serie serie : orig )
            	{
            		if ( !serie.isFinished() )
            		{
            			results.add(serie);
            		}
            	}
                oReturn.values = results;
                oReturn.count = results.size();
            }
            else
            {
            	oReturn.values = orig;
            }
            
            return oReturn;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
	        Log.i("Result: ", ""+results.count);
	        Log.i("Result Values: ", ""+results.values);
	        
	        series = (ArrayList<Serie>) results.values;
	        notifyDataSetChanged();
		}
		
	}
	
	private class SerieFilter extends Filter {
		
		
	    @Override
	    protected FilterResults performFiltering(CharSequence constraint) {
	        
	        final FilterResults oReturn = new FilterResults();
            final ArrayList<Serie> results = new ArrayList<Serie>();
            if (orig == null)
                orig = series;
            if (constraint != null) {
            	
            	
        	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        	Set<String> defaultValue = new HashSet<String>();
        	defaultValue.add("Name");
			Set<String> filterOptions = sharedPref.getStringSet(MySettingsFragment.KEY_FILTER_OPTIONS, defaultValue );
        	boolean filterByName = false;
        	boolean filterByNetwork = false;
        	boolean filterByDescription = false;
        	
        	if ( filterOptions.contains("Name") )
        		filterByName = true;
        	if ( filterOptions.contains("Network") )
        		filterByNetwork = true;
        	if ( filterOptions.contains("Description") )
        		filterByDescription = true;	
            	
                if (orig != null && orig.size() > 0) {
                    for (final Serie g : orig) {
                    	if ( ( filterByName && g.getNombre().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ) || 
                    		( filterByNetwork && g.getCadena().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ) ||
                    		( filterByDescription && g.getDescripcion().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ) )
                    	{
                    		results.add(g);
                    	}  
                    }
                }
                oReturn.values = results;
                oReturn.count = results.size();
            }
            return oReturn;
	    }
	    @SuppressWarnings("unchecked")
	    @Override
	    protected void publishResults(CharSequence constraint, FilterResults results) {
	        Log.i("Result: ", ""+results.count);
	        Log.i("Result Values: ", ""+results.values);
	        
	        series = (ArrayList<Serie>) results.values;
	        notifyDataSetChanged();
	    }

	}

}
