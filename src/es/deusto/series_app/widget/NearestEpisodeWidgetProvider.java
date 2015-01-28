package es.deusto.series_app.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.RemoteViews;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.activity.EpisodioDetailActivity;
import es.deusto.series_app.database.EpisodioDAO;
import es.deusto.series_app.database.SerieFavoritaDAO;
import es.deusto.series_app.login.Session;
import es.deusto.series_app.task.ConvertToBitmap;
import es.deusto.series_app.task.IConvertToBitmap;
import es.deusto.series_app.vo.Episodio;

public class NearestEpisodeWidgetProvider extends AppWidgetProvider implements
		IConvertToBitmap {

	private Context context;
	private AppWidgetManager appWidgetManager;
	private int[] appWidgetIds;
	private Episodio episodioMasCercano;

	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		this.context = context;
		this.appWidgetManager = appWidgetManager;
		this.appWidgetIds = appWidgetIds;

		Episodio episodioMasCercano = getEpisodioMasCercano();
		Log.i("Episodio mas cercano", "" + episodioMasCercano);
		List<String> rutasImagenes = new ArrayList<String>();
		if (episodioMasCercano != null
				&& episodioMasCercano.getRutaImagen() != null) {
			this.episodioMasCercano = episodioMasCercano;
			rutasImagenes.add(episodioMasCercano.getRutaImagen());
			ConvertToBitmap convertToBitMap = new ConvertToBitmap(context, this);
			convertToBitMap.execute(rutasImagenes);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public Episodio getEpisodioMasCercano() {

		Session session = new Session(context);
		int userId = session.getId();

		Episodio episodioMasCercano = null;

		// Existe un usuario con la sesión establecida
		if (userId != 0) {
			SerieFavoritaDAO serieFavoritaDAO = new SerieFavoritaDAO(context);
			serieFavoritaDAO.open();
			List<String> seriesFavoritas = serieFavoritaDAO
					.findSeriesFavroitesByUserId(userId);
			serieFavoritaDAO.close();
			// Si el usuario tiene series favoritas establecidas
			if (seriesFavoritas != null && seriesFavoritas.size() > 0) {
				EpisodioDAO episodioDAO = new EpisodioDAO(context);
				episodioDAO.open();

				Long tiempoMasCercano = null;
				for (String serieFavorita : seriesFavoritas) {
					List<Episodio> episodios = episodioDAO
							.findBySerieId(serieFavorita);
					for (Episodio episodio : episodios) {
						long ahora = new Date().getTime();
						long diff = episodio.getFechaEmision() - ahora;
						if (tiempoMasCercano == null && diff > 0) {
							tiempoMasCercano = diff;
							episodioMasCercano = episodio;
						} else {
							if (diff > 0 && diff < tiempoMasCercano) {
								tiempoMasCercano = diff;
								episodioMasCercano = episodio;
							}
						}
					}

				}
				episodioDAO.close();
			}
		}
		return episodioMasCercano;
	}

	public void updateWidgetWithSoonerEpisodio(Episodio episodio, int widgetId,
			Bitmap imagen) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		Log.i("Episodio Widget: ", "" + episodio);

		if (episodio != null) {
			views.setTextViewText(R.id.textLabel, episodio.getNombre());
			if (imagen != null) {
				setBitmap(views, R.id.imageLabel, imagen);
			}
		}

		Intent intent = new Intent(context, EpisodioDetailActivity.class);
		Log.i("Episodio enviado", "" + episodio);
		intent.putExtra(Constantes.INFO_EPISODIO, episodio);
		intent.setAction("com.blah.Action");
		PendingIntent pendIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Log.i("Pending Intent", "" + pendIntent);
		views.setOnClickPendingIntent(R.id.textLabel, pendIntent);
		views.setOnClickPendingIntent(R.id.imageLabel, pendIntent);
		appWidgetManager.updateAppWidget(widgetId, views);
	}

	private void setBitmap(RemoteViews views, int resId, Bitmap bitmap) {
		Bitmap proxy = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(proxy);
		c.drawBitmap(bitmap, new Matrix(), null);
		views.setImageViewBitmap(resId, proxy);
	}

	@Override
	public void processBitmapsForBannersPath(
			Map<String, Bitmap> bitmapsBannersPath) {
		Log.i("Me llega", "" + bitmapsBannersPath);
		Bitmap image = bitmapsBannersPath.get(episodioMasCercano
				.getRutaImagen());

		// We must iterate all the widget instances
		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			updateWidgetWithSoonerEpisodio(episodioMasCercano, widgetId, image);
		}
	}
}
