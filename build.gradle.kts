import org.gradle.api.tasks.compile.JavaCompile

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    id("maven-publish")
}

val modVersion = property("mod.version") as String
val modGroup = property("mod.group") as String
val modId = property("mod.id") as String
val modName = property("mod.name") as String
val modDescription = property("mod.description") as String
val minecraftVersion = property("minecraft_version") as String
val loaderVersion = property("loader_version") as String
val fabricVersion = property("fabric_version") as String
val clothVersion = property("cloth_version") as String
val modmenuVersion = property("modmenu_version") as String
val fabricPermissionsApiVersion = property("fabric_permissions_api_version") as String
val serverTranslationsApiVersion = property("server_translations_api_version") as String

version = modVersion
group = modGroup

sourceSets {
    main {
        java.setSrcDirs(listOf("remappedSrc"))
        java.exclude("**/client/**")
        java.exclude("**/e2e/**")
        java.exclude("**/mixin/MixinContainerScreen.java")
        java.exclude("**/mixin/MixinCreativeInventoryScreen.java")
        java.exclude("**/mixin/RecipeBookScreenAccessor.java")
    }
}

repositories {
    mavenLocal()
    maven("https://maven.terraformersmc.com/releases")
    maven("https://maven.shedaniel.me")
    maven("https://maven.meza.gg/releases")
    maven("https://maven.nucleoid.xyz")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

    implementation("com.github.erosb:everit-json-schema:1.14.4")
    include("com.github.erosb:everit-json-schema:1.14.4")
    implementation("blue.endless:jankson:1.2.3")
    include("blue.endless:jankson:1.2.3")
    include("org.json:json:20231013")

    implementation("me.lucko:fabric-permissions-api:$fabricPermissionsApiVersion")
    include("me.lucko:fabric-permissions-api:$fabricPermissionsApiVersion")

    implementation("xyz.nucleoid:server-translations-api:$serverTranslationsApiVersion")
    include("xyz.nucleoid:server-translations-api:$serverTranslationsApiVersion")

    testImplementation("net.fabricmc:fabric-loader-junit:$loaderVersion")
    testImplementation("com.google.jimfs:jimfs:1.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${modId}" }
    }
    exclude("**/e2e/**")
}

tasks.processResources {
    inputs.property("version", modVersion)
    inputs.property("id", modId)
    inputs.property("name", modName)
    inputs.property("description", modDescription)
    inputs.property("fabricVersion", fabricVersion)
    inputs.property("clothVersion", clothVersion)
    inputs.property("modmenuVersion", modmenuVersion)
    inputs.property("loaderVersion", loaderVersion)
    inputs.property("minecraftVersion", minecraftVersion)
    inputs.property("fabricPermissionsApiVersion", fabricPermissionsApiVersion)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to modVersion,
                "id" to modId,
                "name" to modName,
                "description" to modDescription,
                "fabricVersion" to fabricVersion,
                "clothVersion" to clothVersion,
                "modmenuVersion" to modmenuVersion,
                "loaderVersion" to loaderVersion,
                "minecraftVersion" to minecraftVersion,
                "fabricPermissionsApiVersion" to fabricPermissionsApiVersion,
            )
        )
    }

    doLast {
        val resourcesDir = project.layout.buildDirectory.dir("resources/main")
        val srcDir = resourcesDir.get().dir("assets/$modId/lang")
        val destDir = resourcesDir.get().dir("data/$modId/lang")

        if (srcDir.asFile.exists()) {
            destDir.asFile.mkdirs()
            copy {
                from(srcDir)
                into(destDir)
                rename { filename -> filename.lowercase() }
            }
            logger.info("Copied language files from assets/$modId/lang to data/$modId/lang")
        } else {
            logger.error("Source language directory not found: ${srcDir.asFile.absolutePath}")
        }
    }
}
