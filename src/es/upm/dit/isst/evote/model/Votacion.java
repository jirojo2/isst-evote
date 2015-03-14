package es.upm.dit.isst.evote.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Votacion implements Serializable
{
	private static final long serialVersionUID = 629275954361031820L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String nombre;	
	private Date fechaInicio;
	private Date fechaFin;
	
	public Key id()
	{
		return id;
	}
	
	public Votacion(String nombre)
	{
		this.nombre = nombre;
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
