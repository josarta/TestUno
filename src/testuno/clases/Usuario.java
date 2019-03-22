package testuno.clases;

public class Usuario {
	
	private String tipoDocumento;
	private Long numeroDocumento;
	private String nombres;
	private String apellidos;
	private String correo;
		
	
public Usuario(String tipoDocumento, Long numeroDocumento, String nombres, String apellidos, String correo) {
		super();
		this.tipoDocumento = tipoDocumento;
		this.numeroDocumento = numeroDocumento;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.correo = correo;
	}




static public boolean validarUsuario(String nombreUsuario, String clave) {
		try {
			return nombreUsuario.equals("josarta")?(clave.equals("1234")):false;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	
	public Long getNumeroDocumento() {
		return numeroDocumento;
	}
	
	public String getNombres() {
		return nombres;
	}
	
	public String getApellidos() {
		return apellidos;
	}
	
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	
	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorreo() {
		return correo;
	}


	public void setCorreo(String correo) {
		this.correo = correo;
	}

	
	
}
