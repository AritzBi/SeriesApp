package es.deusto.series_app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import es.deusto.series_app.Constantes;
import es.deusto.series_app.R;
import es.deusto.series_app.SeriesListActivity;
import es.deusto.series_app.activity.EpisodioDetailActivity;
import es.deusto.series_app.database.EpisodioDAO;
import es.deusto.series_app.database.SerieFavoritaDAO;
import es.deusto.series_app.login.Session;
import es.deusto.series_app.vo.Episodio;

public class NotificationEpisodioService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		   Log.i("Entra NotificationEpisodioService","NotificationEpisodioService");
	        if ( SeriesListActivity.isAlarmRegistered() )
	        {
	        	Session session = new Session ( this );
	        	int userId = session.getId();
	        	
	        	//Existe un usuario con la sesión establecida
	        	if ( userId != 0 )
	        	{
	        		SerieFavoritaDAO serieFavoritaDAO = new SerieFavoritaDAO(this);
	        		serieFavoritaDAO.open();
	        		List<String> seriesFavoritas = serieFavoritaDAO.findSeriesFavroitesByUserId(userId);
	        		serieFavoritaDAO.close();
	        		//Si el usuario tiene series favoritas establecidas
	        		if ( seriesFavoritas != null && seriesFavoritas.size() > 0 )
	        		{
	        			EpisodioDAO episodioDAO = new EpisodioDAO(this);
	        			episodioDAO.open();
	        			
	        			List<Episodio> episodiosANotificar = new ArrayList<Episodio>();
	        			for ( String serieFavorita :seriesFavoritas )
	        			{
	        				//Se comprueba si algun episodio va a empezar dentro de 4 horas
	        				List<Episodio> episodios = episodioDAO.findBySerieId(serieFavorita);
	        				for ( Episodio episodio : episodios )
	        				{
	        					Date ahora = new Date();
	        					ahora.setDate(11);
	        					ahora.setMinutes(0);
	        					ahora.setHours(21);
	        					Log.i("Fecha ahora", "" + ahora );
	        					Date newDate = episodio.getFechaEmisionDate();
	        					newDate.setMinutes(ahora.getMinutes());
	        					newDate.setSeconds(ahora.getSeconds());
	        					episodio.setFechaEmision( newDate.getTime() );
	        					
	        					Log.i("Fecha episodio", "" + episodio.getFechaEmisionDate() );

	        			        long diff = episodio.getFechaEmisionDate().getTime() - ahora.getTime();
	        			        long diffHours = diff / (60 * 60 * 1000) + 1;
	        			        Log.i("Diff hours", " " + diffHours);
	        					if ( diffHours == 4 )
	        					{
	        						episodiosANotificar.add(episodio);
	        					}
	        				}
	        			}
	        			if ( episodiosANotificar.size() > 0 )
	        			{
	        				int i = 0;
	        				for ( Episodio episodio : episodiosANotificar )
	        				{
	        					if ( episodio != null )
	        					{
	        						showNotification(getApplicationContext(), episodio, i);
	        						i++;
	        					}
	        				}
	        			}
	        			episodiosANotificar.clear();
	        		}
	        	}
	        }
	        
	        return START_STICKY;
	}
	
	private void showNotification(Context context, Episodio episodio, int notificationPosition ){
		// First, create the notification
		NotificationCompat.Builder nBuilder =
				new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_action_time)
				.setContentTitle("Episode " + episodio.getNombre() )
				.setAutoCancel(true)
				.setContentText("Start time at " + episodio.getFechaEmisionFormateada() );
		
		Intent i = new Intent(this, EpisodioDetailActivity.class);
		i.setAction("android.intent.action.MAIN");
		i.addCategory("android.intent.category.LAUNCHER");
		i.putExtra(Constantes.INFO_EPISODIO , episodio );
		nBuilder.setContentIntent(PendingIntent.getActivity(this, 0 , i , Intent.FLAG_ACTIVITY_NEW_TASK));
		
		Notification noti = nBuilder.build();

		// Second, display the notification
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationPosition, noti);
		
	}

}
