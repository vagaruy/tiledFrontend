/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author Testing
 */
public class TileMessage {
    private JsonObject json;

    public TileMessage(JsonObject json) {
        this.json = json;
    }

    public void setJson(JsonObject json) {
        this.json = json;
    }

    public JsonObject getJson() {
        return json;
    }
    
     
}
