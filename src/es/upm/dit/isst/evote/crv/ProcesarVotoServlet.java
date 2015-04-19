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
import es.upm.dit.isst.evote.model.Escuela;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

public class ProcesarVotoServlet extends HttpServlet
{
	private static final long serialVersionUID = 1251763050668641745L;
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	{
		long id_votacion = Long.parseLong(req.getParameter("id_votacion"));
		long id_cee = Long.parseLong(req.getParameter("id_cee"));
		long id_escuela = Long.parseLong(req.getParameter("id_escuela"));
		long id_sector = Long.parseLong(req.getParameter("id_sector"));
		long id_candidato = Long.parseLong(req.getParameter("id_candidato"));
		long timestamp = Long.parseLong(req.getParameter("timestamp"));
		long nonce = Long.parseLong(req.getParameter("nonce"));
		String firma = req.getParameter("firma");
		
		Votacion votacion = CRVDAO.instance.findVotacionById(id_votacion);
		CEE cee = CRVDAO.instance.findCEEById(id_cee);
		Escuela escuela = CRVDAO.instance.findEscuelaById(id_escuela);
		Sector sector = CRVDAO.instance.findSectorById(id_sector);
		Candidato candidato = CRVDAO.instance.findCandidatoById(id_candidato);	
		
		PrintWriter out = null;
		
		Voto voto = new Voto(votacion, cee, escuela, sector, candidato, timestamp, nonce, firma);
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
