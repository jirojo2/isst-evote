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
import es.upm.dit.isst.evote.crv.json.ResultadosVotacion;
import es.upm.dit.isst.evote.crv.json.ResultadosVotacionPorEscuelas;
import es.upm.dit.isst.evote.model.CEE;
import es.upm.dit.isst.evote.model.Candidato;
import es.upm.dit.isst.evote.model.Escuela;
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
	
	public ResultadosVotacion resultadosVotacion(Votacion votacion)
	{
		ResultadosVotacion resultados = new ResultadosVotacion();
		
		List<Candidato> candidatos = CRVDAO.instance.candidatosVotacion(votacion);
		for (Candidato candidato : candidatos)
		{
			int votos = CRVDAO.instance.votosCandidatoVotacion(candidato, votacion);
			resultados.votosDelCandidato(candidato, votos);
		}
		
		return resultados;
	}
	
	public ResultadosVotacionPorEscuelas resultadosVotacionPorEscuelas(Votacion votacion)
	{
		ResultadosVotacion globales = this.resultadosVotacion(votacion);
		ResultadosVotacionPorEscuelas resultados = new ResultadosVotacionPorEscuelas(globales);
		
		List<Candidato> candidatos = CRVDAO.instance.candidatosVotacion(votacion);
		List<Escuela> escuelas = CRVDAO.instance.escuelasVotacion(votacion);
		for (Escuela escuela : escuelas) {
			ResultadosVotacion local = new ResultadosVotacion();
			for (Candidato candidato : candidatos)
			{
				int votos = CRVDAO.instance.votosCandidatoEscuelaVotacion(candidato, escuela, votacion);				
				local.votosDelCandidato(candidato, votos);
			}
			resultados.escuela(escuela, local);
		}
		
		return resultados;
	}
}
