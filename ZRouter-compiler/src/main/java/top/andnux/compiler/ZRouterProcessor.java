package top.andnux.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import top.andnux.annotation.AutoValue;
import top.andnux.annotation.Router;

public class ZRouterProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> list = new HashSet<>();
        list.add(Router.class.getCanonicalName());
        list.add(AutoValue.class.getCanonicalName());
        return list;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> routerElement = roundEnvironment.getElementsAnnotatedWith(Router.class);
        TypeSpec.Builder zRouter = TypeSpec.classBuilder("ZRouter")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Element element : routerElement) {
            messager.printMessage(Diagnostic.Kind.NOTE, element.toString());
            TypeElement typeElement = (TypeElement) element;
            ClassName context = ClassName.get("android.content", "Context");
            MethodSpec.Builder builder = MethodSpec.methodBuilder("launch" + typeElement.getSimpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(context, "context");

            boolean isFirst = true;
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                AutoValue annotation = enclosedElement.getAnnotation(AutoValue.class);
                if (annotation != null) {
                    String value = annotation.value();
                    if (value.length() == 0) {
                        value = enclosedElement.getSimpleName().toString();
                    }
                    TypeMirror typeMirror = enclosedElement.asType();
                    messager.printMessage(Diagnostic.Kind.NOTE, typeMirror.toString());
                    builder.addParameter(ClassName.get(typeMirror), value);
                    if (isFirst) {
                        ClassName intent = ClassName.get("android.content", "Intent");
                        ClassName name = ClassName.get(typeElement);
                        messager.printMessage(Diagnostic.Kind.NOTE, name.toString());
                        builder.addStatement("$T intent = new $T(context,$T.class)", intent, intent, name);
                        ClassName activity = ClassName.get("android.app", "Activity");
                        builder.addStatement("if (!(context instanceof $T)){", activity);
                        builder.addStatement("intent.addFlags($T.FLAG_ACTIVITY_NEW_TASK)", intent);
                        builder.addStatement("}");
                        isFirst = false;
                    }
                    builder.addStatement("intent.putExtra(" + "\"" + value + "\"" + "," + value + ")");
                }
            }
            builder.addStatement(" context.startActivity(intent)");
            zRouter.addMethod(builder.build());
        }
        try {
            JavaFile javaFile = JavaFile.builder("top.andnux.zrouter", zRouter.build())
                    .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
