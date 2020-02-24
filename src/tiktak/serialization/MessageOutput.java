/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: MessageOutput.java
 * Description: Message Output stream wrapper for tiktak protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.OutputStream;

/**
 * Message Outputstream wrapper for tiktak protocol
 * @version 1.0
 * @author David Beggs
 */
public class MessageOutput {

    private OutputStream out;       //dynamic output stream defining protocol destination

    /**
     * Constructor
     * @param out OutputStream
     * @throws NullPointerException thrown if stream is null
     */
    public MessageOutput(OutputStream out) throws NullPointerException{
        if (out == null){
            throw new NullPointerException("NULL OUTPUT STREAM");
        }
        else {
            this.out = out;
        }
    }

    /**
     * Returns outputstream member variable
     * @return OutputStream
     */
    public OutputStream getOut() {
        return out;
    }

}
