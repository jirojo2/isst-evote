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
public class MesaElectoral implements Serializable
{
	private static final long serialVersionUID = 898337253558091432L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	
	private String codigo;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@Unowned
	private Escuela escuela;
	
	public MesaElectoral(String codigo, Escuela escuela)
	{
		this.codigo = codigo;
		this.escuela = escuela;
	}

	public Key id()
	{
		return id;
	}

	public String codigo()
	{
		return codigo;
	}

	public Escuela escuela()
	{
		return escuela;
	}	
}
