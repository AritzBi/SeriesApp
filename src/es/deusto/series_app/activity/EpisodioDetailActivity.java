package es.deusto.series_app.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.ConvertToBitmap;
import es.deusto.series_app.IConvertToBitmap;
import es.deusto.series_app.R;
import es.deusto.series_app.vo.Episodio;

public class EpisodioDetailActivity extends Activity implements IConvertToBitmap {

	private Episodio episodio = null;
	
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_episodio_detail);
		
		Episodio episodio = null;
		if ( getIntent().getExtras() != null )
		{
			episodio = (Episodio) getIntent().getExtras().getSerializable(Constantes.INFO_EPISODIO);
			this.episodio = episodio;
			setEpisodio();
		}
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void processBitmapsForBannersPath(
			Map<String, Bitmap> bitmapsBannersPath) {
	
		Bitmap imagen = bitmapsBannersPath.get(episodio.getRutaImagen() );
		imageView.setImageBitmap(imagen);
		
	}
}
