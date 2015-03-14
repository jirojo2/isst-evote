package es.upm.dit.isst.evote.crv;

import java.util.ArrayList;
import java.util.List;

import es.upm.dit.isst.evote.model.Escuela;

public class ResultadosVotacionPorEscuelas
{
	public class ResultadosVotacionPorEscuela
	{
		private Escuela escuela;
		private ResultadosVotacion recuento;
		
		public ResultadosVotacionPorEscuela(Escuela escuela, ResultadosVotacion recuento)
		{
			this.escuela = escuela;
			this.recuento = recuento;
		}
		
		public Escuela escuela()
		{
			return escuela;
		}
		
		public ResultadosVotacion recuento()
		{
			return recuento;
		}
	}
	
	private ResultadosVotacion global;
	private List<ResultadosVotacionPorEscuela> escuelas = new ArrayList<ResultadosVotacionPorEscuela>();
	
	public ResultadosVotacionPorEscuelas(ResultadosVotacion global, List<ResultadosVotacionPorEscuela> escuelas)
	{
		this.global = global;
		this.escuelas = escuelas;
	}

	public ResultadosVotacionPorEscuelas(ResultadosVotacion global)
	{
		this.global = global;
	}

	public ResultadosVotacion global()
	{
		return global;
	}

	public List<ResultadosVotacionPorEscuela> escuelas()
	{
		return escuelas;
	}
	
	public void escuela(Escuela escuela, ResultadosVotacion recuento)
	{
		escuelas.add(new ResultadosVotacionPorEscuela(escuela, recuento));
	}
}
