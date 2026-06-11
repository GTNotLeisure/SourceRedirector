# SourceRedirector

SourceRedirector is an IntelliJ IDEA plugin that keeps library navigation on attached source files for Multi-Release JAR layouts.

When a dependency contains versioned classes under `META-INF/versions/<java>/`, IDEA can navigate to a compiled entry and open a decompiled view if the matching versioned source file is missing. This plugin checks whether the same library has a root-level source file in the attached sources JAR and redirects navigation there before IDEA falls back to decompiled content.

## Behavior

- Handles compiled library entries under `META-INF/versions/<number>/`.
- Searches only the same library's attached `SOURCES` roots.
- Redirects to the root source file only when that file already exists.
- Resolves classes, methods, and fields to matching source PSI elements when possible.
- Does not read class bytecode or invoke the decompiler.

## Development

Run formatting and Java compilation checks:

```powershell
.\gradlew.bat spotlessApply compileJava
```

The plugin depends on the IntelliJ Java module because it works with Java PSI and compiled Java library navigation.
