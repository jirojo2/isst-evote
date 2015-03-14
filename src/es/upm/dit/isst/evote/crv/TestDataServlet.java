package es.upm.dit.isst.evote.crv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Escuela;
import es.upm.dit.isst.evote.model.MesaElectoral;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;
import es.upm.dit.isst.evote.crv.dao.CRVDAO;

public class TestDataServlet extends HttpServlet
{
	private static final long serialVersionUID = 1118083128547257020L;
	
	private String firma(PrivateKey privateKey, Votacion votacion, CEE cee, Escuela escuela, MesaElectoral mesa, Candidato candidato, long timestamp) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		ByteBuffer buffer = ByteBuffer.allocate(48);
		buffer.putLong(votacion.id().getId());
		buffer.putLong(cee.id().getId());
		buffer.putLong(escuela.id().getId());
		buffer.putLong(mesa.id().getId());
		buffer.putLong(candidato.id().getId());
		buffer.putLong(timestamp);
		
		Signature signature = Signature.getInstance("SHA512withRSA");
		signature.initSign(privateKey);
		signature.update(buffer);
		return Base64.encodeBase64String(signature.sign());
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	{
		KeyPairGenerator keyPairGenerator = null;
		
		try
		{
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			
		} catch (NoSuchAlgorithmException e1)
		{
			e1.printStackTrace();
		}

		keyPairGenerator.initialize(1024);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		
		System.out.println("CEE publicKey: " + Base64.encodeBase64String(publicKey.getEncoded()));
				
		Candidato candidato1 = new Candidato("00000000X", "José Ignacio", "Rojo Rivero");
		Candidato candidato2 = new Candidato("00000000Y", "Roberto", "Paterna Ferrón");
		Candidato candidato3 = new Candidato("00000000Z", "Jorge", "Díez de la Fuente");
		
		Votacion votacion = new Votacion("Votación de prueba");
		CEE cee = new CEE("Test Center", Base64.encodeBase64String(publicKey.getEncoded()));
		
		Escuela escuela = new Escuela("ETSIT");
		MesaElectoral mesa = new MesaElectoral("00000", escuela);

		Escuela escuela2 = new Escuela("ETSII");
		MesaElectoral mesa2 = new MesaElectoral("00100", escuela2);
		
		CRVDAO.instance.registrar(candidato1);
		CRVDAO.instance.registrar(candidato2);
		CRVDAO.instance.registrar(candidato3);
		CRVDAO.instance.registrar(votacion);
		CRVDAO.instance.registrar(cee);
		CRVDAO.instance.registrar(escuela);
		CRVDAO.instance.registrar(mesa);
		CRVDAO.instance.registrar(escuela2);
		CRVDAO.instance.registrar(mesa2);
		
		long timestamp = new Date().getTime();
		
		try
		{
			System.out.println("Signature: " + firma(privateKey, votacion, cee, escuela, mesa, candidato1, timestamp));
			
			Voto voto1 = new Voto(votacion, cee, escuela, mesa, candidato1, timestamp, firma(privateKey, votacion, cee, escuela, mesa, candidato1, timestamp));
			Voto voto2 = new Voto(votacion, cee, escuela, mesa, candidato1, timestamp, firma(privateKey, votacion, cee, escuela, mesa, candidato1, timestamp));
			Voto voto3 = new Voto(votacion, cee, escuela, mesa, candidato2, timestamp, firma(privateKey, votacion, cee, escuela, mesa, candidato2, timestamp));
			Voto voto4 = new Voto(votacion, cee, escuela, mesa, candidato1, timestamp, firma(privateKey, votacion, cee, escuela, mesa, candidato1, timestamp));
			Voto voto5 = new Voto(votacion, cee, escuela2, mesa2, candidato1, timestamp, firma(privateKey, votacion, cee, escuela2, mesa2, candidato1, timestamp));
			Voto voto6 = new Voto(votacion, cee, escuela2, mesa2, candidato2, timestamp, firma(privateKey, votacion, cee, escuela2, mesa2, candidato2, timestamp));
			Voto voto7 = new Voto(votacion, cee, escuela2, mesa2, candidato2, timestamp, firma(privateKey, votacion, cee, escuela2, mesa2, candidato2, timestamp));
			Voto voto8 = new Voto(votacion, cee, escuela2, mesa2, candidato3, timestamp, firma(privateKey, votacion, cee, escuela2, mesa2, candidato3, timestamp));
			
			CRV.instance.procesarVoto(voto1);
			CRV.instance.procesarVoto(voto2);
			CRV.instance.procesarVoto(voto3);
			CRV.instance.procesarVoto(voto4);
			CRV.instance.procesarVoto(voto5);
			CRV.instance.procesarVoto(voto6);
			CRV.instance.procesarVoto(voto7);
			CRV.instance.procesarVoto(voto8);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
		
		try
		{
			res.getWriter().println("Ok!");
			res.getWriter().close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
