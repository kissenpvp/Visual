plugins {
    id("java")
}

group = "net.kissenpvp"
version = "1.4.3"

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation(project(":VisualAPI"))
}

tasks.test {
    useJUnitPlatform()
}
