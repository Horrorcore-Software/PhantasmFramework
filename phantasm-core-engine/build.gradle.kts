plugins {
    java
    id("org.lwjgl.plugin") version "0.0.35"
}

group = "com.horrorcoresoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.5"
val lwjglNatives = listOf("natives-linux", "natives-windows", "natives-macos", "natives-macos-arm64")

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.joml", "joml", jomlVersion)

    lwjglNatives.forEach { native ->
        runtimeOnly("org.lwjgl", "lwjgl", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = native)
        runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = native)
    }

    // Physics engine (jBullet)
    implementation("cz.advel.jbullet:jbullet:20101010-1")

    // Scene graph library (JMonkeyEngine's scene graph, as an example)
    implementation("org.jmonkeyengine:jme3-core:3.5.2-stable")

    // UI framework (libGDX Scene2D, as an example)
    implementation("com.badlogicgames.gdx:gdx:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}