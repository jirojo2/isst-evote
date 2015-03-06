package es.upm.dit.isst.evote.crv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

public class CRVDAO
{
	public static CRVDAO instance = new CRVDAO();
	
	private CRVDAO()
	{	
	}
	
	public Votacion findVotacionById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(Votacion.class, id);
	}
	
	public CEE findCEEById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(CEE.class, id);
	}
	
	public Candidato findCandidatoById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(Candidato.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Voto> votosVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getResultList();
	}
	
	public List<Candidato> candidatosVotacion(Votacion votacion)
	{
		List<Voto> votos = votosVotacion(votacion);
		List<Candidato> candidatos = new ArrayList<Candidato>();
		for (Voto voto: votos)
			if (!candidatos.contains(voto.candidato()))
				candidatos.add(voto.candidato());
		return candidatos;
	}
	
	public int votosCandidatoVotacion(Candidato candidato, Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion and candidato = :candidato");
		q.setParameter("votacion", votacion.id());
		q.setParameter("candidato", candidato.id());
		return q.getResultList().size();
	}
	
	public synchronized void registrar(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(votacion);
		em.close();
	}
	
	public synchronized void registrar(CEE cee)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(cee);
		em.close();
	}
	
	public synchronized void registrar(Candidato candidato)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(candidato);
		em.close();
	}
	
	public synchronized void registrar(Voto voto)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(voto);
		em.close();
	}
}
