package me.juhezi.module.router_compiler.processor;

import com.google.auto.service.AutoService;
import com.juhezi.module.router_annotation.annotation.Proxy;
import com.juhezi.module.router_annotation.annotation.Register;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import me.juhezi.module.router_compiler.ProxyHandler;
import me.juhezi.module.router_compiler.extensions.MessagerExKt;

/**
 * Created by Juhezi on 2017/8/17.
 */
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    private ProxyHandler mProxyHandler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFileUtils = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            annotationTypes.add(annotation.getCanonicalName());
        }
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> proxyElements = roundEnvironment
                .getElementsAnnotatedWith(Proxy.class);
        Set<? extends Element> registerElements = roundEnvironment
                .getElementsAnnotatedWith(Register.class);
        /*for (Element element : proxyElements) {
            MessagerExKt.print(mMessager, element.getSimpleName().toString() + "=====");
        }
        for (Element element : registerElements) {
            MessagerExKt.print(mMessager, element.getSimpleName().toString() + "---->");
        }*/
        for (Element element : registerElements) {
            MessagerExKt.print(mMessager, element.getSimpleName().toString() + "---->");
        }
        if (proxyElements.size() <= 0) return false;
        for (Element element : proxyElements) {
            mProxyHandler = new ProxyHandler(element);
            break;
        }
        if (mProxyHandler == null) return false;
        mProxyHandler.addAll(registerElements);
        try {
            generateCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void generateCode() throws IOException {
        JavaFileObject f = mFileUtils.createSourceFile(
                mProxyHandler.getGenerateClassName());
        MessagerExKt.print(mMessager, "Creating " + f.toUri());
        try (Writer w = f.openWriter()) {
            PrintWriter pw = new PrintWriter(w);
            pw.print(mProxyHandler.generate());
            pw.flush();
        }
    }

    private boolean checkAnnotationUseValid(Element element) {
        return element instanceof TypeElement;  //只接受类
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotation = new LinkedHashSet<>();
        annotation.add(Proxy.class);
        annotation.add(Register.class);
        return annotation;
    }

}
