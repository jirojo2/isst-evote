# isst-evote
Implementación del caso de estudio del voto electrónico para la asignatura ISST, ETSIT UPM

## CA

### Referencias  
https://www.mayrhofer.eu.org/create-x509-certs-in-java

## CRV

### Funcionalidad  
Recibe una petición POST del CEE con los datos del voto emitido (id_votacion, id_cee, id_candidato, timestamp) y la firma usando esos campos por parte del CEE emisor. Dicha firma se comprueba con la clave pública del CEE que conoce el CRV, validando la autenticidad y la integridad del voto, respetando la confidencialidad del votante. Este voto se añade a la base de datos del CRV, actualizando el recuento de votos para esa votación.  

Por otra parte, al recibir una petición GET a /resultados?id=<id_votación> (o similar), se devolverá, en formato JSON, el total de votos contabilizados, el candidato ganador y el recuento por candidato de los votos.  

### Dependencias  
* GSON
* Apache Commons Codec
