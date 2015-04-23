package es.upm.dit.isst.evote.cee.model;

import java.io.Serializable;

public class Sector implements Serializable
{
	private static final long serialVersionUID = 5001982206887465710L;
	
	private long id;
	private String nombre;
	private Float ponderacion;
	
	public Sector()
	{
		
	}
	
	public Sector(long id, String nombre, Float ponderacion)
	{
		this.id = id;
		this.nombre = nombre;
		this.ponderacion = ponderacion;
	}
	
	public long id()
	{
		return id;
	}
	
	public String nombre()
	{
		return nombre;
	}
	
	public Float ponderacion()
	{
		return ponderacion;
	}
}
