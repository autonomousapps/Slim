package com.metova.slim.compiler;

import com.metova.slim.binder.LayoutBinder;
import com.metova.slim.provider.ExtraProvider;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Date;
import java.util.HashSet;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

class BinderClassBuilder {

    private static final int NO_LAYOUT_ID = -1;

    private final String mPackageName;
    private final TypeElement mClassElement;
    private final HashSet<TypeName> mInterfaceTypeNameSet = new HashSet<>();

    private MethodSpec.Builder mLayoutMethodSpec;
    private MethodSpec.Builder mExtrasMethodSpec;

    BinderClassBuilder(String packageName, TypeElement classElement) {
        mPackageName = packageName;
        mClassElement = classElement;
    }

    void writeLayout(int layoutId) {
        mLayoutMethodSpec = createLayoutMethodSpec(layoutId);
    }

    void writeExtra(String fieldName, String extraKey) {
        if (mExtrasMethodSpec == null) {
            mExtrasMethodSpec = createExtrasMethodSpec();
        }

        mExtrasMethodSpec.addCode("obj.$L = provider.getExtra(target, \"$L\");\n", fieldName, extraKey);
    }

    // void bindLayout(Object target, LayoutBinder binder);
    private MethodSpec.Builder createLayoutMethodSpec(int layoutId) {
        final String target = "target";
        final String binder = "binder";

        return MethodSpec.methodBuilder("bindLayout")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(TypeName.OBJECT, target)
                .addParameter(TypeName.get(LayoutBinder.class), binder)
                .addCode("$L.bindLayout($L, $L);\n", binder, target, layoutId);
    }

    // void bindExtras(Object target, ExtraProvider provider);
    private MethodSpec.Builder createExtrasMethodSpec() {
        final String target = "target";
        final String provider = "provider";

        return MethodSpec.methodBuilder("bindExtras")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(TypeName.OBJECT, target)
                .addParameter(TypeName.get(ExtraProvider.class), provider)
                .addCode("$T obj = ($T) target;\n", mClassElement, mClassElement);
    }

    void addInterfaceTypeName(TypeName interfaceTypeName) {
        mInterfaceTypeNameSet.add(interfaceTypeName);
    }

    JavaFile buildJavaFile(String classSuffix) {
        if (mLayoutMethodSpec == null) {
            mLayoutMethodSpec = createLayoutMethodSpec(NO_LAYOUT_ID);
        }
        if (mExtrasMethodSpec == null) {
            mExtrasMethodSpec = createExtrasMethodSpec();
        }

        TypeSpec typeSpec = TypeSpec.classBuilder(mClassElement.getSimpleName() + classSuffix)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterfaces(mInterfaceTypeNameSet)
                .addMethod(mLayoutMethodSpec.build())
                .addMethod(mExtrasMethodSpec.build())
                .build();

        return JavaFile.builder(mPackageName, typeSpec)
                .addFileComment("Generated by Slim. Do not modify!\n")
                .addFileComment(new Date(System.currentTimeMillis()).toString())
                .build();
    }

    String getCanonicalName() {
        return mPackageName + "." + mClassElement.getSimpleName();
    }
}
