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
	private MesaElectoral mesa;
	
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
	
	private float ponderacion;
	
	/**
	 * Base64 encoded SHA512withRSA, firma emitida por el CEE emisor del voto
	 * Incluye los campos id_votacion, id_cee, id_escuela, id_mesa, id_candidato, timestamp (CEE)
	 */
	private String firma;
	
	public Voto(Votacion votacion, CEE cee, Escuela escuela, MesaElectoral mesa, Candidato candidato, long timestamp, String firma, float ponderacion)
	{
		this.votacion = votacion;
		this.cee = cee;
		this.escuela = escuela;
		this.mesa = mesa;
		this.candidato = candidato;
		
		this.ponderacion = ponderacion;
		
		this.timestampEmitido = timestamp;
		this.firma = firma;
		
		this.timestamp = new Date().getTime();
	}
	
	public Voto(Votacion votacion, CEE cee, Escuela escuela, MesaElectoral mesa, Candidato candidato, long timestamp, String firma)
	{
		this(votacion, cee, escuela, mesa, candidato, timestamp, firma, 1.0f);
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

	public MesaElectoral mesa()
	{
		return mesa;
	}
	
	public Candidato candidato()
	{
		return candidato;
	}

	public float ponderacion()
	{
		return ponderacion;
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
	
	/**
	 * Incluye los campos id_votacion, id_cee, id_escuela, id_mesa, id_candidato, timestamp (CEE)
	 * @return ByteBuffer preparado para comprobar la firma del voto con la clave pública del CEE
	 */
	public ByteBuffer datosParaValidarFirma()
	{
		ByteBuffer buffer = ByteBuffer.allocate(48);
		buffer.putLong(votacion.id().getId());
		buffer.putLong(cee.id().getId());
		buffer.putLong(escuela.id().getId());
		buffer.putLong(mesa.id().getId());
		buffer.putLong(candidato.id().getId());
		buffer.putLong(timestampEmitido);
		return buffer;
	}
}
