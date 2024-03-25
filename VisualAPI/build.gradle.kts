plugins {
    id("java")
}

group = "net.kissenpvp"
version = "1.4.2"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
