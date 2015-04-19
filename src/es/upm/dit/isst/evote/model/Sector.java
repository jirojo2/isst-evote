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
public class Sector implements Serializable
{
	private static final long serialVersionUID = 1620366643383094032L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Votacion votacion;
	
	private String nombre;
	private Float ponderacion;
	
	public Sector(Votacion votacion, String nombre, Float ponderacion)
	{
		this.votacion = votacion;
		this.nombre = nombre;
		this.ponderacion = ponderacion;
	}
	
	public Key id()
	{
		return id;
	}
	
	public Votacion votacion()
	{
		return votacion;
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
