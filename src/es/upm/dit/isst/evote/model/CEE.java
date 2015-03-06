package es.upm.dit.isst.evote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class CEE implements Serializable
{
	private static final long serialVersionUID = 3892068449111837614L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String nombre;
	private String clavePublica;
	
	public CEE(String nombre, String clavePublica)
	{
		this.nombre = nombre;
		this.clavePublica = clavePublica;
	}
	
	public Key id()
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
