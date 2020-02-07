package tiktak.serialization;

import java.util.Arrays;
import java.util.Objects;

public class LtsRL {

    private String category;
    private byte [] image;

    public LtsRL(String category, byte [] image){
        super();

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "LtsRL{" +
                "category='" + category + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
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
}
