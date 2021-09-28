package com.chlhrssj.processor;

import com.chlhrssj.annotation.BindClick;
import com.chlhrssj.annotation.BindExtra;
import com.chlhrssj.annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Create by rssj on 2021/9/27
 */
@AutoService(Processor.class)
public class BindProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Writer writer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "RSSJ注解启动！");

        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

    }

    /**
     * 要扫描扫描的注解，可以添加多个
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add(BindClick.class.getCanonicalName());
        hashSet.add(BindExtra.class.getCanonicalName());
        hashSet.add(BindView.class.getCanonicalName());
        return hashSet;
    }

    /**
     * 编译版本，固定写法就可以
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(BindView.class);
        Set<? extends Element> bindExtraElements = roundEnv.getElementsAnnotatedWith(BindExtra.class);
        Set<? extends Element> bindClickElements = roundEnv.getElementsAnnotatedWith(BindClick.class);

        String packName = "com.rssj.binder";

        //遍历所有元素，获取用到注解的所有Activity名字
        Set<TypeElement> actNames = new HashSet<>();
        for (Element element : bindViewElements) {
            //获取成员变量所在的类的类名
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            actNames.add(typeElement);
        }
        for (Element element : bindExtraElements) {
            //获取成员变量所在的类的类名
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            actNames.add(typeElement);
        }
        for (Element element : bindClickElements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            actNames.add(typeElement);
        }

        if (actNames.size() > 0) {

            //生成类
            TypeSpec.Builder classBuilder = TypeSpec
                    .classBuilder("RssjBindUtil")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            Iterator<TypeElement> iterator = actNames.iterator();
            while (iterator.hasNext()) {
                TypeElement activityElement = iterator.next();
                //将属于当前Activity的对象分拣出来
                MethodSpec.Builder build = MethodSpec.methodBuilder("bind")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(ClassName.get(activityElement), "target")
                        .returns(void.class);

                for (Element element : bindViewElements) {
                    VariableElement variableElement = (VariableElement) element;
                    TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                        String error = typeElement.getQualifiedName() + "." + element.getSimpleName() + "对象必须为PUBLIC !!!";
                        messager.printMessage(Diagnostic.Kind.ERROR, error, element);
                    }
                    if (activityElement == typeElement) {
                        String vName = variableElement.getSimpleName().toString();
                        int resId = variableElement.getAnnotation(BindView.class).value();
                        build.addStatement("target." + vName + " = target.findViewById(" + resId + ")");
                    }
                }
                for (Element element : bindExtraElements) {
                    VariableElement variableElement = (VariableElement) element;
                    TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                        String error = typeElement.getQualifiedName() + "." + element.getSimpleName() + "变量必须为PUBLIC !!!";
                        messager.printMessage(Diagnostic.Kind.ERROR, error, element);
                    }
                    if (activityElement == typeElement && variableElement.getKind().isField()) {
                        String vName = variableElement.getSimpleName().toString();
                        String key = variableElement.getAnnotation(BindExtra.class).value();
                        TypeMirror mirror = variableElement.asType();
                        switch (mirror.getKind()) {
                            case INT:
                                build.addStatement("target." + vName + " = target.getIntent().getIntExtra(\"" + key + "\", 0)");
                                break;
                            case DOUBLE:
                                build.addStatement("target." + vName + " = target.getIntent().getDoubleExtra(\"" + key + "\", 0f)");
                                break;
                            case LONG:
                                build.addStatement("target." + vName + " = target.getIntent().getLongExtra(\"" + key + "\", 0L)");
                                break;
                            case DECLARED:
                                if (mirror.toString().equals("java.lang.String")) {
                                    //字符串
                                    build.addStatement("target." + vName + " = target.getIntent().getStringExtra(\"" + key + "\")");
                                    build.addStatement("if (target." + vName + " == null) target." + vName + " = \"\"");
                                }
                                break;
                        }
                    }
                }
                for (Element element : bindClickElements) {
                    TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                    if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                        String error = typeElement.getQualifiedName() + "." + element.getSimpleName() + "方法必须为PUBLIC !!!";
                        messager.printMessage(Diagnostic.Kind.ERROR, error, element);
                    }
                    if (activityElement == typeElement) {
                        int[] key = element.getAnnotation(BindClick.class).value();
                        for (int i = 0;i < key.length;i++) {
                            int resId = key[i];
                            build.addStatement("target.findViewById(" + resId + ").setOnClickListener(v -> {target." + element.getSimpleName() + "(v);})");
                        }
                    }
                }

                classBuilder.addMethod(build.build());
            }

            //包
            JavaFile javaFile = JavaFile
                    .builder(packName, classBuilder.build())
                    .build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.WARNING, e.toString());
            }

        }

        return false;
    }

}
