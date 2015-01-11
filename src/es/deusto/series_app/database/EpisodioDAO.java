package es.deusto.series_app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import es.deusto.series_app.vo.Episodio;

public class EpisodioDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String[] allColumns = { MySQLiteHelper.COLUMN_EPISODIO_ID,
		      MySQLiteHelper.COLUMN_EPISODIO_DESCRIPCION, MySQLiteHelper.COLUMN_EPISODIO_NOMBRE, MySQLiteHelper.COLUMN_EPISODIO_NUMERO_EPISODIO, 
		      				MySQLiteHelper.COLUMN_EPISODIO_NUMERO_TEMPORADA, MySQLiteHelper.COLUMN_EPISODIO_RATING,MySQLiteHelper.COLUMN_EPISODIO_RUTA_IMAGEN,
		      										MySQLiteHelper.COLUMN_EPISODIO_FECHA_EMISION};
	  
	
	public EpisodioDAO ( Context context )
	{
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addEpisodio ( Episodio episodio )
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_EPISODIO_ID, episodio.getId());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_DESCRIPCION, episodio.getDescripcion());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_NOMBRE,
				episodio.getNombre());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_NUMERO_EPISODIO, episodio.getNumeroEpisodio());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_NUMERO_TEMPORADA, episodio.getNumeroTemporada());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_RATING, episodio.getRating());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_SERIE_ID, episodio.getSerieId());
		values.put(MySQLiteHelper.COLUMN_EPISODIO_RUTA_IMAGEN, episodio.getRutaImagen() );
		
		database.insert(MySQLiteHelper.TABLE_EPISODIO, null, values);
	}
	
	 public List<Episodio> findBySerieId( String idSerie ) {
		    
		 	List<Episodio> episodios = new ArrayList<Episodio>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_EPISODIO,
		        allColumns, MySQLiteHelper.COLUMN_EPISODIO_SERIE_ID + " = " + idSerie , null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Episodio episodio = cursorToComment(cursor);
		      episodios.add(episodio);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    
		    return episodios;
	 }
 
	  private Episodio cursorToComment(Cursor cursor) {
		    Episodio episodio = new Episodio();
		    episodio.setId(cursor.getString(0));
		    episodio.setDescripcion(cursor.getString(1));
		    episodio.setNombre(cursor.getString(2));
		    episodio.setNumeroEpisodio(cursor.getString(3));
		    episodio.setNumeroTemporada(cursor.getString(4));
		    episodio.setRating(cursor.getString(5));
		    episodio.setRutaImagen(cursor.getString(6));
		    episodio.setFechaEmision(cursor.getLong(7));
		    return episodio;
		  }

}
