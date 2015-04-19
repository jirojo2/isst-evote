package es.upm.dit.isst.evote.crv;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;
import es.upm.dit.isst.evote.crv.dao.CRVDAO;

public class TestDataServlet extends HttpServlet
{
	private static final long serialVersionUID = 1118083128547257020L;
	
	public class VotoSimulado
	{		
		private Voto voto;
		
		public VotoSimulado(PrivateKey privateKey, Votacion votacion, CEE cee, Escuela escuela, Sector sector, Candidato candidato) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			long timestamp = new Date().getTime();
			long nonce = generarNonce();
			String firma = firma(privateKey, votacion, cee, escuela, sector, candidato, timestamp, nonce);
			voto = new Voto(votacion, cee, escuela, sector, candidato, timestamp, nonce, firma);
		}
		
		public Voto voto()
		{
			return this.voto;
		}
	}
	
	private String firma(PrivateKey privateKey, Votacion votacion, CEE cee, Escuela escuela, Sector sector, Candidato candidato, long timestamp, long nonce) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		ByteBuffer buffer = ByteBuffer.allocate(56); // total, 56 Byte
		buffer.putLong(votacion.id().getId());  // votación en curso
		buffer.putLong(cee.id().getId());       // cee emisor (1 si solo hay uno)
		buffer.putLong(escuela.id().getId());   // no se si vamos a mantener esto
		buffer.putLong(sector.id().getId());    // sector al que pertenece el votante (ponderación)
		buffer.putLong(candidato.id().getId()); // candidato votado
		buffer.putLong(timestamp);              // timestamp con precisión de ms
		buffer.putLong(nonce);                  // nonce aleatorio que usamos para confirmar y evitar replays, 64 bit
		
		Signature signature = Signature.getInstance("SHA512withRSA");
		signature.initSign(privateKey);
		signature.update(buffer);
		return Base64.encodeBase64String(signature.sign()); // la firma se codifica en base64
	}
	
	private Long generarNonce() throws NoSuchAlgorithmException
	{
		SecureRandom sr = SecureRandom.getInstance("NativePRNG");
		return sr.nextLong();
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
		
		Sector sector1 = new Sector("PDI con vinculación permanente", 0.51f);
		Sector sector2 = new Sector("PDI sin vinculación permanente", 0.16f);
		Sector sector3 = new Sector("PAS", 0.09f);
		Sector sector4 = new Sector("Alumnos", 0.24f);

		Escuela escuela2 = new Escuela("ETSII");
		
		CRVDAO.instance.registrar(candidato1);
		CRVDAO.instance.registrar(candidato2);
		CRVDAO.instance.registrar(candidato3);
		CRVDAO.instance.registrar(votacion);
		CRVDAO.instance.registrar(cee);
		CRVDAO.instance.registrar(escuela);
		CRVDAO.instance.registrar(escuela2);
		CRVDAO.instance.registrar(sector1);
		CRVDAO.instance.registrar(sector2);
		CRVDAO.instance.registrar(sector3);
		CRVDAO.instance.registrar(sector4);
		
		try
		{			
			Voto voto1 = new VotoSimulado(privateKey, votacion, cee, escuela, sector1, candidato1).voto();
			Voto voto2 = new VotoSimulado(privateKey, votacion, cee, escuela, sector1, candidato1).voto();
			Voto voto3 = new VotoSimulado(privateKey, votacion, cee, escuela, sector1, candidato2).voto();
			Voto voto4 = new VotoSimulado(privateKey, votacion, cee, escuela, sector1, candidato1).voto();
			Voto voto5 = new VotoSimulado(privateKey, votacion, cee, escuela2, sector1, candidato1).voto();
			Voto voto6 = new VotoSimulado(privateKey, votacion, cee, escuela2, sector1, candidato2).voto();
			Voto voto7 = new VotoSimulado(privateKey, votacion, cee, escuela2, sector1, candidato2).voto();
			Voto voto8 = new VotoSimulado(privateKey, votacion, cee, escuela2, sector1, candidato3).voto();
			
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
