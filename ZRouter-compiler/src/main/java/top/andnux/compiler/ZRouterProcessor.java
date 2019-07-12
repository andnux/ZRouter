package top.andnux.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
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
        TypeSpec.Builder routers = TypeSpec.classBuilder("Routers")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        //private Map<String, Class<?>> mRouteMap = new HashMap<>();
        TypeName typeName = TypeName.get(Map.class);
        FieldSpec build = FieldSpec.builder(typeName,"mRouteMap").build();
        TypeName hashMap = TypeName.get(HashMap.class);
        builder.addStatement("mRouteMap = new $T()",hashMap);
        routers.addField(build);

        for (Element element : routerElement) {
            TypeElement typeElement = (TypeElement) element;
            Router annotation = element.getAnnotation(Router.class);
            if (annotation == null) continue;
            Name simpleName = typeElement.getSimpleName();
            messager.printMessage(Diagnostic.Kind.NOTE, annotation.value());
            ClassName className = ClassName.get(typeElement);
            messager.printMessage(Diagnostic.Kind.NOTE, simpleName.toString());
            builder.addStatement("mRouteMap.put($S,$T.class)", annotation.value(), className);
        }
        routers.addMethod(builder.build());
        try {
            JavaFile javaFile = JavaFile.builder("top.andnux.router", routers.build())
                    .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
