package es.upm.dit.isst.evote.crv;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

public class ProcesarVotoServlet extends HttpServlet
{
	private static final long serialVersionUID = 1251763050668641745L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	{
		long id_votacion = 0;
		long id_cee = 0;
		long id_sector = 0;
		long id_candidato = 0;
		long timestamp = 0;
		long nonce = 0;
		try 
		{
			id_votacion = Long.parseLong(req.getParameter("id_votacion"));
			id_cee = Long.parseLong(req.getParameter("id_cee"));
			id_sector = Long.parseLong(req.getParameter("id_sector"));
			id_candidato = Long.parseLong(req.getParameter("id_candidato"));
			timestamp = Long.parseLong(req.getParameter("timestamp"));
			nonce = Long.parseLong(req.getParameter("nonce"));
		}
		catch (NumberFormatException e)
		{
			try
			{
				PrintWriter out = res.getWriter();
				out.write("Parámetros incorrectos");
				out.close();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return;
		}
		
		String firma = req.getParameter("firma");
		
		Votacion votacion = CRVDAO.instance.findVotacionById(id_votacion);
		CEE cee = CRVDAO.instance.findCEEById(id_cee);
		Sector sector = CRVDAO.instance.findSectorById(id_sector);
		Candidato candidato = CRVDAO.instance.findCandidatoById(id_candidato);	
		
		PrintWriter out = null;
		
		Voto voto = new Voto(votacion, cee, sector, candidato, timestamp, nonce, firma);
		try
		{
			out = res.getWriter();
			
			if (CRV.instance.procesarVoto(voto))
			{
				// OK, confirmar con un mensaje de OK, el nonce y un timestamp
				// TODO confirmar
				out.println("ok");
			}
			else
			{
				// Voto rechazado por cualquier razón
				// TODO rechazar
				out.println("rechazado");
			}
		}
		catch (InvalidKeyException | InvalidKeySpecException e)
		{
			// Clave del CEE inválida
			// TODO rechazar
			out.println("rechazado por cee inválido o no registado");
		}
		catch (SignatureException e)
		{
			// Firma del voto inválida
			// TODO rechazar
			out.println("rechazado por firma de voto inválida");
		}
		catch (NoSuchAlgorithmException | IOException e)
		{
			// Error -.-, rechazar
			e.printStackTrace();
			// TODO rechazar
			out.println("rechazado por error desconocido");
		}
		finally
		{
			out.flush();
			out.close();
		}
	}
}
