package es.upm.dit.isst.evote.cee.model;

import java.io.Serializable;

public class CEE implements Serializable
{
	private static final long serialVersionUID = 7485032254958286762L;
	
	private long id;
	private String nombre;
	private String clavePublica;
	
	public CEE()
	{
		
	}
	
	public CEE(long id, String nombre, String clavePublica)
	{
		this.id = id;
		this.nombre = nombre;
		this.clavePublica = clavePublica;
	}
	
	public long id()
	{
		return id;
	}
	
	public String clavePublica()
	{
		return clavePublica;
	}
	
	public void clavePublica(String value)
	{
		this.clavePublica = value;
	}
	
	public String nombre()
	{
		return nombre;
	}

	public void nombre(String value)
	{
		this.nombre = value;
	}
}
