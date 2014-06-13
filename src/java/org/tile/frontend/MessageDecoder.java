/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;


import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Testing
 */
public class MessageDecoder implements Decoder.Text<TileMessage>{
    

    @Override
    public TileMessage decode(String s) throws DecodeException {
        JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
        return  new TileMessage(jsonObject);
    }

    @Override
    public boolean willDecode(String s) {
        try {
            Json.createReader(new StringReader(s)).readObject();
            return true;
        } catch (JsonException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
        //System.out.println("init");
    }

    @Override
    public void destroy() {
        //System.out.println("destroy");
    }
    
}
