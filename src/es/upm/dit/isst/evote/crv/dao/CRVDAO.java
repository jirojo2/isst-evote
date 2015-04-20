package es.upm.dit.isst.evote.crv.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Escuela;
import es.upm.dit.isst.evote.model.Sector;
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
	
	public Sector findSectorById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		return em.find(Sector.class, id);
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

	/**
	 * @deprecated no usar
	 * @param votacion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Voto> votosVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getResultList();
	}
	
	/**
	 * @deprecated no usar
	 * @param votacion
	 * @return
	 */
	public List<Escuela> escuelasVotacion(Votacion votacion)
	{
		List<Voto> votos = votosVotacion(votacion);
		List<Escuela> escuelas = new ArrayList<Escuela>();
		for (Voto voto: votos)
			if (!escuelas.contains(voto.escuela()))
				escuelas.add(voto.escuela());
		return escuelas;
	}
	
	/**
	 * @deprecated no usar
	 * @param votacion
	 * @return
	 */
	public List<Candidato> candidatosVotacionOld(Votacion votacion)
	{
		List<Voto> votos = votosVotacion(votacion);
		List<Candidato> candidatos = new ArrayList<Candidato>();
		for (Voto voto: votos)
			if (!candidatos.contains(voto.candidato()))
				candidatos.add(voto.candidato());
		return candidatos;
	}

	@SuppressWarnings("unchecked")
	public List<Candidato> candidatosVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select distinct(v.candidato) from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Sector> sectoresVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select s from Sector s where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getResultList();
	}
	
	/**
	 * @deprecated no usar
	 * @param candidato
	 * @param votacion
	 * @return
	 */
	public int votosCandidatoVotacion(Candidato candidato, Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion and candidato = :candidato");
		q.setParameter("votacion", votacion.id());
		q.setParameter("candidato", candidato.id());
		
		// TODO: Votos por sectores, pero no mezclar los números => NO DAR DECIMALES 
		
		return q.getResultList().size();
	}
	
	/**
	 * Cuenta todos los votos asociados a una votación.
	 * @param votacion en curso
	 * @return total de votos
	 */
	public int votos(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select COUNT(v) as c from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		return q.getFirstResult();
	}
	
	/**
	 * Cuenta los votos emitidos por un determinado sector.
	 * @param votacion en curso
	 * @param sector específico
	 * @return votos desde un sector determinado
	 */
	public int votosSector(Votacion votacion, Sector sector)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select COUNT(v) as c from Voto v where votacion = :votacion and sector = :sector");
		q.setParameter("votacion", votacion.id());
		q.setParameter("sector", sector.id());
		return q.getFirstResult();
	}
	
	/**
	 * Cuenta los votos de un determinado sector a favor del candidato especificado.
	 * @param votacion en curso
	 * @param candidato a recontar
	 * @param sector específico
	 * @return votos para un candidato desde un sector determinado
	 */
	public int votosCandidatoSector(Votacion votacion, Candidato candidato, Sector sector)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select COUNT(v) as c from Voto v where votacion = :votacion and candidato = :candidato and sector = :sector");
		q.setParameter("votacion", votacion.id());
		q.setParameter("candidato", candidato.id());
		q.setParameter("sector", sector.id());
		return q.getFirstResult();
	}
	
	/**
	 * Encuentra el candidato especial para los votos en blanco, identificado por su nif
	 * @return candidato especial para los votos en blanco
	 */
	public Candidato blanco()
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select c from Candidato c where nif = 'blanco'");
		return (Candidato) q.getSingleResult();
	}
	
	/**
	 * Cuenta los votos emitidos en blanco para un sector determinado
	 * @param votacion en curso
	 * @param sector determinado
	 * @return
	 */
	public int votosBlancoSector(Votacion votacion, Sector sector) 
	{	
		return votosCandidatoSector(votacion, blanco(), sector);
	}
	
	/**
	 * @deprecated no desglosar por escuelas
	 * @param candidato
	 * @param escuela
	 * @param votacion
	 * @return
	 */
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
	
	public synchronized void registrar(Sector sector)
	{
		EntityManager em = EMFService.get().createEntityManager();
		em.persist(sector);
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
