package tiktak.serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public class LtsRL extends Message{

    private String category;
    private byte [] image;

    public LtsRL(String category, byte [] image) throws ValidationException{
        super();
        this.imageValidation(image);
        this.categoryValidation(category);

        this.category = category;
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws ValidationException {
        this.categoryValidation(category);
        this.category = category;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) throws ValidationException {
        this.imageValidation(image);
        this.image = image;
    }

    @Override
    public String toString() {
        return "LtsRL: category=" + this.category + " image= " + image.length + " bytes";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LtsRL ltsRL = (LtsRL) o;
        return Objects.equals(category, ltsRL.category) &&
                Arrays.equals(image, ltsRL.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(category);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    void categoryValidation(String category) throws ValidationException {
        if (category == null){
            throw new ValidationException("DO NOT SEND NULL STRINGS", "STRING WAS NULL");
        }

        if (category.isEmpty() && category.isBlank() && !category.matches("([0-9a-zA-Z]*)") ){
            throw new ValidationException("CATEGORY IS NOT STRICTLY ALPHANUMERIC", category);
        }
    }

    void imageValidation(byte [] image) throws ValidationException {
        if (image == null){
            throw new ValidationException("NO NULL IMAGES", "NULL ARRAY");
        }
    }

    @Override
    public void encode(MessageOutput out) throws IOException {
        Objects.requireNonNull(out, "NULL MESSAGE OUTPUT");
        Objects.requireNonNull(out.getOut(), "MESSAGE OUTPUT STREAM NULL EXCEPTION");

        if (out.getOut() == null){
            throw new IOException("Message Output Null Exception");
        }

        String returnString = "LTSRL " + this.getCategory() + " ";
        byte [] ret = Base64.getEncoder().encode(getImage());

        out.getOut().write(returnString.getBytes(StandardCharsets.ISO_8859_1 ));
        out.getOut().write(ret);
        out.getOut().write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }
}
