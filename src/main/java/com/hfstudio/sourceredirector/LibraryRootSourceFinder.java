package com.hfstudio.sourceredirector;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import java.util.Collection;
import java.util.Optional;

public class LibraryRootSourceFinder {
    public Optional<PsiFile> findRootSourceFile(Project project, VirtualFile compiledFile, String rootRelativePath) {
        Collection<OrderEntry> orderEntries = ProjectFileIndex.getInstance(project).getOrderEntriesForFile(compiledFile);
        PsiManager psiManager = PsiManager.getInstance(project);

        for (OrderEntry orderEntry : orderEntries) {
            if (!(orderEntry instanceof LibraryOrderEntry libraryOrderEntry)) {
                continue;
            }

            Library library = libraryOrderEntry.getLibrary();
            if (library == null) {
                continue;
            }

            Optional<PsiFile> sourceFile = findRootSourceFile(psiManager, library, rootRelativePath);
            if (sourceFile.isPresent()) {
                return sourceFile;
            }
        }

        return Optional.empty();
    }

    public Optional<PsiFile> findRootSourceFile(PsiManager psiManager, Library library, String rootRelativePath) {
        for (VirtualFile sourceRoot : library.getFiles(OrderRootType.SOURCES)) {
            VirtualFile sourceFile = sourceRoot.findFileByRelativePath(rootRelativePath);
            if (sourceFile == null || !sourceFile.isValid()) {
                continue;
            }

            PsiFile psiFile = psiManager.findFile(sourceFile);
            if (psiFile != null) {
                return Optional.of(psiFile);
            }
        }

        return Optional.empty();
    }
}
