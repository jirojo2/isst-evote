package es.upm.dit.isst.evote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Candidato implements Serializable
{
	private static final long serialVersionUID = 3683342280130412633L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String nombre;
	private String apellidos;
	private String nif;
	
	public Candidato(String nif, String nombre, String apellidos)
	{
		this.nif = nif;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}
	
	public Key id()
	{
		return id;
	}

	public String apellidos()
	{
		return apellidos;
	}

	public void apellidos(String apellidos)
	{
		this.apellidos = apellidos;
	}

	public String nif()
	{
		return nif;
	}

	public void nif(String nif)
	{
		this.nif = nif;
	}
	public String nombre()
	{
		return nombre;
	}
	public void nombre(String nombre)
	{
		this.nombre = nombre;
	}
}
