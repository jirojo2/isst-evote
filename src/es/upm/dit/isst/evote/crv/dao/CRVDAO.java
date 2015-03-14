package es.upm.dit.isst.evote.crv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Escuela;
import es.upm.dit.isst.evote.model.MesaElectoral;
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
	
	public Escuela findEscuelaById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(Escuela.class, id);
	}
	
	public MesaElectoral findMesaElectoralById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(MesaElectoral.class, id);
	}
	
	public Candidato findCandidatoById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(Candidato.class, id);
	}

	@SuppressWarnings("rawtypes")
	public long ultimaVotacionId()
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Votacion v order by id desc");
		List results = q.getResultList();
		Votacion votacion = null;
		if (!results.isEmpty()) {
			votacion = (Votacion) results.get(0);
			return votacion.id().getId();
		}
		return 0L;
	}

	@SuppressWarnings("unchecked")
	public List<Voto> votosVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getResultList();
	}
	
	public List<Escuela> escuelasVotacion(Votacion votacion)
	{
		List<Voto> votos = votosVotacion(votacion);
		List<Escuela> escuelas = new ArrayList<Escuela>();
		for (Voto voto: votos)
			if (!escuelas.contains(voto.escuela()))
				escuelas.add(voto.escuela());
		return escuelas;
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
	
	public int votosCandidatoEscuelaVotacion(Candidato candidato, Escuela escuela, Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion and escuela = :escuela and candidato = :candidato");
		q.setParameter("votacion", votacion.id());
		q.setParameter("escuela", escuela.id());
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
	
	public synchronized void registrar(Escuela escuela)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(escuela);
		em.close();
	}
	
	public synchronized void registrar(MesaElectoral mesa)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(mesa);
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
