package es.upm.dit.isst.evote.cee;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.codec.binary.Base64;

import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

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
			SecureRandom sr = SecureRandom.getInstance("NativePRNG");
			return sr.nextLong();
		}
		
		private String firma(PrivateKey privateKey, Votacion votacion, CEE cee, Sector sector, Candidato candidato, long timestamp, long nonce) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
		{
			ByteBuffer buffer = ByteBuffer.allocate(48); // total, 48 Byte
			buffer.putLong(votacion.id().getId());       // votación en curso
			buffer.putLong(cee.id().getId());            // cee emisor (1 si solo hay uno)
			buffer.putLong(sector.id().getId());         // sector al que pertenece el votante (ponderación)
			buffer.putLong(candidato.id().getId());      // candidato votado
			buffer.putLong(timestamp);                   // timestamp con precisión de ms
			buffer.putLong(nonce);                       // nonce aleatorio que usamos para confirmar y evitar replays, 64 bit
			
			Signature signature = Signature.getInstance("SHA512withRSA");
			signature.initSign(privateKey);
			signature.update(buffer);
			return Base64.encodeBase64String(signature.sign()); // la firma se codifica en base64
		}
	}
	
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("transactions-optional");
	
	private Votacion votacion;
	private CEE cee;
	
	PrivateKey privateKeyCEE;
	
	private LinkedList<Candidato> candidatos; 
	private LinkedList<Sector> sectores; 
	
	private Candidato blanco;
	
	private Random rand = new Random();
	
	private HashMap<Long, Integer> censo;
	
	/**
	 * Ejecuta el simulador
	 */
	public void run() 
	{
		generarCEE();
		generarVotacion();
		generarCenso();
		generarCandidatos();
		generarVotos();
	}
	
	/**
	 * Genera un CEE de prueba
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

		cee = new CEE("Test Center", Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
		privateKeyCEE = keyPair.getPrivate();

		EntityManager em = emf.createEntityManager();	
		em.persist(cee);
		em.close();
	}
	
	/**
	 * Genera una votación de prueba
	 */
	private void generarVotacion()
	{
		votacion = new Votacion("Votación de prueba");

		EntityManager em = emf.createEntityManager();	
		em.persist(votacion);
		em.close();		
	}
	
	/**
	 * Genera: Censo y sectores
	 * grupoA	Profesores doctores con vinculación permanente
     * grupoB	Resto del profesorado y personal investigador
     * grupoC	Estudiantes
     * grupoD	Personal de administración y servicios
	 */
	private void generarCenso()
	{
		censo = new HashMap<Long, Integer>();
		sectores = new LinkedList<Sector>();
		
		HashMap<String, Integer> censoN = new HashMap<String, Integer>();
		censoN.put("grupoA", 1820);
		censoN.put("grupoB", 2297);
		censoN.put("grupoC", 39450);
		censoN.put("grupoD", 2569);

		HashMap<String, Float> censoPonderaciones = new HashMap<String, Float>();
		censoPonderaciones.put("grupoA", 0.51f);
		censoPonderaciones.put("grupoB", 0.16f);
		censoPonderaciones.put("grupoC", 0.24f);
		censoPonderaciones.put("grupoD", 0.09f);
		
		HashMap<String, String> censoNombres = new HashMap<String, String>();
		censoNombres.put("grupoA", "Profesores doctores con vinculación permanente");
		censoNombres.put("grupoB", "Resto del profesorado y personal investigador");
		censoNombres.put("grupoC", "Estudiantes");
		censoNombres.put("grupoD", "Personal de administración y servicios");

		EntityManager em = emf.createEntityManager();
		for(String n : new String[]{"grupoA", "grupoB", "grupoC", "grupoD"})
		{
			Sector sector = new Sector(votacion, censoNombres.get(n), censoPonderaciones.get(n));
			sectores.add(sector);
			em.getTransaction().begin();
			em.persist(sector);
			em.getTransaction().commit();
			censo.put(sector.id().getId(), censoN.get(n));
		}
		em.close();
	}
	
	/**
	 * Genera los candidatos
	 */
	private void generarCandidatos()
	{
		String[] nombresCandidatos = { "Guillermo Cisneros", "Carlos Conde", "Alfonso Maldonado" };
		candidatos = new LinkedList<Candidato>(); 
		
		EntityManager em = emf.createEntityManager();
		for (String nombreCandidato : nombresCandidatos)
		{
			String[] n = nombreCandidato.split(" ");
			Candidato candidato = new Candidato("test", n[0], n[1]);
			candidatos.add(candidato);
			em.persist(candidato);
		}
		
		// Candidato especial: blanco
		blanco = new Candidato("blanco", "", "blanco");
		em.persist(blanco);
		
		em.close();
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
			
			int votosEsperados = (int) Math.round(entry.getValue() * participacion);
			System.out.format("  %d votos esperados para el sector %s...", votosEsperados, entry.getKey());
			
			for (int i = 0; i < votosEsperados; i++)
			{				
				generarVoto(entry.getKey());
			}
			
			System.out.println(" ok!");
		}
	}
	
	/**
	 * Genera un voto para un miembro de un sector determinado
	 * Se basa en probabilidades para elegir a un candidato
	 * @param sectorId
	 */
	private void generarVoto(Long sectorId)
	{
		// TODO: generar un voto

		Sector sector = null;
		for (Sector s : sectores)
		{
			if (s.id().getId() == sectorId)
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
			if (magic < pBlanco + (100-pBlanco)/candidatos.size()*(i+1))
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
			
			// TODO - IMPORTANTE
			// El voto no tiene que guardarse en la base de datos, sino que tiene que enviarse mediante un POST al Servlet del CRV
			EntityManager em = emf.createEntityManager();
			em.persist(votoSimulado.voto());
			em.close();	
		} 
		catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e)
		{
			// Voto fallido! Problemas cryptográficos
			e.printStackTrace();
		}	
	}
	
	/**
	 * Borra todos los datos de la base de datos
	 */
	public void borrarDatos()
	{
		EntityManager em = emf.createEntityManager();
		
		// Borrar todas las entradas de las tablas que trabajamos
		em.createQuery("DELETE FROM Candidato c").executeUpdate();
		em.createQuery("DELETE FROM CEE c").executeUpdate();
		em.createQuery("DELETE FROM Sector s").executeUpdate();
		em.createQuery("DELETE FROM Votacion v").executeUpdate();
		em.createQuery("DELETE FROM Voto v").executeUpdate();
	}

	/**
	 * Ejecuta el simulador, borrando previamente los datos que pueda haber en la base de datos.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Simulator sim = new Simulator();
		
		sim.borrarDatos();
		sim.run();
	}

}
