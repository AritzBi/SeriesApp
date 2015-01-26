package es.deusto.series_app.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import es.deusto.series_app.vo.Comment;

public class CommentDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String[] allColumns = { MySQLiteHelper.COLUMN_COMMENT_EPISODIO_ID,MySQLiteHelper.COLUMN_COMMENT_LOCALIZACION_USUARIO,
		      MySQLiteHelper.COLUMN_COMMENT_USUARIO_ID, MySQLiteHelper.COLUMN_COMMENT_TEXTO };
	
	public CommentDAO ( Context context )
	{
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public void addComment ( Comment comment )
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_COMMENT_EPISODIO_ID, comment.getIdEpisodio() );
		values.put(MySQLiteHelper.COLUMN_COMMENT_USUARIO_ID, comment.getIdUsuario() );
		values.put(MySQLiteHelper.COLUMN_COMMENT_TEXTO, comment.getTexto());
		values.put(MySQLiteHelper.COLUMN_COMMENT_LOCALIZACION_USUARIO, comment.getLocalizacionUsuario());

		database.insert(MySQLiteHelper.TABLE_COMMENT, null, values);
	}
	
	public void removeComment ( Comment comment )
	{
		database.delete(MySQLiteHelper.TABLE_COMMENT, MySQLiteHelper.COLUMN_COMMENT_EPISODIO_ID + " = " + comment.getIdEpisodio() + " AND " + MySQLiteHelper.COLUMN_COMMENT_USUARIO_ID + " = " + comment.getIdUsuario(), null);
	}
	
	public List<Comment> findCommentByEpisodioId ( String id )
	{
	 	List<Comment> comentarios = new ArrayList<Comment>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENT,
	        allColumns, MySQLiteHelper.COLUMN_COMMENT_EPISODIO_ID + " = " + id , null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Comment comment = cursorToComment(cursor);
	      comentarios.add(comment);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    
	    return comentarios;
	}
	
	private Comment cursorToComment ( Cursor cursor )
	{
		Comment comment = new Comment();
		comment.setIdEpisodio(cursor.getString(0));
		comment.setLocalizacionUsuario(cursor.getString(1));
		comment.setIdUsuario(cursor.getInt(2));
		comment.setTexto(cursor.getString(3));
		
		return comment;
	}
}
