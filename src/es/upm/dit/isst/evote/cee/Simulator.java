package es.upm.dit.isst.evote.cee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;

import es.upm.dit.isst.evote.crv.json.SyncVotacion;
import es.upm.dit.isst.evote.cee.model.CEE;
import es.upm.dit.isst.evote.cee.model.Candidato;
import es.upm.dit.isst.evote.cee.model.Sector;
import es.upm.dit.isst.evote.cee.model.Votacion;
import es.upm.dit.isst.evote.cee.model.Voto;

/**
 * Simulador del componente CEE
 * 
 * <p>
 *   Aunque sea un simulador del CEE, trabajamos con la base de datos
 *	 del CRV, que compartirá ciertas tablas con el CEE
 * </p>
 * <p>
 *   Referencia: http://web.archive.org/web/20120324071014/http://www3.upm.es/elecciones/resultados
 * </p>
 */
public class Simulator 
{
	/**
	 * Voto Simulado
	 * 
	 * <p>
	 * 	 Facilita la generación de un voto en el simulador
	 * </p>
	 */
	public class VotoSimulado
	{		
		private Voto voto;
		
		public VotoSimulado(Sector sector, Candidato candidato) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			long timestamp = new Date().getTime();
			long nonce = generarNonce();
			String firma = firma(privateKeyCEE, votacion, cee, sector, candidato, timestamp, nonce);
			voto = new Voto(votacion, cee, sector, candidato, timestamp, nonce, firma);
		}
		
		public Voto voto()
		{
			return this.voto;
		}
		
		private Long generarNonce() throws NoSuchAlgorithmException
		{
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			return sr.nextLong();
		}
		
		private String firma(PrivateKey privateKey, Votacion votacion, CEE cee, Sector sector, Candidato candidato, long timestamp, long nonce) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			ByteBuffer buffer = ByteBuffer.allocate(48); // total, 48 Byte
			buffer.putLong(votacion.id());       		 // votación en curso
			buffer.putLong(cee.id());            		 // cee emisor (1 si solo hay uno)
			buffer.putLong(sector.id());         		 // sector al que pertenece el votante (ponderación)
			buffer.putLong(candidato.id());      		 // candidato votado
			buffer.putLong(timestamp);                   // timestamp con precisión de ms
			buffer.putLong(nonce);                       // nonce aleatorio que usamos para confirmar y evitar replays, 64 bit
			
			Signature signature = Signature.getInstance("SHA512withRSA");
			signature.initSign(privateKey);
			signature.update(buffer.array());
			return Base64.encodeBase64String(signature.sign()); // la firma se codifica en base64
		}
	}
	
	private Votacion votacion;
	private CEE cee;
	
	PrivateKey privateKeyCEE;
	PublicKey publicKeyCEE;
	
	private List<Candidato> candidatos; 
	private List<Sector> sectores; 
	
	private Candidato blanco;
	
	private Random rand = new Random();
	
	private Map<Long, Integer> censo;
	
	private int totalVotosEmitidos = 0;
	private String baseURL;
	
	private long votacionExistente = 0;
	
	public void setBaseURL(String value)
	{
		baseURL = value;
	}
	
	public void setVotacionExistente(long value)
	{
		votacionExistente = value;
	}
	
	/**
	 * Devuelve el total de votos emitidos registrados por el simulador
	 * @return total de votos emitidos por el simulador
	 */
	public int getTotalVotosEmitidos()
	{
		return totalVotosEmitidos;
	}
	
	/**
	 * Ejecuta el simulador
	 */
	public void run() 
	{
		generarCEE();
		syncData();
		generarVotos();
	}
	
	/**
	 * Genera la pareja de claves para un CEE de prueba
	 */
	private void generarCEE()
	{
		KeyPairGenerator keyPairGenerator = null;
		
		try
		{
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			
		} catch (NoSuchAlgorithmException e1)
		{
			e1.printStackTrace();
		}

		keyPairGenerator.initialize(1024); // Key Size -> 4096 bit en producción 
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		publicKeyCEE = keyPair.getPublic();
		privateKeyCEE = keyPair.getPrivate();
	}
	
	/**
	 * Inicializa una nueva votación de prueba en el CRV
	 * Devuelve un JSON con los IDs relevantes para el simulador
	 * @return
	 */
	private boolean syncData()
	{
		try 
	    {
			Gson gson = new Gson();
			String encodedData = "key=" + URLEncoder.encode(Base64.encodeBase64String(publicKeyCEE.getEncoded()), "UTF-8");
			if (votacionExistente != 0)
			{
				encodedData += "&votacion=" + votacionExistente;
			}
			
	        URL url = new URL(baseURL + "/sync");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setReadTimeout(30000);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));

	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	        writer.write(encodedData);
	        writer.close();

	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) 
	        {
	            // OK
	        	SyncVotacion sv = gson.fromJson(new InputStreamReader(connection.getInputStream()), SyncVotacion.class);
	        	
	        	cee = sv.getCee();
	        	votacion = sv.getVotacion();
	        	censo = sv.getCenso();
	        	sectores = sv.getSectores();
	        	blanco = sv.getBlanco();
	        	candidatos = sv.getCandidatos();
	        	
	        	System.out.format("Votación de prueba con id %s", votacion.id());
	        	System.out.println();
	        	
	        	return true;
	        } 
	        else 
	        {
	            // Server returned HTTP error code.
	        	return false;
	        }
	    } 
	    catch (MalformedURLException e) 
	    {
			e.printStackTrace();
			return false;
	    } 
	    catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return false;
		}
	    catch (IOException e) 
	    {
			e.printStackTrace();
			return false;
	    }
	}
	
	/**
	 * Genera los votos a partir del censo y de un porcentaje de participación aleatorio
	 */
	private void generarVotos()
	{
		// Por sectores: 4 grupos
		// Por candidato: 3 candidatos + voto en blanco
		// No hay voto nulo
				
		Iterator<Entry<Long, Integer>> it = censo.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Long, Integer> entry = it.next();

			// participación: 0.3 - 0.8
			double participacion = 0.3 + rand.nextDouble()/2;
			
			// TEMP: bajamos la participación para la demo:
			participacion = participacion / 20;
			
			int votosEsperados = (int) Math.round(entry.getValue() * participacion);
			System.out.format("%d votos esperados para el sector %s", votosEsperados, entry.getKey());
			System.out.println();
			
			for (int i = 0; i < votosEsperados; i++)
			{				
				generarVoto(entry.getKey());
			}
			
			totalVotosEmitidos += votosEsperados;
			System.out.format("%d votos emitidos!", votosEsperados);
			System.out.println();
		}
	}
	
	/**
	 * Genera un voto para un miembro de un sector determinado
	 * Se basa en probabilidades para elegir a un candidato
	 * @param sectorId
	 */
	private void generarVoto(Long sectorId)
	{
		Sector sector = null;
		for (Sector s : sectores)
		{
			if (s.id() == sectorId)
			{
				sector = s;
				break;
			}
		}

		Candidato candidato = null;
		int pBlanco = 10;
		int magic = rand.nextInt(100);
		
		for (int i = 0; i < candidatos.size(); i++)
		{
			if (magic > pBlanco && magic < pBlanco + (100-pBlanco)/candidatos.size()*(i+1))
			{
				candidato = candidatos.get(i);
				break;
			}
		}
		if (candidato == null) 
		{
			candidato = blanco;
		}
		
		VotoSimulado votoSimulado;
		try
		{
			votoSimulado = new VotoSimulado(sector, candidato);			
			enviarVoto(votoSimulado.voto());
		} 
		catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e)
		{
			// Voto fallido! Problemas cryptográficos
			e.printStackTrace();
		}	
	}
	
	/**
	 * Realiza una petición HTTP POST al CRV con el voto emitido
	 * @param voto emitido por el CEE y firmado
	 * @return si la operación tiene éxito
	 */
	public boolean enviarVoto(Voto voto)
	{
	    try 
	    {
	    	String encodedData = "";
			encodedData = "id_votacion=" + URLEncoder.encode(String.valueOf(voto.votacion().id()), "UTF-8");
			encodedData += "&id_cee=" + URLEncoder.encode(String.valueOf(voto.cee().id()), "UTF-8");
			encodedData += "&id_sector=" + URLEncoder.encode(String.valueOf(voto.sector().id()), "UTF-8");
			encodedData += "&id_candidato=" + URLEncoder.encode(String.valueOf(voto.candidato().id()), "UTF-8");
			encodedData += "&timestamp=" + URLEncoder.encode(String.valueOf(voto.timestampEmitido()), "UTF-8");
			encodedData += "&nonce=" + URLEncoder.encode(String.valueOf(voto.nonce()), "UTF-8");
			encodedData += "&firma=" + URLEncoder.encode(voto.firma(), "UTF-8");
			
	        URL url = new URL(baseURL + "/voto");
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setReadTimeout(30000);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        connection.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));

	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	        writer.write(encodedData);
	        writer.close();

	        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) 
	        {
	            // OK
	        	return true;
	        } 
	        else 
	        {
	            // Server returned HTTP error code.
	        	return false;
	        }
	    } 
	    catch (MalformedURLException e) 
	    {
			e.printStackTrace();
			return false;
	    } 
	    catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return false;
		}
	    catch (IOException e) 
	    {
			e.printStackTrace();
			return false;
	    }
	}
	/**
	 * Ejecuta el simulador, borrando previamente los datos que pueda haber en la base de datos.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Simulator sim = new Simulator();
		sim.run();
	}

}
