import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime

plugins {
    id("eclipse")
    id("idea")
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    kotlin("jvm") version "1.9.20"
}

val mod_group_id: String by project
val mod_version: String by project
val mod_id: String by project
val minecraft_version: String by project
val forge_version: String by project
val mapping_channel: String by project
val mapping_version: String by project
val kotlinForForgeVersion = "4.11.0" // Compatible with Forge 1.20.1
val tconVersion = "1.20.1-3.8.1.179" // Pinned TCon version for 1.20.1

group = mod_group_id
version = mod_version

base {
    archivesName.set(mod_id)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings(mapping_channel, mapping_version)

    copyIdeResources.set(true)

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")

            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("client") {
            property("forge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            property("forge.enabledGameTestNamespaces", mod_id)
            args("--nogui")
        }

        create("gameTestServer") {
            property("forge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            workingDirectory(project.file("run-data"))
            args(
                "--mod", mod_id,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

repositories {
    mavenCentral()
    maven {
        name = "KotlinForForge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "TConstruct"
        url = uri("https://maven.blamejared.com/")
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:$minecraft_version-$forge_version")

    // KotlinForForge
    implementation("thedarkcolour:kotlinforforge:$kotlinForForgeVersion")

    // Mantle - TCon dependency from libs
    implementation(fg.deobf("slimeknights.mantle:Mantle:1.20.1-1.11.95"))

    // Tinkers' Construct from libs
    implementation(fg.deobf("slimeknights.tconstruct:TConstruct:1.20.1-3.11.0.148"))
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to project.property("minecraft_version_range"),
        "forge_version" to forge_version,
        "forge_version_range" to project.property("forge_version_range"),
        "loader_version_range" to project.property("loader_version_range"),
        "mod_id" to mod_id,
        "mod_name" to project.property("mod_name"),
        "mod_license" to project.property("mod_license"),
        "mod_version" to mod_version,
        "mod_authors" to project.property("mod_authors"),
        "mod_description" to project.property("mod_description")
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties + mapOf("project" to project))
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to mod_id,
                "Specification-Vendor" to project.property("mod_authors"),
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to project.property("mod_authors"),
                "Implementation-Timestamp" to LocalDateTime.now().toString()
            )
        )
    }

    finalizedBy("reobfJar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
