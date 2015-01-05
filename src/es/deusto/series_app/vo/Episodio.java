package es.deusto.series_app.vo;

import java.io.Serializable;
import java.sql.Date;

public class Episodio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String rating;
	private String descripcion;
	private String nombre;
	private String numeroEpisodio;
	private String numeroTemporada;
	private Date fechaEmision;
	private String rutaImagen;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNumeroEpisodio() {
		return numeroEpisodio;
	}
	public void setNumeroEpisodio(String numeroEpisodio) {
		this.numeroEpisodio = numeroEpisodio;
	}
	public String getNumeroTemporada() {
		return numeroTemporada;
	}
	public void setNumeroTemporada(String numeroTemporada) {
		this.numeroTemporada = numeroTemporada;
	}
	public Date getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getRutaImagen() {
		return rutaImagen;
	}
	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
	

}
