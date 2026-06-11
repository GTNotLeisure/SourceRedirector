package com.hfstudio.sourceredirector;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.MethodSignatureUtil;
import java.util.Optional;

public class JavaSourceMemberResolver {
    public Optional<PsiClass> findClass(PsiJavaFile sourceFile, PsiClass compiledClass) {
        String qualifiedName = compiledClass.getQualifiedName();
        if (qualifiedName == null) {
            return Optional.empty();
        }

        for (PsiClass sourceClass : sourceFile.getClasses()) {
            Optional<PsiClass> match = findClass(sourceClass, qualifiedName);
            if (match.isPresent()) {
                return match;
            }
        }

        return Optional.empty();
    }

    public Optional<PsiMethod> findMethod(PsiJavaFile sourceFile, PsiMethod compiledMethod) {
        PsiClass compiledClass = compiledMethod.getContainingClass();
        if (compiledClass == null) {
            return Optional.empty();
        }

        Optional<PsiClass> sourceClass = findClass(sourceFile, compiledClass);
        if (sourceClass.isEmpty()) {
            return Optional.empty();
        }

        PsiMethod[] sourceMethods = compiledMethod.isConstructor()
            ? sourceClass.get().getConstructors()
            : sourceClass.get().findMethodsByName(compiledMethod.getName(), false);

        for (PsiMethod sourceMethod : sourceMethods) {
            if (MethodSignatureUtil.areParametersErasureEqual(compiledMethod, sourceMethod)) {
                return Optional.of(sourceMethod);
            }
        }

        return Optional.empty();
    }

    public Optional<PsiField> findField(PsiJavaFile sourceFile, PsiField compiledField) {
        PsiClass compiledClass = compiledField.getContainingClass();
        if (compiledClass == null) {
            return Optional.empty();
        }

        Optional<PsiClass> sourceClass = findClass(sourceFile, compiledClass);
        if (sourceClass.isEmpty()) {
            return Optional.empty();
        }

        PsiField sourceField = sourceClass.get().findFieldByName(compiledField.getName(), false);
        return Optional.ofNullable(sourceField);
    }

    public Optional<PsiElement> navigationElement(PsiElement element) {
        PsiElement navigationElement = element.getNavigationElement();
        if (navigationElement == element) {
            return Optional.of(element);
        }
        return Optional.ofNullable(navigationElement);
    }

    public Optional<PsiClass> findClass(PsiClass sourceClass, String qualifiedName) {
        if (qualifiedName.equals(sourceClass.getQualifiedName())) {
            return Optional.of(sourceClass);
        }

        for (PsiClass innerClass : sourceClass.getInnerClasses()) {
            Optional<PsiClass> match = findClass(innerClass, qualifiedName);
            if (match.isPresent()) {
                return match;
            }
        }

        return Optional.empty();
    }
}
