package es.upm.dit.isst.evote.cee.model;

import java.io.Serializable;

public class Voto implements Serializable
{
	private static final long serialVersionUID = 7281653896502134874L;

	private long id;	
	private Votacion votacion;	
	private CEE cee;
	private Sector sector;	
	private Candidato candidato;
	private long timestampEmitido;
	private long nonce;
	private String firma;
	
	public Voto(Votacion votacion, CEE cee, Sector sector, Candidato candidato, long timestamp, long nonce, String firma)
	{
		this.votacion = votacion;
		this.cee = cee;
		this.sector = sector;
		this.candidato = candidato;
		
		this.timestampEmitido = timestamp;
		this.nonce = nonce;
		this.firma = firma;
	}
	
	public long id()
	{
		return id;
	}
	
	public Votacion votacion()
	{
		return votacion;
	}
	
	public CEE cee()
	{
		return cee;
	}

	public Sector sector()
	{
		return sector;
	}
	
	public Candidato candidato()
	{
		return candidato;
	}

	public long timestampEmitido()
	{
		return timestampEmitido;
	}
	
	public long nonce()
	{
		return nonce;
	}
	
	public String firma()
	{
		return firma;
	}
	
	public float ponderacion()
	{
		if (sector == null)
			return 0.0f;
		return sector.ponderacion();
	}
}
