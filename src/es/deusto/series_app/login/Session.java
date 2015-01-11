package es.deusto.series_app.login;

import es.deusto.series_app.database.UsuarioDAO;
import es.deusto.series_app.preferences.MySettingsFragment;
import es.deusto.series_app.vo.Usuario;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Session {

	private SharedPreferences preferences;
	private UsuarioDAO usuarioDAO;
	
	public Session(Context ctx, UsuarioDAO usuarioDAO ) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.usuarioDAO = usuarioDAO;
	}

	public void setEmail(String email) {
		preferences.edit().putString(MySettingsFragment.KEY_EMAIL, email)
				.commit();
		setUsername();
	}

	public String getEmail() {
		String email = preferences.getString(MySettingsFragment.KEY_EMAIL, "");
		return email;
	}

	public void setUsername() {
		String email = getEmail();
		if (email != null && !email.isEmpty()) {
			String[] username = null;
			if (email.contains("@")) {
				username = email.split("@");
				preferences
						.edit()
						.putString(MySettingsFragment.KEY_USERNAME, username[0])
						.commit();
			} else
				preferences.edit()
						.putString(MySettingsFragment.KEY_USERNAME, email)
						.commit();
		}
	}

	public Integer getId() {
		Integer id = preferences.getInt(MySettingsFragment.KEY_ID, 0);
		if ( id == null || id != 0 )
		{
			usuarioDAO.open();
			
			String email = getEmail();
			
			if ( !email.equals("") ) {
				
				Usuario usuario = usuarioDAO.getUsuarioByEmail(email);
				
				if (usuario != null) {
					preferences.edit()
					.putInt(MySettingsFragment.KEY_ID, usuario.getId())
					.commit();
				} else {
					Log.e("Database Error", "Email " + getEmail()
							+ " not found");
				}
			}
		}
		return id;
	}
}
