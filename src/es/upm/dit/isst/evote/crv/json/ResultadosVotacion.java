package es.upm.dit.isst.evote.crv.json;

import java.util.ArrayList;
import java.util.List;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.model.Candidato;

/**
 * Obtiene los datos de una votación, preparados para exportarse como JSON.
 * <hr>
 * Se deben mostrar: 
 * <ul>
 *  <li>El total de votos contabilizados</li>
 *  <li>El porcentaje ponderado</li>
 * </ul>
 */
public class ResultadosVotacion
{
	public class CandidatoVoto
	{
		private Candidato candidato;
		private int votos;
		private double total;
		
		public CandidatoVoto(Candidato candidato, int votos)
		{
			this.candidato = candidato;
			this.votos = votos;
		}
		
		public Candidato candidato()
		{
			return candidato;
		}
		
		public int votos()
		{
			return votos;
		}
		
		public double total()
		{
			return total;
		}
	}
	
	private int totalVotos;
	private Candidato ganador;
	private List<CandidatoVoto> recuento = new ArrayList<CandidatoVoto>();
	
	public int votosDelCandidato(Candidato candidato)
	{
		if (candidato == null)
		{
			return 0;
		}
		
		for (CandidatoVoto cv : recuento)
		{
			if (cv.candidato.equals(candidato))
			{
				return cv.votos;
			}
		}
		return 0;
	}
	
	private void actualizarGanador()
	{
		totalVotos = 0;
		int maxVotos = 0;
		
		for (CandidatoVoto cv: recuento)
		{
			totalVotos += cv.votos;
			if (cv.votos > maxVotos)
			{
				maxVotos = cv.votos;
				ganador = cv.candidato;
			}
		}
	}
	
	public int votosDelCandidato(long id_candidato)
	{
		return votosDelCandidato(CRVDAO.instance.findCandidatoById(id_candidato));
	}
	
	public void votosDelCandidato(Candidato candidato, int num_votos)
	{		
		for (CandidatoVoto cv: recuento)
		{
			if (cv.candidato.equals(candidato))
			{
				cv.votos = num_votos;
				return;
			}
		}
		
		recuento.add(new CandidatoVoto(candidato, num_votos));
		actualizarGanador();
	}
	
	public int totalVotos()
	{
		return totalVotos;
	}
	
	public Candidato ganador()
	{
		return ganador;
	}
	
	public float porcentajeVotos(int votos)
	{
		return votos / (float) totalVotos;
	}
	
	public float porcentajeVotos(Candidato candidato)
	{
		return porcentajeVotos(votosDelCandidato(candidato));
	}
}
