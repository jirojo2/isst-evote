package es.upm.dit.isst.evote.crv;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.crv.json.Resultados;
import es.upm.dit.isst.evote.model.Votacion;

public class ResultadosVotacionServlet extends HttpServlet
{
	private static final long serialVersionUID = -3909947981713813483L;

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
		Resultados resultados = CRV.instance.resultados(votacion);
		
		Gson gson = new Gson();
		
		res.setContentType("application/json; charset=UTF-8");
		
		try
		{
			res.getWriter().println(gson.toJson(resultados));
			res.getWriter().close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
