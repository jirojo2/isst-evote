package es.upm.dit.isst.evote.crv.json;

import java.util.Date;

public class RespuestaVoto
{
	private String respuesta;
	private long nonce;
	private long timestamp;
	
	public RespuestaVoto()
	{
		this.timestamp = new Date().getTime();
	}

	public String getRespuesta()
	{
		return respuesta;
	}

	public void setRespuesta(String respuesta)
	{
		this.respuesta = respuesta;
	}

	public long getNonce()
	{
		return nonce;
	}

	public void setNonce(long nonce)
	{
		this.nonce = nonce;
	}

	public long getTimestamp()
	{
		return timestamp;
	}
}
