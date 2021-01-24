import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HtmlFormAnnotatedClass {
    private final TypeElement typeElement;

    public HtmlFormAnnotatedClass(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    @SneakyThrows
    public void generateForm(Filer filer, Configuration cfg, List<? extends Element> enclosedElements) {
        FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", typeElement.getQualifiedName().toString() + ".html");
        Writer writer = fileObject.openWriter();
        PrintWriter out = new PrintWriter(writer, true);
        Template template = cfg.getTemplate("form.ftl");
        Map<String, Object> root = new HashMap<>();
        HtmlForm htmlForm = typeElement.getAnnotation(HtmlForm.class);
        root.put("action", htmlForm.action());
        root.put("method", htmlForm.method());
        root.put("inps", enclosedElements.stream()
                .filter(element -> element.getKind().isField())
                .map(element -> new HtmlInputAnnotatedClass(element.getAnnotation(HtmlInput.class)))
                .collect(Collectors.toList()));
        Environment env = template.createProcessingEnvironment(root, out);
        env.setOutputEncoding("UTF-8");
        env.process();
    }
}
