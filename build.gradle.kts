/*
 * Copyright (C) 2024 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `maven-publish`
    java
}

group = "net.kissenpvp"
version = "1.6.6-SNAPSHOT"

configurations {
    create("includeLib")
}

repositories {
    mavenCentral()
    maven("https://repo.kissenpvp.net/snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    publishing {
        repositories {
            maven("https://repo.kissenpvp.net/repository/maven-snapshots/") {
                name = "kissenpvp"
                credentials(PasswordCredentials::class)
            }
        }
    }
}


subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.kissenpvp.net/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("net.kissenpvp.pulvinar:pulvinar-api:1.21-R0.1-SNAPSHOT")

        testCompileOnly("org.jetbrains:annotations:24.0.0")
    }

    publishing {
        publications.create<MavenPublication>(project.name) {
            artifact("${project.projectDir}/build/libs/${project.name}-${rootProject.version}.jar")
        }
    }
}

dependencies {
    implementation(project(":VisualAPI"))
    implementation(project(":VisualSource"))
}

tasks.jar {
    from(subprojects.map { it.sourceSets.getByName("main").output })
}
