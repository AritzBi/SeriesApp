package es.deusto.series_app.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import es.deusto.series_app.R;

public class MySettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	private static final String KEY_USERNAME = "pref_username";
	private static final String KEY_EMAIL = "pref_useremail";
	public static final String KEY_FILTER_OPTIONS = "pref_filter_options";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		findPreference(KEY_USERNAME).setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_USERNAME, ""));
		findPreference(KEY_EMAIL).setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_EMAIL, ""));
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

}
