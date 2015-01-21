package es.deusto.series_app.vo;

import java.io.Serializable;

public class Comment implements Serializable {

	private int idUsuario;
	private String idEpisodio;
	private String texto;
	private String localizacionUsuario;
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getIdEpisodio() {
		return idEpisodio;
	}
	public void setIdEpisodio(String idEpisodio) {
		this.idEpisodio = idEpisodio;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getLocalizacionUsuario() {
		return localizacionUsuario;
	}
	public void setLocalizacionUsuario(String localizacionUsuario) {
		this.localizacionUsuario = localizacionUsuario;
	}
	
}
