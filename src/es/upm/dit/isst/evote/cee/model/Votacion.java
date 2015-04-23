package es.upm.dit.isst.evote.cee.model;

import java.io.Serializable;
import java.util.Date;

public class Votacion implements Serializable
{
	private static final long serialVersionUID = 1388711265333542350L;
	
	private long id;	
	private String nombre;	
	private Date fechaInicio;
	private Date fechaFin;
	
	public Votacion()
	{
		
	}
	
	public Votacion(long id, String nombre)
	{
		this.id = id;
		this.nombre = nombre;
	}
	
	public long id()
	{
		return id;
	}
	
	public String nombre()
	{
		return nombre;
	}

	public Date fechaInicio()
	{
		return fechaInicio;
	}

	public Date fechaFin()
	{
		return fechaFin;
	}
}
