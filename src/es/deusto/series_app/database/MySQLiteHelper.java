package es.deusto.series_app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "series.db";
	private static final int DATABASE_VERSION = 1;

	public static String TABLE_SERIE = "SERIE";
	public static String COLUMN_SERIE_ID = "id";
	public static String COLUMN_SERIE_CADENA = "cadena";
	public static String COLUMN_SERIE_DESCRIPCION = "descripcion";
	public static String COLUMN_SERIE_NOMBRE = "nombre";
	public static String COLUMN_SERIE_BANNER = "banner";

	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_SERIE
			+ " ( " + COLUMN_SERIE_ID + " INTEGER NOT NULL UNIQUE, "
			+ COLUMN_SERIE_CADENA + " TEXT, " + COLUMN_SERIE_DESCRIPCION
			+ " TEXT, " + COLUMN_SERIE_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_SERIE_BANNER + "	TEXT, " + " PRIMARY KEY(id));";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIE);
		onCreate(db);
	}

}
