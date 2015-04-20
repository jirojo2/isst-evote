package es.upm.dit.isst.evote.crv;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import es.upm.dit.isst.evote.crv.dao.CRVDAO;
import es.upm.dit.isst.evote.crv.json.Resultados;
import es.upm.dit.isst.evote.crv.json.ResultadosCandidato;
import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Sector;
import es.upm.dit.isst.evote.model.Votacion;
import es.upm.dit.isst.evote.model.Voto;

/**
 * Centro de Recuento de Votos
 * 
 * Se encarga de llevar el recuento de los votos emitidos
 * por el CEE, que ya han sido verificados con el centro,
 * y preservan la confidencialidad del votante.
 * 
 * @author Josi
 *
 */
public class CRV
{
	public static CRV instance = new CRV();
	
	private CRV()
	{
	}
	
	public boolean procesarVoto(Voto voto) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, UnsupportedEncodingException, InvalidKeySpecException
	{
		if (!validarFirmaVoto(voto))
		{
			return false;
		}
		CRVDAO.instance.registrar(voto);
		return true;
	}
	
	public PublicKey obtenerClavePublicaDeCEE(CEE cee) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] decoded = Base64.decodeBase64(cee.clavePublica());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey key = keyFactory.generatePublic(spec);
		return key;
	}
	
	public boolean validarFirmaVoto(Voto voto) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, InvalidKeySpecException
	{
		byte[] decoded = Base64.decodeBase64(voto.firma());
		Signature signature = Signature.getInstance("SHA512withRSA");
		signature.initVerify(obtenerClavePublicaDeCEE(voto.cee()));
		signature.update(voto.datosParaValidarFirma());
		return signature.verify(decoded);
	}
	
	public Resultados resultados(Votacion votacion)
	{
		Resultados resultados = new Resultados();
		
		List<Candidato> candidatos = CRVDAO.instance.candidatosVotacion(votacion);
		List<Sector> sectores = CRVDAO.instance.sectoresVotacion(votacion);
		
		for (Sector sector : sectores)
		{
			int votosBlanco = CRVDAO.instance.votosBlancoSector(votacion, sector);
			int totalVotos = CRVDAO.instance.votosSector(votacion, sector);
			resultados.getVotosBlanco().put(sector, votosBlanco);
			resultados.getVotosEmitidos().put(sector, totalVotos);
		}
		
		for (Candidato candidato : candidatos)
		{
			ResultadosCandidato rc = new ResultadosCandidato();
			rc.setCandidato(candidato);
			
			double totalPonderado = 0.0;
			
			for (Sector sector : sectores)
			{
				int votos = CRVDAO.instance.votosCandidatoSector(votacion, candidato, sector);
				int votosSectorValidos = resultados.getVotosEmitidos().get(sector) - resultados.getVotosBlanco().get(sector);
				totalPonderado += votos * sector.ponderacion() / votosSectorValidos;
				rc.getVotos().put(sector, votos);
			}
			
			rc.setTotalPonderado(totalPonderado);
			resultados.getCandidatos().add(rc);
		}
		
		return resultados;
	}
}
