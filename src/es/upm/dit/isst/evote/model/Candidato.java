package es.upm.dit.isst.evote.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Candidato implements Serializable
{
	private static final long serialVersionUID = 3683342280130412633L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Votacion votacion;
	
	private String nombre;
	private String apellidos;
	private String nif;
	
	public Candidato(Votacion votacion, String nif, String nombre, String apellidos)
	{
		this.votacion = votacion;
		this.nif = nif;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}
	
	public Key id()
	{
		return id;
	}	
	
	public Votacion votacion()
	{
		return votacion;
	}

	public String apellidos()
	{
		return apellidos;
	}

	public String nif()
	{
		return nif;
	}
	
	public String nombre()
	{
		return nombre;
	}
}
