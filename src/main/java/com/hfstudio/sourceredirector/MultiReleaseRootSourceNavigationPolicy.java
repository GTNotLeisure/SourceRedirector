package com.hfstudio.sourceredirector;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.compiled.ClsCustomNavigationPolicy;
import com.intellij.psi.impl.compiled.ClsFieldImpl;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiReleaseRootSourceNavigationPolicy implements ClsCustomNavigationPolicy {
    private final MultiReleasePathMapper pathMapper;
    private final LibraryRootSourceFinder sourceFinder;
    private final JavaSourceMemberResolver memberResolver;

    public MultiReleaseRootSourceNavigationPolicy() {
        this(new MultiReleasePathMapper(), new LibraryRootSourceFinder(), new JavaSourceMemberResolver());
    }

    public MultiReleaseRootSourceNavigationPolicy(
        MultiReleasePathMapper pathMapper,
        LibraryRootSourceFinder sourceFinder,
        JavaSourceMemberResolver memberResolver
    ) {
        this.pathMapper = pathMapper;
        this.sourceFinder = sourceFinder;
        this.memberResolver = memberResolver;
    }

    @Override
    public @Nullable PsiElement getNavigationElement(@NotNull ClsFileImpl clsFile) {
        return rootSourceFile(clsFile).flatMap(memberResolver::navigationElement).orElse(null);
    }

    @Override
    public @Nullable PsiElement getNavigationElement(@NotNull ClsClassImpl clsClass) {
        return rootJavaSourceFile(clsClass)
            .flatMap(sourceFile -> memberResolver.findClass(sourceFile, clsClass))
            .flatMap(memberResolver::navigationElement)
            .orElse(null);
    }

    @Override
    public @Nullable PsiElement getNavigationElement(@NotNull ClsMethodImpl clsMethod) {
        return rootJavaSourceFile(clsMethod)
            .flatMap(sourceFile -> memberResolver.findMethod(sourceFile, clsMethod))
            .flatMap(memberResolver::navigationElement)
            .orElse(null);
    }

    @Override
    public @Nullable PsiElement getNavigationElement(@NotNull ClsFieldImpl clsField) {
        return rootJavaSourceFile(clsField)
            .flatMap(sourceFile -> memberResolver.findField(sourceFile, clsField))
            .flatMap(memberResolver::navigationElement)
            .orElse(null);
    }

    public Optional<PsiJavaFile> rootJavaSourceFile(PsiElement compiledElement) {
        return rootSourceFile(compiledElement).filter(PsiJavaFile.class::isInstance).map(PsiJavaFile.class::cast);
    }

    public Optional<PsiFile> rootSourceFile(PsiElement compiledElement) {
        PsiFile containingFile = compiledElement.getContainingFile();
        if (!(containingFile instanceof ClsFileImpl clsFile)) {
            return Optional.empty();
        }

        VirtualFile compiledFile = clsFile.getVirtualFile();
        Optional<MultiReleaseSourcePath> sourcePath = pathMapper.toRootSourcePath(compiledFile);
        if (sourcePath.isEmpty()) {
            return Optional.empty();
        }

        Project project = clsFile.getProject();
        return sourceFinder.findRootSourceFile(project, compiledFile, sourcePath.get().rootRelativePath());
    }
}
