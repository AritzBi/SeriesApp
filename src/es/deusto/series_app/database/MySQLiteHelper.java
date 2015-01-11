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
	
	public static String TABLE_EPISODIO = "EPISODIO";
	public static String COLUMN_EPISODIO_ID = "id";
	public static String COLUMN_EPISODIO_RATING = "rating";
	public static String COLUMN_EPISODIO_DESCRIPCION = "descripcion";
	public static String COLUMN_EPISODIO_NOMBRE = "nombre";
	public static String COLUMN_EPISODIO_NUMERO_EPISODIO = "numeroEpisodio";
	public static String COLUMN_EPISODIO_NUMERO_TEMPORADA = "numeroTemporada";
	public static String COLUMN_EPISODIO_RUTA_IMAGEN = "rutaImagen";
	public static String COLUMN_EPISODIO_FECHA_EMISION = "fechaEmision";
	public static String COLUMN_EPISODIO_SERIE_ID = "serieId";
	
	public static String TABLE_USUARIO = "USUARIO";
	public static String COLUMN_USUARIO_ID = "id";
	public static String COLUMN_USUARIO_EMAIL = "email";
	public static String COLUMN_USUARIO_PASSWORD = "password";
	
	public static String TABLE_SERIES_FAVORITAS = "SERIES_FAVORITAS";
	public static String COLUMN_SERIES_FAVORITAS_USUARIO_ID = "userId";
	public static String COLUMN_SERIES_FAVORITAS_SERIE_ID = "serieId";
	
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_SERIE
			+ " ( " + COLUMN_SERIE_ID + " INTEGER NOT NULL UNIQUE, "
			+ COLUMN_SERIE_CADENA + " TEXT, " + COLUMN_SERIE_DESCRIPCION
			+ " TEXT, " + COLUMN_SERIE_NOMBRE + " TEXT NOT NULL, "
			+ COLUMN_SERIE_BANNER + "	TEXT, " + " PRIMARY KEY(id));";
	
	private static final String TABLE_EPISODE_CREATE = "CREATE TABLE " + TABLE_EPISODIO
			+ " ( " + COLUMN_EPISODIO_ID + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_EPISODIO_RATING + " TEXT, "
			+ COLUMN_EPISODIO_DESCRIPCION + " TEXT, "
			+ COLUMN_EPISODIO_NOMBRE + " TEXT, "
			+ COLUMN_EPISODIO_NUMERO_EPISODIO + " TEXT, "
			+ COLUMN_EPISODIO_NUMERO_TEMPORADA + " TEXT, "
			+ COLUMN_EPISODIO_RUTA_IMAGEN + " TEXT, "
			+ COLUMN_EPISODIO_FECHA_EMISION + " INTEGER, "
			+ COLUMN_EPISODIO_SERIE_ID + " TEXT, "
			+ " PRIMARY KEY(id) );";

	private static final String TABLE_USUARIO_CREATE = "CREATE TABLE " + TABLE_USUARIO
			+ " ( " + COLUMN_USUARIO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
			+ COLUMN_USUARIO_EMAIL + " TEXT NOT NULL UNIQUE, "
			+ COLUMN_USUARIO_PASSWORD + " TEXT NOT NULL );";
	
	private static final String TABLE_SERIES_FAVORITAS_CREATE = "CREATE TABLE " + TABLE_SERIES_FAVORITAS
			+ " ( " + COLUMN_SERIES_FAVORITAS_USUARIO_ID + " INTEGER NOT NULL, "
			+ COLUMN_SERIES_FAVORITAS_SERIE_ID + " TEXT NOT NULL, "
			+ " PRIMARY KEY(userId,serieId) );";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		db.execSQL(TABLE_EPISODE_CREATE);
		db.execSQL(TABLE_USUARIO_CREATE);
		db.execSQL(TABLE_SERIES_FAVORITAS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODIO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERIES_FAVORITAS);
		onCreate(db);
	}

}
