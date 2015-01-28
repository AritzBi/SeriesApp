package es.deusto.series_app.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
	
	private ActionMode mActionMode = null;
	
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
		
		if ( this.episodio != null )
		{
			comments = commentDAO.findCommentByEpisodioId( this.episodio.getId() );
		}
		
		commentAdapter = new ArrayAdapter<Comment>(this,android.R.layout.simple_list_item_activated_2, android.R.id.text1,comments)
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
		
		final ListView listViewComments = (ListView) findViewById(R.id.listComments);
		
		listViewComments.setAdapter(commentAdapter);
		
		listViewComments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		listViewComments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		    // Called when the user long-clicks an item on the list
			@SuppressLint("NewApi") @Override
			public boolean onItemLongClick(AdapterView<?> parent, View row, int position, long rowid) {
		        if (mActionMode != null) {
		            return false;
		        }
		        Log.i("Entra","REntra");
		        // Important: to marked the editing row as activated
		        listViewComments.setItemChecked(position, true);
		        Log.i("Entra2","REntra2");
		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = EpisodioDetailActivity.this.startActionMode(mActionModeCallback);
		        Log.i("Entra3","REntra3");
		        mActionMode.setTag(position);
		        return true;
		    }
		});
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	    	MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.comment_bar, menu);
	        return true;
	    }

	    // Called when the user enters the action mode
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	    	ListView commentList = (ListView) EpisodioDetailActivity.this.findViewById(R.id.listComments);
	    	commentList.setEnabled(false);
	        return true;
	    }
	    
	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) { //te llega el elemento de menu que te ha llegado que ha sido clickado
	        //Take the position that has been clicked
	    	Log.i("Llega itemPosition ", "" + mode.getTag().toString() );
	    	final int itemPosition = Integer.parseInt(mode.getTag().toString());
	    	Log.i("Llega itemPosition ", "" + itemPosition );
	    	Comment comment = comments.get(itemPosition);
	    	switch (item.getItemId()) {
	            case R.id.mnu_delete_comment:
	            	
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								Comment deletedComment = comments.remove(itemPosition);
								commentDAO.removeComment(deletedComment);
								commentAdapter.notifyDataSetChanged();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								break;
							}
						}
					};
					
	            	AlertDialog.Builder builder = new AlertDialog.Builder(EpisodioDetailActivity.this);

					builder.setMessage("Delete comment");
					builder.setTitle("Comment: " + comment.getTexto() );

					// Add button OK
					builder.setPositiveButton(R.string.ok, dialogClickListener);
					builder.setNegativeButton(R.string.cancel,dialogClickListener);
					builder.show();
	                mode.finish(); // Action picked, so close the CAB and execute action
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	//If it is checked an item position, it is desired that when CAB is destroyed, we want to set the checkeditemposition to false
	    	ListView listComments = (ListView) EpisodioDetailActivity.this.findViewById(R.id.listComments);
	    	if (listComments.getCheckedItemPosition() >= 0)
			{
	    		listComments.setItemChecked(listComments.getCheckedItemPosition(),
						false);
			}
	    	listComments.setEnabled(true);
	        mActionMode = null;
	    }
	};
	
	@SuppressWarnings("unchecked")
	private void setEpisodio () {
		imageView = (ImageView) findViewById(R.id.episodeImage);
		
		TextView textViewName = (TextView) findViewById(R.id.episodeName);
		textViewName.setText(checkIfNull ( episodio.getNombre() ) );

		TextView textViewRating = (TextView) findViewById(R.id.episodeRating);
		textViewRating.setText(checkIfNull ( episodio.getRating() ) );
		
		TextView textViewIssueDate = (TextView)findViewById(R.id.episodeIssueDate);
		textViewIssueDate.setText(checkIfNull ( episodio.getFechaEmisionFormateada2() ) );
		
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
		if ( imagen != null )
		{
			imageView.setImageBitmap(imagen);
		}
		
		
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
					newComment.setId( (int) commentDAO.addComment(newComment) );
				}
			}
		}
		
	}
}
