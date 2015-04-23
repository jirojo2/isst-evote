package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;
import java.util.LinkedList;

public class Resultados
{	
	private HashMap<String, Integer> votosEmitidos = new HashMap<String, Integer>();
	private HashMap<String, Integer> votosBlanco = new HashMap<String, Integer>();
	private LinkedList<ResultadosCandidato> candidatos = new LinkedList<ResultadosCandidato>();
	
	public Resultados()
	{
		
	}
	
	public HashMap<String, Integer> getVotosEmitidos()
	{
		return votosEmitidos;
	}
	
	public void setVotosEmitidos(HashMap<String, Integer> votosEmitidos)
	{
		this.votosEmitidos = votosEmitidos;
	}
	
	public HashMap<String, Integer> getVotosBlanco()
	{
		return votosBlanco;
	}
	
	public void setVotosBlanco(HashMap<String, Integer> votosBlanco)
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
