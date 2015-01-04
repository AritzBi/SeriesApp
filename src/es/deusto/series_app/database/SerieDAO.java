package es.deusto.series_app.database;

import java.util.ArrayList;
import java.util.List;

import es.deusto.series_app.Serie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SerieDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	private String[] allColumns = { MySQLiteHelper.COLUMN_SERIE_ID,
		      MySQLiteHelper.COLUMN_SERIE_NOMBRE, MySQLiteHelper.COLUMN_SERIE_DESCRIPCION, MySQLiteHelper.COLUMN_SERIE_CADENA, MySQLiteHelper.COLUMN_SERIE_BANNER };
	  
	public SerieDAO(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addSerie(Serie serie) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SERIE_ID, serie.getId());
		values.put(MySQLiteHelper.COLUMN_SERIE_NOMBRE, serie.getNombre());
		values.put(MySQLiteHelper.COLUMN_SERIE_DESCRIPCION,
				serie.getDescripcion());
		values.put(MySQLiteHelper.COLUMN_SERIE_CADENA, serie.getCadena());
		values.put(MySQLiteHelper.COLUMN_SERIE_BANNER, serie.getBannerPath());

		database.insert(MySQLiteHelper.TABLE_SERIE, null, values);
	}
	
	 public List<Serie> getAllSeries() {
		    List<Serie> series = new ArrayList<Serie>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_SERIE,
		        allColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Serie serie = cursorToComment(cursor);
		      series.add(serie);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    
		    return series;
	 }
	 
	  private Serie cursorToComment(Cursor cursor) {
		    Serie serie = new Serie();
		    serie.setId(cursor.getString(0));
		    serie.setNombre(cursor.getString(1));
		    serie.setDescripcion(cursor.getString(2).trim());
		    serie.setCadena(cursor.getString(3));
		    serie.setBannerPath(cursor.getString(4));
		    return serie;
		  }

}
