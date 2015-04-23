package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;

public class ResultadosCandidato 
{
	private String candidato;
	private Double totalPonderado;
	private HashMap<String, Integer> votos = new HashMap<String, Integer>();
	
	public ResultadosCandidato()
	{
		
	}

	public String getCandidato()
	{
		return candidato;
	}

	public void setCandidato(String candidato)
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

	public HashMap<String, Integer> getVotos()
	{
		return votos;
	}

	public void setVotos(HashMap<String, Integer> votos)
	{
		this.votos = votos;
	}
}