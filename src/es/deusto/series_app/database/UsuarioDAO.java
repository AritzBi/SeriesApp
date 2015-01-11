package es.deusto.series_app.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import es.deusto.series_app.vo.Usuario;

public class UsuarioDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String[] allColumns = { MySQLiteHelper.COLUMN_USUARIO_EMAIL,
		      MySQLiteHelper.COLUMN_USUARIO_ID, MySQLiteHelper.COLUMN_USUARIO_PASSWORD  };
	  
	
	public UsuarioDAO ( Context context )
	{
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addUsuario ( Usuario usuario )
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_USUARIO_EMAIL, usuario.getEmail() );
		values.put(MySQLiteHelper.COLUMN_USUARIO_PASSWORD, getPasswordMD5( usuario.getPassword() ) );
		
		database.insert(MySQLiteHelper.TABLE_USUARIO, null, values);
	}
	
	public Usuario getUsuarioByEmail ( String email )
	{
		Usuario usuario = null;

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_USUARIO,
	        allColumns, MySQLiteHelper.COLUMN_USUARIO_EMAIL + " = ?" , new String[]{email}, null, null, null);
	    
	    if ( cursor.getCount() >= 1 )
	    {
	    	cursor.moveToFirst();
	    	usuario = cursorToUsuario(cursor);
	    }
	    cursor.close();
	    
	    return usuario;
	}
	
	public static String getPasswordMD5 ( String password )
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
	
	 private Usuario cursorToUsuario(Cursor cursor) {
		 	Usuario usuario = new Usuario();
		 	usuario.setEmail ( cursor.getString(0) );
		 	usuario.setId(cursor.getInt(1));
		 	usuario.setPassword(cursor.getString(2));
		    return usuario;
	 }
	 
}
