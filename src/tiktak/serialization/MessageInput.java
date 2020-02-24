/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: MessageInput.java
 * Description: Message Input stream wrapper for tiktak protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.InputStream;

/**
 * Message Inputstream wrapper for tiktak protocol
 * @version 1.0
 * @author David Beggs
 */
public class MessageInput {

    private InputStream i;      //dynamic input stream defining source

    /**
     * Constructor
     * @param in InputStream
     * @throws NullPointerException if Stream is null
     */
    public MessageInput(InputStream in) throws NullPointerException{
        if (in == null){
            throw new NullPointerException("NULL INPUT STREAM");
        }
        else {
            i = in;
        }
    }

    /**
     * Returns input stream member variable
     * @return InputStream
     */
    public InputStream getInStream() {
        return i;
    }
}
