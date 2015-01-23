package es.deusto.series_app.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.database.CommentDAO;
import es.deusto.series_app.task.ConvertToBitmap;
import es.deusto.series_app.task.IConvertToBitmap;
import es.deusto.series_app.vo.Comment;
import es.deusto.series_app.vo.Episodio;

public class EpisodioDetailActivity extends Activity implements IConvertToBitmap {

	private Episodio episodio = null;
	
	private ImageView imageView;
	
	private List<Comment> comments;
	
	private ArrayAdapter<Comment> commentAdapter;
	
	private CommentDAO commentDAO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_episodio_detail);
		
		comments = new ArrayList<Comment>();
		
		commentDAO = new CommentDAO(this);
		commentDAO.open();
		
		Episodio episodio = null;
		if ( getIntent().getExtras() != null )
		{
			episodio = (Episodio) getIntent().getExtras().getSerializable(Constantes.INFO_EPISODIO);
			this.episodio = episodio;
			setEpisodio();
		}
		
		ListView listViewComments = (ListView) findViewById(R.id.listComments);
		
		commentAdapter = new ArrayAdapter<Comment>(this,android.R.layout.simple_list_item_2, android.R.id.text1,comments)
			{
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				TextView text1 = (TextView) view
						.findViewById(android.R.id.text1);
				TextView text2 = (TextView) view
						.findViewById(android.R.id.text2);
				
				text1.setText(comments.get(position).getTexto());
				text2.setText(comments.get(position).getLocalizacionUsuario());
				return view;
			}
		};
		
		listViewComments.setAdapter(commentAdapter);
	}
	
	@SuppressWarnings("unchecked")
	private void setEpisodio () {
		imageView = (ImageView) findViewById(R.id.episodeImage);
		
		TextView textViewName = (TextView) findViewById(R.id.episodeName);
		textViewName.setText(checkIfNull ( episodio.getNombre() ) );

		TextView textViewRating = (TextView) findViewById(R.id.episodeRating);
		textViewRating.setText(checkIfNull ( episodio.getRating() ) );
		
		TextView textViewIssueDate = (TextView)findViewById(R.id.episodeIssueDate);
		textViewIssueDate.setText(checkIfNull ( episodio.getFechaEmisionFormateada() ) );
		
		comments = commentDAO.findCommentByEpisodioId(episodio.getId());
		
		List<String> path = new ArrayList<String>();
		path.add(episodio.getRutaImagen());
		ConvertToBitmap convertToBitmap = new ConvertToBitmap(getApplicationContext(), this);
		convertToBitmap.execute( path );
		
	}
	
	private String checkIfNull ( String value )
	{
		if ( value != null && !value.isEmpty() )
			return value;
		else
			return "Undefined";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.episodio_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if ( id == R.id.action_addcoment ) {
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra( Constantes.EPISODIO, episodio );
			startActivityForResult(intent, Constantes.ADD_COMMENT);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void processBitmapsForBannersPath(
			Map<String, Bitmap> bitmapsBannersPath) {
	
		Bitmap imagen = bitmapsBannersPath.get(episodio.getRutaImagen() );
		imageView.setImageBitmap(imagen);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( requestCode == Constantes.ADD_COMMENT )
		{
			if ( resultCode == RESULT_OK )
			{
				Comment newComment = (Comment) data.getExtras().getSerializable(Constantes.NEW_COMMENT);
				if ( newComment != null )
				{
					comments.add(newComment);
					commentAdapter.notifyDataSetChanged();
				}
			}
		}
		
	}
}
