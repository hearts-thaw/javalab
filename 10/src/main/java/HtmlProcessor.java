import com.google.auto.service.AutoService;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@Slf4j
@SupportedAnnotationTypes("HtmlForm")
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {

    private Configuration cfg;

    @SneakyThrows
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        // region Freemarker config
        cfg = new Configuration(Configuration.VERSION_2_3_30);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        // endregion
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element annotatedElement : annotatedElements) {
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    log.error("Only classes can be annotated with @" + annotation.getSimpleName());
                    return true;
                }

                for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                    log.info(String.valueOf(enclosedElement.getAnnotation(HtmlInput.class)));
                }

                TypeElement typeElement = (TypeElement) annotatedElement;
                HtmlFormAnnotatedClass annotatedClass = new HtmlFormAnnotatedClass(typeElement);

                annotatedClass.generateForm(processingEnv.getFiler(), cfg, annotatedElement.getEnclosedElements());
            }
        }
        return true;
    }
}
