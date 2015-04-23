package es.upm.dit.isst.evote.crv.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

/**
 * DAO para CRV
 */
public class CRVDAO
{
	public static CRVDAO instance = new CRVDAO();
	
	private CRVDAO()
	{	
	}
	
	public Votacion findVotacionById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Votacion r = em.find(Votacion.class, id);
		em.close();
		return r;
	}
	
	public CEE findCEEById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		CEE r = em.find(CEE.class, id);
		em.close();
		return r;
	}
	
	public Sector findSectorById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Sector r = em.find(Sector.class, id);
		em.close();
		return r;
	}
	
	public Candidato findCandidatoById(long id)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Candidato r = em.find(Candidato.class, id);
		em.close();
		return r;
	}

	@SuppressWarnings("rawtypes")
	public long ultimaVotacionId()
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Votacion v order by id desc");
		List results = q.getResultList();
		em.close();
		Votacion votacion = null;
		if (!results.isEmpty()) {
			votacion = (Votacion) results.get(0);
			return votacion.id().getId();
		}
		return 0L;
	}

	@SuppressWarnings("unchecked")
	public List<Candidato> candidatosVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select c from Candidato c where votacion = :votacion and nif <> 'blanco'");
		q.setParameter("votacion", votacion.id());
		List<Candidato> r = q.getResultList();
		em.close();
		return r;
	}

	@SuppressWarnings("unchecked")
	public List<Sector> sectoresVotacion(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select s from Sector s where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		List<Sector> r = q.getResultList();
		em.close();
		return r;
	}
	
	/**
	 * Cuenta todos los votos asociados a una votación.
	 * @param votacion en curso
	 * @return total de votos
	 */
	public int votos(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select v from Voto v where votacion = :votacion");
		q.setParameter("votacion", votacion.id());
		int r = q.getResultList().size();
		em.close();
		return r;
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
		Query q = em.createQuery("select v from Voto v where votacion = :votacion and sector = :sector");
		q.setParameter("votacion", votacion.id());
		q.setParameter("sector", sector.id());
		int r = q.getResultList().size();
		em.close();
		return r;
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
		Query q = em.createQuery("select v from Voto v where votacion = :votacion and candidato = :candidato and sector = :sector");
		q.setParameter("votacion", votacion.id());
		q.setParameter("candidato", candidato.id());
		q.setParameter("sector", sector.id());
		int r = q.getResultList().size();
		em.close();
		return r;
	}
	
	/**
	 * Encuentra el candidato especial para los votos en blanco, identificado por su nif
	 * @return candidato especial para los votos en blanco
	 */
	public Candidato blanco(Votacion votacion)
	{
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select c from Candidato c where votacion = :votacion and nif = 'blanco'");
		q.setParameter("votacion", votacion.id());
		Candidato c = (Candidato) q.getSingleResult();
		em.close();
		return c;
	}
	
	/**
	 * Cuenta los votos emitidos en blanco para un sector determinado
	 * @param votacion en curso
	 * @param sector determinado
	 * @return
	 */
	public int votosBlancoSector(Votacion votacion, Sector sector) 
	{	
		return votosCandidatoSector(votacion, blanco(votacion), sector);
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
