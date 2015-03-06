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
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;
import es.upm.dit.isst.evote.crv.dao.CRVDAO;

public class TestDataServlet extends HttpServlet
{
	private static final long serialVersionUID = 1118083128547257020L;
	
	private String firma(PrivateKey privateKey, Votacion votacion, CEE cee, Candidato candidato, long timestamp) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.putLong(votacion.id().getId());
		buffer.putLong(cee.id().getId());
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
				
		Candidato candidato1 = new Candidato("05466628B", "José Ignacio", "Rojo Rivero");
		Candidato candidato2 = new Candidato("05466628B", "José Ignacio 2", "Rojo Rivero");
		
		Votacion votacion = new Votacion("Votación de prueba");
		CEE cee = new CEE("Test Center", Base64.encodeBase64String(publicKey.getEncoded()));
		
		CRVDAO.instance.registrar(candidato1);
		CRVDAO.instance.registrar(candidato2);
		CRVDAO.instance.registrar(votacion);
		CRVDAO.instance.registrar(cee);
		
		long timestamp = new Date().getTime();
		
		try
		{
			System.out.println("Signature: " + firma(privateKey, votacion, cee, candidato1, timestamp));
			
			Voto voto1 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			Voto voto2 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			Voto voto3 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			Voto voto4 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			Voto voto5 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			Voto voto6 = new Voto(votacion, cee, candidato1, timestamp, firma(privateKey, votacion, cee, candidato1, timestamp));
			
			CRV.instance.procesarVoto(voto1);
			CRV.instance.procesarVoto(voto2);
			CRV.instance.procesarVoto(voto3);
			CRV.instance.procesarVoto(voto4);
			CRV.instance.procesarVoto(voto5);
			CRV.instance.procesarVoto(voto6);
			
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
