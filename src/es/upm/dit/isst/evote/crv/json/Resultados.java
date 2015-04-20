package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;
import java.util.LinkedList;

import es.upm.dit.isst.evote.model.Sector;

public class Resultados
{	
	private HashMap<Sector, Integer> votosEmitidos = new HashMap<Sector, Integer>();
	private HashMap<Sector, Integer> votosBlanco = new HashMap<Sector, Integer>();
	private LinkedList<ResultadosCandidato> candidatos = new LinkedList<ResultadosCandidato>();
	
	public Resultados()
	{
		
	}
	
	public HashMap<Sector, Integer> getVotosEmitidos()
	{
		return votosEmitidos;
	}
	
	public void setVotosEmitidos(HashMap<Sector, Integer> votosEmitidos)
	{
		this.votosEmitidos = votosEmitidos;
	}
	
	public HashMap<Sector, Integer> getVotosBlanco()
	{
		return votosBlanco;
	}
	
	public void setVotosBlanco(HashMap<Sector, Integer> votosBlanco)
	{
		this.votosBlanco = votosBlanco;
	}
	
	public LinkedList<ResultadosCandidato> getCandidatos()
	{
		return candidatos;
	}
	
	public void setCandidatos(LinkedList<ResultadosCandidato> candidatos)
	{
		this.candidatos = candidatos;
	}
}
