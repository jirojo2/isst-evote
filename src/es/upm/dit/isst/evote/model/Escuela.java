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
public class Escuela implements Serializable
{
	private static final long serialVersionUID = 326176655652928106L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String nombre;

	public Escuela(String nombre) 
	{
		this.nombre = nombre;
	}
	
	public Key id()
	{
		return id;
	}
	
	public String nombre()
	{
		return nombre;
	}
}
