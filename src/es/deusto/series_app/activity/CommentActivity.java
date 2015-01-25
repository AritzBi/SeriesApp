package es.deusto.series_app.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.login.Session;
import es.deusto.series_app.task.GetLocation;
import es.deusto.series_app.task.IGetLocation;
import es.deusto.series_app.vo.Comment;
import es.deusto.series_app.vo.Episodio;

public class CommentActivity extends Activity implements IGetLocation {

	private String episodioId;
	
	private int usuarioId;
	
	private TextView commentCountry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_comment);
		
		Episodio episodio = (Episodio) getIntent().getExtras().getSerializable(Constantes.EPISODIO );
		
		if ( episodio != null )
		{
			this.episodioId = episodio.getId();
			
			TextView episodioName = (TextView) findViewById(R.id.commentEpisodioName);
			episodioName.setText(episodio.getNombre());
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
			
			CheckBox includeMyLocationCheck = (CheckBox) findViewById(R.id.chkIncluirLocalizacion);
			
			if ( includeMyLocationCheck.isChecked() && commentCountry != null )
			{
				comment.setLocalizacionUsuario( commentCountry.getText().toString() );
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
		else if ( id == R.id.action_point_location ) {
			GetLocation getLocation = new GetLocation(this, getApplicationContext() );
			getLocation.execute();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String getTexto () 
	{
		EditText editText = (EditText) findViewById(R.id.textoComment);
		return editText.getText().toString();
	}
	
	public void processReceivedLocation ( Location loc )
	{
		if ( loc != null )
		{
			double [] coords = new double[2];
			coords[0] = loc.getLatitude();
			coords[1] = loc.getLongitude();
			
			String country = getCountry(coords);
			if ( country != null )
			{
				commentCountry = (TextView) findViewById(R.id.commentLocation);
				commentCountry.setText(country);
			}
		}
	}
	
	private String getCountry(double[] coords){
		String result = "";
		
		try {
			Geocoder geo = new Geocoder(getApplicationContext());
			Address address = geo.getFromLocation(coords[0], coords[1], 1).get(0);
			result =  address.getLocality() + " , " + address.getPostalCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
