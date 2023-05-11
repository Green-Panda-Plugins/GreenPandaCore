plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "dev.michaud.greenpanda"
version = "0.1.2"
description = "A Minecraft paper plugin that provides some useful stuff to make other plugins. Most plugins in the GreenPanda plugin pack (see github.com/Green-Panda-Plugins) use this as a dependency."

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven {
        name = "viaversion-repo"
        url = uri("https://repo.viaversion.com")
    }
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "4.7.0")
    compileOnly("com.viaversion:viaversion-api:4.6.0")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {

            artifactId = "core"

            from(components["java"])

            pom {
                name.set("GreenPandaCore")
                description.set("A Minecraft paper plugin that provides some useful stuff to make other plugins. Most plugins in the GreenPanda plugin pack (see github.com/Green-Panda-Plugins) use this as a dependency.")
                url.set("https://github.com/Green-Panda-Plugins")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("elim")
                        name.set("Eli Michaud")
                        email.set("greenpanda@michaud.dev")
                        organization.set("com.github.Green-Panda-Plugins")
                        organizationUrl.set("https://github.com/Green-Panda-Plugins")
                    }
                }
                scm {
                    url.set("https://github.com/Green-Panda-Plugins/GreenPandaCore")
                    connection.set("scm:git:git://github.com/Green-Panda-Plugins/GreenPandaCore.git")
                    developerConnection.set("scm:git:git@github.com:Green-Panda-Plugins/GreenPandaCore.git")
                }
            }
        }
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}