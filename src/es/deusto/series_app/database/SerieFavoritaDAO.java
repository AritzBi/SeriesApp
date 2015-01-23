package es.deusto.series_app.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.task.CallAPI;
import es.deusto.series_app.vo.SerieFavorita;

public class SerieFavoritaDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context context;
	
	private String[] allColumns = { MySQLiteHelper.COLUMN_SERIES_FAVORITAS_SERIE_ID,
		      MySQLiteHelper.COLUMN_SERIES_FAVORITAS_USUARIO_ID };
	
	public SerieFavoritaDAO ( Context context )
	{
		dbHelper = new MySQLiteHelper(context);
		this.context = context;
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addSerieFavorita ( SerieFavorita serieFavorita )
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SERIES_FAVORITAS_SERIE_ID, serieFavorita.getIdSerie());
		values.put(MySQLiteHelper.COLUMN_SERIES_FAVORITAS_USUARIO_ID, serieFavorita.getIdUser());

		//We need to add this value over the API
		Map<String,String> parametros = new HashMap<String,String> ();
		parametros.put("user_id", String.valueOf( serieFavorita.getIdUser() ) );
		parametros.put("serie_id", serieFavorita.getIdSerie());
		
		CallAPI callAPI = new CallAPI(context, null);
		callAPI.setGetOrPost(false);
		callAPI.setParametros(parametros);
		callAPI.setJsonResponse(false);
		callAPI.execute(Constantes.URL_ADD_SERIE_FAVORITA);
		
		database.insert(MySQLiteHelper.TABLE_SERIES_FAVORITAS, null, values);
	}
	
	public void deleteSerieFavorita ( SerieFavorita serieFavorita )
	{
		if ( serieFavorita != null )
		{
			Map<String,String> parametros = new HashMap<String,String> ();
			parametros.put("user_id", String.valueOf( serieFavorita.getIdUser() ) );
			parametros.put("serie_id", serieFavorita.getIdSerie());
			
			CallAPI callAPI = new CallAPI(context, null);
			callAPI.setGetOrPost(false);
			callAPI.setParametros(parametros);
			callAPI.setJsonResponse(false);
			callAPI.execute(Constantes.URL_REMOVE_SERIE_FAVORITA);
			
			database.delete(MySQLiteHelper.TABLE_SERIES_FAVORITAS, MySQLiteHelper.COLUMN_SERIES_FAVORITAS_SERIE_ID + " = " + serieFavorita.getIdSerie() + " AND " + MySQLiteHelper.COLUMN_SERIES_FAVORITAS_USUARIO_ID + " = " + serieFavorita.getIdUser(), null);
		}
	}

	public boolean existeSerieFavoritaByPk ( Integer userId, String serieId )
	{
		return getSerieFavoritaByPK(userId, serieId) != null;
	}
	public SerieFavorita getSerieFavoritaByPK ( Integer userId, String serieId )
	{
		SerieFavorita serieFavorita = null;

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_SERIES_FAVORITAS,
	        allColumns, MySQLiteHelper.COLUMN_SERIES_FAVORITAS_SERIE_ID + " = " + serieId + " AND " + MySQLiteHelper.COLUMN_SERIES_FAVORITAS_USUARIO_ID + " = " + userId , null, null, null, null);

	    if ( cursor.getCount() > 0 )
	    {
	    	cursor.moveToFirst();
	    	serieFavorita = cursorToSerieFavorita ( cursor ); 
	    }
	    
	    cursor.close();
	    
	    return serieFavorita;
	}
	
	public List<String> findSeriesFavroitesByUserId ( int userId )
	{
		List<SerieFavorita> seriesFavoritas = new ArrayList<SerieFavorita>();
		List<String> codigosSeries = new ArrayList<String>();
		
		Cursor cursor =  database.query(MySQLiteHelper.TABLE_SERIES_FAVORITAS,
		        allColumns, MySQLiteHelper.COLUMN_SERIES_FAVORITAS_USUARIO_ID + " = " + userId , null, null, null, null);
	
		if ( cursor.getCount() > 0 )
		{
			cursor.moveToFirst();
			while ( !cursor.isAfterLast() )
			{
				SerieFavorita serieFavorita = cursorToSerieFavorita(cursor);
				seriesFavoritas.add(serieFavorita);
				cursor.moveToNext();
			}
		}
		
		if ( seriesFavoritas.size() > 0 )
		{
			for ( SerieFavorita serieFavorita : seriesFavoritas )
			{
				codigosSeries.add(serieFavorita.getIdSerie());
			}
		}
		
		return codigosSeries;
	}
	
	private SerieFavorita cursorToSerieFavorita ( Cursor cursor )
	{
		SerieFavorita serieFavorita = new SerieFavorita();
		serieFavorita.setIdSerie(cursor.getString(0));
		serieFavorita.setIdUser(cursor.getInt(1));
		
		return serieFavorita;
	}
	
}
