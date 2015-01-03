package es.deusto.series_app;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class SerieAdapter extends BaseAdapter implements Filterable {

	
	private Context context;
	private int resource;
	private int textViewResourceId;
	private List<Serie> series;
	private List<Serie> orig;
	private Filter mFilter;
	
	public SerieAdapter ( Context context, int resource, int textViewResourceId,
			List<Serie> series )
	{
		super();
		this.context = context;
		this.resource = resource;
		this.textViewResourceId = textViewResourceId;
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
	
	private Bitmap DownloadImage(String URL)
    {
	    InputStream in = null;
		try {
			URL url = new URL ( URL.trim() );
			in = url.openConnection().getInputStream();
		} catch (IOException e) {
			Log.e("Series", "" + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        BufferedInputStream bis = new BufferedInputStream(in,1024*8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int len=0;
        byte[] buffer = new byte[1024];
        try {
			while((len = bis.read(buffer)) != -1){
			    out.write(buffer, 0, len);
			}
	        out.close();
	        bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        byte[] data = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);       
        return bitmap;
    }



	@Override
	public Filter getFilter() {
		if ( mFilter == null )
			mFilter = new SerieFilter();
		return mFilter;
	}
	
	private class SerieFilter extends Filter {

	    @Override
	    protected FilterResults performFiltering(CharSequence constraint) {
	        
	        final FilterResults oReturn = new FilterResults();
            final ArrayList<Serie> results = new ArrayList<Serie>();
            if (orig == null)
                orig = series;
            if (constraint != null) {
                if (orig != null && orig.size() > 0) {
                    for (final Serie g : orig) {
                    	if ( g.getNombre().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) || 
                    			g.getCadena().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) )
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
