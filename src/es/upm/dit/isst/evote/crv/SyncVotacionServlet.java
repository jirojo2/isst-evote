package es.upm.dit.isst.evote.crv;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.crv.dao.EMFService;
import es.upm.dit.isst.evote.crv.json.SyncVotacion;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;

public class SyncVotacionServlet extends HttpServlet
{
	private static final long serialVersionUID = -4571524480688214801L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
	    // param: CEE publicKey
		String publicKey = req.getParameter("key");	    
		
		// Init datos
		EntityManager em = EMFService.get().createEntityManager();
		
		// Borrar datos anteriores
		em.createQuery("DELETE FROM Candidato c").executeUpdate();
		em.createQuery("DELETE FROM CEE c").executeUpdate();
		em.createQuery("DELETE FROM Sector s").executeUpdate();
		em.createQuery("DELETE FROM Votacion v").executeUpdate();
		em.createQuery("DELETE FROM Voto v").executeUpdate();
		em.close();
		
		// CEE
		em = EMFService.get().createEntityManager();
		em.getTransaction().begin();
		CEE cee = new CEE("Test Center", publicKey);
		em.persist(cee);
		
		// Votacion
		Votacion votacion = new Votacion("Votación de prueba");		
		em.persist(votacion);
		em.getTransaction().commit();
		
		// Censo
		HashMap<Long, Integer> censo = new HashMap<Long, Integer>();
		LinkedList<Sector> sectores = new LinkedList<Sector>();
		
		HashMap<String, Integer> censoN = new HashMap<String, Integer>();
		censoN.put("grupoA", 1820);
		censoN.put("grupoB", 2297);
		censoN.put("grupoC", 39450);
		censoN.put("grupoD", 2569);

		HashMap<String, Float> censoPonderaciones = new HashMap<String, Float>();
		censoPonderaciones.put("grupoA", 0.51f);
		censoPonderaciones.put("grupoB", 0.16f);
		censoPonderaciones.put("grupoC", 0.24f);
		censoPonderaciones.put("grupoD", 0.09f);
		
		HashMap<String, String> censoNombres = new HashMap<String, String>();
		censoNombres.put("grupoA", "Profesores doctores con vinculación permanente");
		censoNombres.put("grupoB", "Resto del profesorado y personal investigador");
		censoNombres.put("grupoC", "Estudiantes");
		censoNombres.put("grupoD", "Personal de administración y servicios");

		for(String n : new String[]{"grupoA", "grupoB", "grupoC", "grupoD"})
		{
			Sector sector = new Sector(votacion, censoNombres.get(n), censoPonderaciones.get(n));
			sectores.add(sector);
			em.getTransaction().begin();
			em.persist(sector);
			em.getTransaction().commit();
			censo.put(sector.id().getId(), censoN.get(n));
		}
		
		// Candidatos
		em.getTransaction().begin();
		
		String[] nombresCandidatos = { "Guillermo Cisneros", "Carlos Conde" };
		LinkedList<Candidato> candidatos = new LinkedList<Candidato>(); 
		
		for (String nombreCandidato : nombresCandidatos)
		{
			String[] n = nombreCandidato.split(" ");
			Candidato candidato = new Candidato(votacion, "test", n[0], n[1]);
			candidatos.add(candidato);
			em.persist(candidato);
		}
		
		// Candidato especial: blanco
		Candidato blanco = new Candidato(votacion, "blanco", "", "blanco");
		em.persist(blanco);
		
		em.getTransaction().commit();
		em.close();
		
	    // Enviar datos para poder votar con el simulador
	    
	    Gson gson = new Gson();
	    SyncVotacion sv = new SyncVotacion();
	    
	    sv.setVotacion(new es.upm.dit.isst.evote.cee.model.Votacion(votacion.id().getId(), votacion.nombre()));
	    sv.setCee(new es.upm.dit.isst.evote.cee.model.CEE(cee.id().getId(), cee.nombre(), cee.clavePublica()));
	    sv.setCenso(censo);
	    
	    for (Sector sector : sectores)
	    {
	    	sv.getSectores().add(new es.upm.dit.isst.evote.cee.model.Sector(sector.id().getId(), sector.nombre(), sector.ponderacion()));
	    }
	    
	    for (Candidato candidato : candidatos)
	    {
	    	sv.getCandidatos().add(new es.upm.dit.isst.evote.cee.model.Candidato(candidato.id().getId(), candidato.nif(), candidato.nombre(), candidato.apellidos()));
	    }
	    
	    sv.setBlanco(new es.upm.dit.isst.evote.cee.model.Candidato(blanco.id().getId(), blanco.nif(), blanco.nombre(), blanco.apellidos()));
	    
	    // Respuesta, un simple OK vale
	    System.out.println("Nueva votación inicializada con id " + votacion.id().getId());
	    res.getWriter().println(gson.toJson(sv));
	}
}
