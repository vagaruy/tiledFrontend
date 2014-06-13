/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author Testing
 */

public class MessageEncoder implements Encoder.Text<TileMessage>{

    @Override
    public String encode(TileMessage object) throws EncodeException {
        return object.getJson().toString();
    }

    @Override
    public void init(EndpointConfig config) {
       // System.out.println("inti");
    }

    @Override
    public void destroy() {
       // System.out.println("destroy");
    }

   
    
}
