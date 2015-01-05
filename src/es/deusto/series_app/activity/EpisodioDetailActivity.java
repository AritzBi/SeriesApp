package es.deusto.series_app.activity;

import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.R.id;
import es.deusto.series_app.R.layout;
import es.deusto.series_app.R.menu;
import es.deusto.series_app.vo.Episodio;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EpisodioDetailActivity extends Activity {

	private Episodio episodio = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_episodio_detail);
		
		Episodio episodio = null;
		if ( getIntent().getExtras() != null )
		{
			episodio = (Episodio) getIntent().getExtras().getSerializable(Constantes.INFO_EPISODIO);
			this.episodio = episodio;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.episodio_detail, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
