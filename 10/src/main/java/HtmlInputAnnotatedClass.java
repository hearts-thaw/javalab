import lombok.Getter;

@Getter
public class HtmlInputAnnotatedClass {
    private final String type;
    private final String name;
    private final String placeholder;

    public HtmlInputAnnotatedClass(HtmlInput annotation) {
        this.type = annotation.type();
        this.name = annotation.name();
        this.placeholder = annotation.placeholder();
    }
}
