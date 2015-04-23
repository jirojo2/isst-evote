package es.upm.dit.isst.evote.cee;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class TestServlet extends HttpServlet 
{

	private static final long serialVersionUID = -707391732241252457L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		Gson gson = new Gson();

		res.setContentType("application/json; charset=UTF-8");
		
		long tic = new Date().getTime();
		
		Simulator sim = new Simulator();
		
		sim.setPort(req.getServerPort());
		sim.setHostname(req.getServerName());
		sim.borrarDatos();
		sim.run();
		
		long toc = new Date().getTime();
		int ellapsedMilliseconds = (int)(toc - tic);
		
		HashMap<String, Integer> datos = new HashMap<String, Integer>();
		datos.put("votosEmitidos", sim.getTotalVotosEmitidos());
		datos.put("ellapsedMilliseconds", ellapsedMilliseconds);
		
		try
		{
			res.getWriter().println(gson.toJson(datos));
			res.getWriter().close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
