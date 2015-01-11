package es.deusto.series_app.login;

import es.deusto.series_app.preferences.MySettingsFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

	private SharedPreferences preferences;
	
	public Session ( Context ctx )
	{
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public void setEmail ( String email )
	{
		preferences.edit().putString(MySettingsFragment.KEY_EMAIL, email).commit();
	}
	
	public String getEmail ( )
	{
		String email = preferences.getString(MySettingsFragment.KEY_EMAIL,"");
		return email;
	}
}
