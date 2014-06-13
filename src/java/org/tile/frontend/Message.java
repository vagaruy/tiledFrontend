/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

/**
 *
 * @author Testing
 */
public class Message {
    
    private byte type;
    private byte value;
    private byte SerialNo;

    public Message(byte type, byte value, byte SerialNo) {
        this.type = type;
        this.value = value;
        this.SerialNo = SerialNo;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    public byte getSerialNo() {
        return SerialNo;
    }

    public void setSerialNo(byte SerialNo) {
        this.SerialNo = SerialNo;
    }
    
    
    
}
