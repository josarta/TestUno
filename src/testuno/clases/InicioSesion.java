package testuno.clases;

public class InicioSesion {

	private String nombreUsuario;
	private String clave;
	
		
	/**
	 * @param nombreUsuario
	 * @param clave
	 */
	public InicioSesion(String nombreUsuario, String clave) {
		this.nombreUsuario = nombreUsuario;
		this.clave = clave;
	}
	/**
	 * @return the nombreUsuario
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	/**
	 * @param nombreUsuario the nombreUsuario to set
	 */
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	/**
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}
	/**
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	
	
	
}
