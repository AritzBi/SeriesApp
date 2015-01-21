package es.deusto.series_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.login.Session;
import es.deusto.series_app.vo.Comment;
import es.deusto.series_app.vo.Episodio;

public class CommentActivity extends Activity implements OnCheckedChangeListener{

	private String episodioId;
	
	private int usuarioId;
	
	private String localizacion = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_comment);
		
		CheckBox includeMyLocationCheck = (CheckBox) findViewById(R.id.chkIncluirLocalizacion);
		
		includeMyLocationCheck.setOnCheckedChangeListener(this);
		
		Episodio episodio = (Episodio) getIntent().getExtras().getSerializable(Constantes.EPISODIO );
		
		if ( episodio != null )
		{
			this.episodioId = episodio.getId();
		}
		
		Session session = new Session ( this );
		this.usuarioId = session.getId();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if ( id == R.id.action_add_comment) {
			//We create the comment and save into the database
			Comment comment = new Comment();
			
			comment.setIdEpisodio(episodioId);
			comment.setIdUsuario(usuarioId);
			comment.setTexto( getTexto() );
			
			if ( localizacion != null )
			{
				comment.setLocalizacionUsuario(localizacion);
			}
			
			Intent intent = new Intent();
			intent.putExtra(Constantes.NEW_COMMENT, comment);

			setResult(RESULT_OK, intent);

			finish();
			return true;
		}
		else if ( id == R.id.action_remove_comment ) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String getTexto () 
	{
		EditText editText = (EditText) findViewById(R.id.textoComment);
		return editText.getText().toString();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if ( isChecked )
		{
			//we retrieve the location
		}
	}
}
