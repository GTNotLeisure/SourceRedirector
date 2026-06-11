package com.hfstudio.sourceredirector;

import com.intellij.openapi.vfs.VirtualFile;
import java.util.Optional;

public class MultiReleasePathMapper {
    public Optional<MultiReleaseSourcePath> toRootSourcePath(VirtualFile file) {
        String entryPath = jarEntryPath(file);
        if (entryPath == null) {
            return Optional.empty();
        }

        String marker = "META-INF/versions/";
        int markerOffset = entryPath.indexOf(marker);
        if (markerOffset < 0) {
            return Optional.empty();
        }

        String versionAndClassPath = entryPath.substring(markerOffset + marker.length());
        int versionEnd = versionAndClassPath.indexOf('/');
        if (versionEnd <= 0 || versionEnd == versionAndClassPath.length() - 1) {
            return Optional.empty();
        }

        String javaVersion = versionAndClassPath.substring(0, versionEnd);
        if (!isJavaVersion(javaVersion)) {
            return Optional.empty();
        }

        String versionRelativePath = versionAndClassPath.substring(versionEnd + 1);
        String rootRelativePath = toJavaSourcePath(versionRelativePath);
        if (rootRelativePath == null) {
            return Optional.empty();
        }

        return Optional.of(new MultiReleaseSourcePath(javaVersion, rootRelativePath));
    }

    public String toJavaSourcePath(String classRelativePath) {
        if (!classRelativePath.endsWith(".class")) {
            return null;
        }

        String javaRelativePath = classRelativePath.substring(0, classRelativePath.length() - ".class".length());
        int nameStart = javaRelativePath.lastIndexOf('/') + 1;
        int innerClassStart = javaRelativePath.indexOf('$', nameStart);
        if (innerClassStart >= 0) {
            javaRelativePath = javaRelativePath.substring(0, innerClassStart);
        }

        return javaRelativePath + ".java";
    }

    public String jarEntryPath(VirtualFile file) {
        String path = file.getPath().replace('\\', '/');
        int separator = path.indexOf("!/");
        if (separator < 0 || separator + 2 >= path.length()) {
            return null;
        }
        return path.substring(separator + 2);
    }

    public boolean isJavaVersion(String text) {
        if (text.isEmpty()) {
            return false;
        }

        for (int index = 0; index < text.length(); index++) {
            if (!Character.isDigit(text.charAt(index))) {
                return false;
            }
        }
        return true;
    }
}
