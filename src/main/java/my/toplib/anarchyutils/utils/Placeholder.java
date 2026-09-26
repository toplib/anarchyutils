package my.toplib.anarchyutils.utils;

public class Placeholder {

    private String placeholder;
    private String replacement;

    public Placeholder(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public String toString() {
        return "Placeholder{" +
                "placeholder='" + placeholder + '\'' +
                ", replacement='" + replacement + '\'' +
                '}';
    }
}
