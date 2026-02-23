plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("com.lab.lab1.MainApp")
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.graphics")
}