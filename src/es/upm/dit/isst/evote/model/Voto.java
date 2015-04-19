package es.upm.dit.isst.evote.model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

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
	private Escuela escuela;

	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Sector sector;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Candidato candidato;
	
	/**
	 * Cuando el CEE ha emitido el voto
	 */
	private long timestampEmitido;
	
	/**
	 * Cuando se ha registrado el voto en el CRV
	 */
	private long timestamp;
	
	/**
	 * Número aleatorio generado por el CEE, incluído en la firma.
	 */
	private long nonce;
	
	/**
	 * Base64 encoded SHA512withRSA, firma emitida por el CEE emisor del voto
	 * Incluye los campos id_votacion, id_cee, id_escuela, id_sector, id_candidato, timestamp (CEE), nonce (CEE)
	 */
	private String firma;
	
	public Voto(Votacion votacion, CEE cee, Escuela escuela, Sector sector, Candidato candidato, long timestamp, long nonce, String firma)
	{
		this.votacion = votacion;
		this.cee = cee;
		this.escuela = escuela;
		this.sector = sector;
		this.candidato = candidato;
		
		this.timestampEmitido = timestamp;
		this.nonce = nonce;
		this.firma = firma;
		
		this.timestamp = new Date().getTime();
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

	public Escuela escuela()
	{
		return escuela;
	}

	public Sector sector()
	{
		return sector;
	}
	
	public Candidato candidato()
	{
		return candidato;
	}

	public long timestamp()
	{
		return timestamp;
	}

	public long timestampEmitido()
	{
		return timestampEmitido;
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
	
	/**
	 * Incluye los campos id_votacion, id_cee, id_escuela, id_mesa, id_candidato, timestamp (CEE), nonce (CEE)
	 * @return ByteBuffer preparado para comprobar la firma del voto con la clave pública del CEE
	 */
	public ByteBuffer datosParaValidarFirma()
	{
		ByteBuffer buffer = ByteBuffer.allocate(56);
		buffer.putLong(votacion.id().getId());
		buffer.putLong(cee.id().getId());
		buffer.putLong(escuela.id().getId());
		buffer.putLong(sector.id().getId());
		buffer.putLong(candidato.id().getId());
		buffer.putLong(timestampEmitido);
		buffer.putLong(nonce);
		return buffer;
	}
}
