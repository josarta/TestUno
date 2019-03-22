package testuno.clases;

public class Mensaje {
	private String valor;
    private String descripcion;
	
	
	
	public Mensaje(String valor, String descripcion) {
		this.valor = valor;
		this.descripcion = descripcion;
	}
	
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
