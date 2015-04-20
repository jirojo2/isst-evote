package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;

import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;

public class ResultadosCandidato 
{
	private Candidato candidato;
	private Double totalPonderado;
	private HashMap<Sector, Integer> votos;
	
	public ResultadosCandidato()
	{
		
	}

	public Candidato getCandidato()
	{
		return candidato;
	}

	public void setCandidato(Candidato candidato)
	{
		this.candidato = candidato;
	}

	public Double getTotalPonderado()
	{
		return totalPonderado;
	}

	public void setTotalPonderado(Double totalPonderado)
	{
		this.totalPonderado = totalPonderado;
	}

	public HashMap<Sector, Integer> getVotos()
	{
		return votos;
	}

	public void setVotos(HashMap<Sector, Integer> votos)
	{
		this.votos = votos;
	}
}