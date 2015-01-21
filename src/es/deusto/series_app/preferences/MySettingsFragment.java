package es.deusto.series_app.preferences;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import es.deusto.series_app.R;
import es.deusto.series_app.SeriesListActivity;
import es.deusto.series_app.service.NotificationEpisodioService;

public class MySettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String KEY_ID = "user_id";
	public static final String KEY_USERNAME = "pref_username";
	public static final String KEY_EMAIL = "pref_useremail";
	public static final String KEY_FILTER_OPTIONS = "pref_filter_options";
	public static final String KEY_NOTIFICATION_ANDROID = "pref_notifications";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		findPreference(KEY_USERNAME).setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_USERNAME, ""));
		findPreference(KEY_EMAIL).setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_EMAIL, ""));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void registerAlarm(Context context) {

		if ( !SeriesListActivity.isAlarmRegistered() ) {
			Toast.makeText(getActivity(), "Alarm Registered", Toast.LENGTH_LONG).show();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, 10);
			Intent intent = new Intent(context, NotificationEpisodioService.class);
			PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
			AlarmManager alarm = (AlarmManager) getActivity().getSystemService(
					Context.ALARM_SERVICE);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					60 * 1000, pintent);
			SeriesListActivity.setPendingIntent(pintent);
			SeriesListActivity.setAlarmRegistered(true);
		}
	}

	public void cancelAlarm() {
		if (  SeriesListActivity.isAlarmRegistered())
		{
			Toast.makeText(getActivity(), "Alarm Canceled", Toast.LENGTH_LONG).show();
			AlarmManager alarm = (AlarmManager) getActivity().getSystemService(
					Context.ALARM_SERVICE);
			alarm.cancel(SeriesListActivity.getPendingIntent());
			SeriesListActivity.setAlarmRegistered(false);
		}
		else
		{
			Toast.makeText(getActivity(), "No Alarm Registered", Toast.LENGTH_LONG).show();
		}

	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if (key.contains(KEY_NOTIFICATION_ANDROID)) {
			Log.i("Ha cambiado: ", ""+sharedPreferences.getBoolean(KEY_NOTIFICATION_ANDROID, false) );
			
			if (sharedPreferences.getBoolean(KEY_NOTIFICATION_ANDROID, false)) {
				registerAlarm(getActivity());
			} else {
				cancelAlarm();
			}
		}
		else if ( key.contains(KEY_USERNAME)) {
			findPreference(key).setSummary(sharedPreferences.getString(KEY_USERNAME, ""));
		}
		else if ( key.contains(KEY_EMAIL)) {
			findPreference(key).setSummary(sharedPreferences.getString(KEY_EMAIL, ""));
		}
		
	}

}
