plugins {
    id("java")
    id("org.jetbrains.changelog")
    id("org.jetbrains.intellij.platform")
    id("com.diffplug.spotless")
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation(libs.junit)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.3.5")
        bundledPlugin("com.intellij.java")
    }
}

spotless {
    format("javaSources") {
        target("src/main/java/**/*.java", "src/test/java/**/*.java")
        encoding("UTF-8")
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
    }
    format("resources") {
        target("*.md", "src/main/resources/**/*.properties", "src/main/resources/**/*.xml")
        encoding("UTF-8")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
