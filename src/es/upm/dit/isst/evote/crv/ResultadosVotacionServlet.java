package es.upm.dit.isst.evote.crv;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.crv.dao.EMFService;
import es.upm.dit.isst.evote.crv.json.Resultados;
import es.upm.dit.isst.evote.model.Votacion;

public class ResultadosVotacionServlet extends HttpServlet
{
	private static final long serialVersionUID = -3909947981713813483L;
	private static boolean enableCache = true;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		long id_votacion = 0L;
		
		if (req.getParameter("id") == null)
		{
			id_votacion = CRVDAO.instance.ultimaVotacionId();
		} 
		else
		{
			id_votacion = Long.parseLong(req.getParameter("id"));
		}
		
		Votacion votacion = CRVDAO.instance.findVotacionById(id_votacion);
		
		Gson gson = new Gson();
		
		res.setContentType("application/json; charset=UTF-8");
		
		try
		{
			if (enableCache && votacion.tieneCacheResultado())
			{
				res.getWriter().println(votacion.cacheResultadoJSON());	
				System.out.println("Mostrando resultados desde cache de votación");
			}
			else 
			{
				Resultados resultados = CRV.instance.resultados(votacion);
				String resultadosJSON = gson.toJson(resultados);
				
				EntityManager em = EMFService.get().createEntityManager();
				Votacion v = em.find(Votacion.class, votacion.id());
				em.getTransaction().begin();
				v.cacheResultadoJSON(resultadosJSON);
				em.getTransaction().commit();
				em.close();
				
				res.getWriter().println(resultadosJSON);
				System.out.println("Guardando resultados en cache de votación");
			}
			res.getWriter().close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
