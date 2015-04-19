package es.upm.dit.isst.evote.crv.json;

import java.util.HashMap;
import java.util.LinkedList;

import es.upm.dit.isst.evote.model.Candidato;

public class Resultados
{
	public class ResultadosCandidato 
	{
		private Candidato candidato;
		private Long totalPonderado;
		private HashMap<String, Integer> votos;
		
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

		public Long getTotalPonderado()
		{
			return totalPonderado;
		}

		public void setTotalPonderado(Long totalPonderado)
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
