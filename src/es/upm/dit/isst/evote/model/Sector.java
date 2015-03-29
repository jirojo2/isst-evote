package es.upm.dit.isst.evote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Sector implements Serializable
{
	private static final long serialVersionUID = 1620366643383094032L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String nombre;
	private Float ponderacion;
	
	public Sector(String nombre, Float ponderacion)
	{
		this.nombre = nombre;
		this.ponderacion = ponderacion;
	}
	
	public Key id()
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
