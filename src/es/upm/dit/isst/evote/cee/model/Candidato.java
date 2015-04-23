package es.upm.dit.isst.evote.cee.model;

import java.io.Serializable;

public class Candidato implements Serializable
{
	private static final long serialVersionUID = -8916337037153726673L;
	
	private long id;	
	private String nombre;
	private String apellidos;
	private String nif;
	
	public Candidato()
	{
		
	}
	
	public Candidato(long id, String nif, String nombre, String apellidos)
	{
		this.id = id;
		this.nif = nif;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}
	
	public long id()
	{
		return id;
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
