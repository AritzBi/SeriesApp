package es.deusto.series_app.vo;

public class SerieFavorita {

	private int idUser;
	private String idSerie;
	
	public SerieFavorita () 
	{
		
	}
	
	public SerieFavorita ( int userId, String serieId )
	{
		idUser = userId;
		idSerie = serieId;
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getIdSerie() {
		return idSerie;
	}
	public void setIdSerie(String idSerie) {
		this.idSerie = idSerie;
	}
}
