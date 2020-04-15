/*
 * Name: David Beggs
 * Assignment: Program 1
 * FileName: LtsRL.java
 * Description: LTSRL message subclass for tiktak application protocol
 * Last Modified: 2/10/2020
 */
package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static tiktak.serialization.TikTakConstants.*;

/**
 * LtsRL message subclass for tiktak application protocol
 * @version 1.0
 * @author David Beggs
 */
public class LtsRL extends Message{

    private String category;    //category for image type
    private byte [] image;      //image in base64 encoding

    /**
     * Constructor
     * @param category Alphanumeric String
     * @param image byte array
     * @throws ValidationException thrown if grammar is violated
     */
    public LtsRL(String category, byte [] image) throws ValidationException{
        super();
        this.imageValidation(image);
        this.categoryValidation(category);

        this.category = category;
        this.image = image;
    }

    /**
     * Returns current category member variable
     * @return String
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets member category to argument category
     * @param category String
     * @return This object
     * @throws ValidationException thrown if grammar is violated
     */
    public LtsRL setCategory(String category) throws ValidationException {
        if (this.categoryValidation(category)){
            this.category = category;
        }

        return this;
    }

    /**
     * Returns current image member variable
     * @return byte []
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Set member image to argument image
     * @param image byte []
     * @return This object
     * @throws ValidationException thrown if grammar is violated
     */
    public LtsRL setImage(byte[] image) throws ValidationException {
        if (imageValidation(image)){
            this.image = image;
        }

        return this;
    }

    /**
     * return String of current object
     * @return String
     */
    @Override
    public String toString() {
        return "LtsRL: category=" + this.category + " image=" + image.length + " bytes";
    }

    /**
     * Finds equality in objects
     * @param o Object
     * @return boolean if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LtsRL ltsRL = (LtsRL) o;
        return Objects.equals(category, ltsRL.category) &&
                Arrays.equals(image, ltsRL.image);
    }

    /**
     * Generates hash code of current object
     * @return 32 bit hash
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(category);
        result = LTSRL_HASH_CALC * result + Arrays.hashCode(image);
        return result;
    }

    /**
     * Verify Category arguments
     * @param category Alphanumeric String
     * @return boolean true if exception is not thrown
     * @throws ValidationException thrown if grammar is violated
     */
    private boolean categoryValidation(String category) throws ValidationException {
        if (category == null){
            throw new ValidationException("DO NOT SEND NULL STRINGS", "STRING WAS NULL");
        }

        if (category.isEmpty() || category.isBlank() || !category.matches(ZERO_OR_MORE_ALPHANUMERIC_REGEX) ){
            throw new ValidationException("CATEGORY IS NOT STRICTLY ALPHANUMERIC", category);
        }

        return true;
    }

    /**
     * Validates image is not null
     * @param image byte[]
     * @return boolean true if no exception is thrown
     * @throws ValidationException Thrown if grammar is violated
     */
    private boolean imageValidation(byte [] image) throws ValidationException {
        if (image == null){
            throw new ValidationException("NO NULL IMAGES", "NULL ARRAY");
        }

        return true;
    }

    /**
     * Encodes current object to ISO_8859_1 charset to outputstream
     * @param out MessageOutput
     * @throws IOException Thrown if IO Problem
     * @throws NullPointerException Thrown if Stream is Null
     */
    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        String returnString = "LTSRL " + this.getCategory() + " ";
        byte [] ret = Base64.getEncoder().withoutPadding().encode(getImage());

        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1));
        out.getOut().write(ret);
        out.getOut().write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }
}
