# SourceRedirector

SourceRedirector 是一个 IntelliJ IDEA 插件，用于让 Multi-Release JAR 布局下的库跳转尽量保持在已附加的源码文件中。

当依赖在 `META-INF/versions/<java>/` 下包含版本化 class 时，如果对应版本化源码不存在，IDEA 可能会跳转到 compiled entry 并打开反编译视图。本插件会先检查同一个库已附加的 sources JAR 中是否存在库根源码文件，如果存在，就在 IDEA 回退到反编译内容前重定向到该源码。

## 行为

- 只处理 `META-INF/versions/<number>/` 下的 compiled library entry。
- 只搜索同一个库的 `SOURCES` roots。
- 只有库根源码文件真实存在时才重定向。
- 尽可能把类、方法、字段定位到匹配的源码 PSI 元素。
- 不读取 class 字节码，也不调用反编译器。

## 开发

运行格式化和 Java 编译验证：

```powershell
.\gradlew.bat spotlessApply compileJava
```

插件依赖 IntelliJ Java 模块，因为实现需要使用 Java PSI 和 compiled Java library navigation。
