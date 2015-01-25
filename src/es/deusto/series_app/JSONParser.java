package es.deusto.series_app;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import es.deusto.series_app.vo.Episodio;
import es.deusto.series_app.vo.Serie;

public class JSONParser {

	public static List<Episodio> parseEpisodios(JSONArray episodios, String serieId) {
		List<Episodio> listaEpisodios = new ArrayList<Episodio>();
		
		for (int i = 0; i < episodios.length(); i++) {

			try {
				JSONObject jsonEpisodio = episodios.getJSONObject(i);
				String rating = jsonEpisodio.getString("rating");
				String descripcion = jsonEpisodio.getString("overview");
				String imagePath = jsonEpisodio.getString("filename");
				String nombreEpisodio = jsonEpisodio.getString("episode_name");
				String numeroEpisodio = jsonEpisodio
						.getString("combined_episodenumber");
				JSONObject jsonFechaEmision = jsonEpisodio
						.getJSONObject("firstAired");
				Long fechaEmision = jsonFechaEmision.getLong("$date");
				String numeroTemporada = jsonEpisodio.getString("combined_season");
				String id = jsonEpisodio.getString("id");

				Episodio episodio = new Episodio();

				episodio.setRating(rating);
				episodio.setDescripcion(descripcion);
				episodio.setRutaImagen(Constantes.URL_RAIZ_API + imagePath);
				episodio.setNombre(nombreEpisodio);
				episodio.setNumeroEpisodio(numeroEpisodio);
				episodio.setNumeroTemporada(numeroTemporada);
				episodio.setFechaEmision(fechaEmision);
				episodio.setId(id);
				if (serieId != null)
					episodio.setSerieId(serieId);

				listaEpisodios.add(episodio);
			}
			catch ( JSONException e )
			{
				Log.e("Error", "Parsing json " + e.getMessage());
			}

		}
		
		return listaEpisodios;
	}
	
	public static List<Serie> parseSeries (JSONArray series ){
		List<Serie> listaSeries = new ArrayList<Serie>();
		
		try {
			for ( int i = 0; i < series.length(); i++ )
			{
				JSONObject jsonSerie = series.getJSONObject(i);
				String cadena = jsonSerie.getString("network");
				String descripcion = jsonSerie.getString("overview");
				String bannerPath = jsonSerie.getString("banner");
				String id = jsonSerie.getString("id");
				String nombre = jsonSerie.getString("name");
				boolean finished = jsonSerie.getBoolean("finished");
				Log.i("Seire " + nombre , "Finish " + finished );
				Serie serie = new Serie();
				serie.setCadena(cadena);
				serie.setDescripcion(descripcion);
				Log.e("Series", Constantes.URL_RAIZ_BANNERS_SERIES + bannerPath );
				serie.setBannerPath( Constantes.URL_RAIZ_BANNERS_SERIES + bannerPath);
				
				serie.setId(id);
				serie.setNombre(nombre);
				serie.setFinished(finished);
				
				listaSeries.add(serie);
			}
			
		} catch (JSONException e) {
			Log.e("Error", "Parsing json " + e.getMessage());
		}
		
		return listaSeries;
	}
}
