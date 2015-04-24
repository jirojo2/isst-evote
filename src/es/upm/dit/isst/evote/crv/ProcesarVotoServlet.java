package es.upm.dit.isst.evote.crv;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.crv.json.RespuestaVoto;
import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

public class ProcesarVotoServlet extends HttpServlet
{
	private static final long serialVersionUID = 1251763050668641745L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		RespuestaVoto respuesta = new RespuestaVoto();
		Gson gson = new Gson();
		
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
			respuesta.setRespuesta("rechazado por parámetros incorrectos");
			res.getWriter().println(gson.toJson(respuesta));
			res.getWriter().close();
			return;
		}
		
		String firma = req.getParameter("firma");
		respuesta.setNonce(nonce);
		
		Votacion votacion = CRVDAO.instance.findVotacionById(id_votacion);
		CEE cee = CRVDAO.instance.findCEEById(id_cee);
		Sector sector = CRVDAO.instance.findSectorById(id_sector);
		Candidato candidato = CRVDAO.instance.findCandidatoById(id_candidato);	
				
		Voto voto = new Voto(votacion, cee, sector, candidato, timestamp, nonce, firma);
		try
		{			
			if (CRV.instance.procesarVoto(voto))
			{
				// OK, confirmar con un mensaje de OK, el nonce y un timestamp
				respuesta.setRespuesta("ok");
			}
			else
			{
				// Voto rechazado por firma incorrecta
				respuesta.setRespuesta("rechazado por firma incorrecta");
			}
		}
		catch (InvalidKeyException | InvalidKeySpecException e)
		{
			// Clave del CEE inválida
			// rechazar
			respuesta.setRespuesta("rechazado por cee inválido o no registado");
		}
		catch (SignatureException e)
		{
			// Firma del voto inválida
			// rechazar
			respuesta.setRespuesta("rechazado por firma de voto inválida");
		}
		catch (NoSuchAlgorithmException e)
		{
			// Falta algoritmo.. esto es serio
			// rechazar
			e.printStackTrace();
			respuesta.setRespuesta("rechazado por error criptográfico, avisar al operador del CRV");
		}
		finally
		{
			res.getWriter().println(gson.toJson(respuesta));
			res.getWriter().flush();
			res.getWriter().close();
		}
	}
}
