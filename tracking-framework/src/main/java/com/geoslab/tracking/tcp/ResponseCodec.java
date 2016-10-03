// ***************************************************************
// COPYRIGHT(C): IAAA 2014, Todos los derechos reservados
// PROYECTO....: tracking-framework
// ARCHIVO.....: ResponseCodec.java
// CREACION....: 28/11/2014 rodolfo 
// ULTIMA MODIF: TODO Fecha y Quién de la última modificacion
// LENGUAJE....: TODO Versión Plataforma Java
// PLATAFORMA..: TODO Microsoft Windows
// REQUERIM....: TODO Librerías de terceros	    
// DESCRIPCION.: TODO Descripción
// HISTORIA....: TODO Fecha y Quién - Cambios relevantes
// ............:
// ***************************************************************
package com.geoslab.tracking.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import reactor.function.Consumer;
import reactor.function.Function;
import reactor.io.Buffer;
import reactor.io.encoding.Codec;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoslab.tracking.web.domain.Response;

/**
 * TODO Describir la funcionalidad.
 * 
 * @author TODO creador del interfaz o clase.
 * @version TODO versión del interfaz o clase.
 */
public class ResponseCodec implements Codec<Buffer, Response, String> {
    private final Charset utf8 = Charset.forName("UTF-8");
    @Override
    public Function<Buffer, Response> decoder(Consumer<Response> next) {
      return new ResponseDecoder(next);
    }

    @Override
    public Function<String, Buffer> encoder() {
      return new ResponseEncoder();
    }

    private class ResponseDecoder implements Function<Buffer, Response> {
      private final Consumer<Response> next;
      private final CharsetDecoder decoder = utf8.newDecoder();

      private ResponseDecoder(Consumer<Response> next) {
        this.next = next;
      }

      @Override
      public Response apply(Buffer bytes) {
        try {
          String s = decoder.decode(bytes.byteBuffer()).toString();
          ObjectMapper mapper = new ObjectMapper();
              Response response = mapper.readValue(s, Response.class);
          if (null != next) {
              
            next.accept(response);
            return null;
          } else {
            return response;
          }
        } catch (CharacterCodingException e) {
          throw new IllegalStateException(e);
        }
        catch (JsonParseException e) {
            throw new IllegalStateException(e);
        }
        catch (JsonMappingException e) {
            throw new IllegalStateException(e);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
      }
    }

    private class ResponseEncoder implements  Function<String, Buffer> {
      private final CharsetEncoder encoder = utf8.newEncoder();

      @Override
      public Buffer apply(String s) {
        try {
          ByteBuffer bb = encoder.encode(CharBuffer.wrap(s));
          return new Buffer(bb);
        } catch (CharacterCodingException e) {
          throw new IllegalStateException(e);
        } 
      }
    }

}
