package es.upm.dit.isst.evote.model;

import java.io.Serializable;
import java.nio.ByteBuffer;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Voto implements Serializable
{
	private static final long serialVersionUID = 624719311074542436L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Votacion votacion;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private CEE cee;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Candidato candidato;
	
	private long timestamp;
	
	private float ponderacion;
	
	/**
	 * Base64 encoded SHA512withRSA, firma emitida por el CEE emisor del voto
	 * Incluye los campos id_votacion, id_cee, id_candidato, timestamp
	 */
	private String firma;
	
	public Voto(Votacion votacion, CEE cee, Candidato candidato, long timestamp, String firma, float ponderacion)
	{
		this.votacion = votacion;
		this.cee = cee;
		this.candidato = candidato;
		
		this.ponderacion = ponderacion;
		
		this.timestamp = timestamp;
		this.firma = firma;
	}
	
	public Voto(Votacion votacion, CEE cee, Candidato candidato, long timestamp, String firma)
	{
		this(votacion, cee, candidato, timestamp, firma, 1.0f);
	}
	
	public Key id()
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
	
	public Candidato candidato()
	{
		return candidato;
	}

	public float ponderacion()
	{
		return ponderacion;
	}
	
	public String firma()
	{
		return firma;
	}
	
	public ByteBuffer datosParaValidarFirma()
	{
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.putLong(votacion.id().getId());
		buffer.putLong(cee.id().getId());
		buffer.putLong(candidato.id().getId());
		buffer.putLong(timestamp);
		return buffer;
	}
}
