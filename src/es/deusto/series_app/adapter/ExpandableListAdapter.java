package es.deusto.series_app.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import es.deusto.series_app.R;
import es.deusto.series_app.vo.Episodio;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Episodio>> _listDataChild;
 
    public ExpandableListAdapter(Context context, List<Episodio> episodios) {
        this._context = context;
        _listDataHeader = new ArrayList<String>();
        _listDataChild = new HashMap<String, List<Episodio>>();
        actualizarGroupYChildsData ( episodios );
    }
    
    public void setEpisodios ( List<Episodio> episodios ) 
    {
    	actualizarGroupYChildsData ( episodios );
    }
    
    private void actualizarGroupYChildsData ( List<Episodio> episodios ) {
    	
        Map<String, List<Episodio> > temporadasConEpisodios = ordenarEpisodiosEnTemporadas ( episodios );
        
        //Calculate the list for groups and childs
        for ( String temporada : temporadasConEpisodios.keySet() )
        {
        	String numeroTemporada = " Season " + ( Integer.parseInt(temporada) );
        	_listDataHeader.add(numeroTemporada);	
        	_listDataChild.put(numeroTemporada, temporadasConEpisodios.get(temporada) );
        }
    }
    
    private Map<String, List<Episodio> > ordenarEpisodiosEnTemporadas ( List <Episodio> episodios ){
    	Map<String, List<Episodio> > temporadasConEpisodios = new TreeMap<String, List<Episodio> >();
    	for ( Episodio episodio : episodios )
    	{
    		if ( temporadasConEpisodios.containsKey(episodio.getNumeroTemporada() ) )
    		{
    			List<Episodio> episodiosDeTemporada = temporadasConEpisodios.get(episodio.getNumeroTemporada() );
    			episodiosDeTemporada.add(episodio);
    		}
    		else
    		{
    			List<Episodio> lstEpisodios = new ArrayList<Episodio>();
    			lstEpisodios.add ( episodio );
    			temporadasConEpisodios.put(episodio.getNumeroTemporada(), lstEpisodios);
    		}
    	}
    	return temporadasConEpisodios;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
    	return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
    	return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        final Episodio episodio = (Episodio) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
 
        txtListChild.setText(episodio.getNombre());
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
    	return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
    	return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
    	return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
    	return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}