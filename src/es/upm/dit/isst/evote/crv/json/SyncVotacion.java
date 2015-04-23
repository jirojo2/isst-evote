package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.upm.dit.isst.evote.cee.model.CEE;
import es.upm.dit.isst.evote.cee.model.Sector;
import es.upm.dit.isst.evote.cee.model.Votacion;
import es.upm.dit.isst.evote.cee.model.Candidato;

public class SyncVotacion
{
	private Votacion votacion;
	private CEE cee;
	private Candidato blanco;
	private List<Candidato> candidatos = new LinkedList<Candidato>();
	private List<Sector> sectores = new LinkedList<Sector>();
	private Map<Long, Integer> censo = new HashMap<Long, Integer>();
	
	public SyncVotacion()
	{
		
	}
	
	public Votacion getVotacion()
	{
		return votacion;
	}
	
	public void setVotacion(Votacion votacion)
	{
		this.votacion = votacion;
	}
	
	public CEE getCee()
	{
		return cee;
	}
	
	public void setCee(CEE cee)
	{
		this.cee = cee;
	}
	
	public Candidato getBlanco()
	{
		return blanco;
	}
	
	public void setBlanco(Candidato blanco)
	{
		this.blanco = blanco;
	}
	
	public List<Candidato> getCandidatos()
	{
		return candidatos;
	}
	
	public void setCandidatos(List<Candidato> candidatos)
	{
		this.candidatos = candidatos;
	}
	
	public List<Sector> getSectores()
	{
		return sectores;
	}
	
	public void setSectores(List<Sector> sectores)
	{
		this.sectores = sectores;
	}
	
	public Map<Long, Integer> getCenso()
	{
		return censo;
	}
	
	public void setCenso(Map<Long, Integer> censo)
	{
		this.censo = censo;
	}
}
