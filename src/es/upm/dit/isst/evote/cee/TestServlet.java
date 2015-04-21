package es.upm.dit.isst.evote.cee;

import java.io.IOException;
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
		
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("test", "alpha");
		datos.put("resultado", "ok");
		
		Simulator sim = new Simulator();
		
		sim.borrarDatos();
		sim.run();
		
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
