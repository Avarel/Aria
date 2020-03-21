plugins {
    kotlin("jvm") version "1.3.70"
    java
    idea
    application
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "xyz.avarel.aria"
version = "1.0"
application.mainClassName = "xyz.avarel.aria.MainKt"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.5")

    implementation("net.dv8tion", "JDA", "4.1.1_117") {
        exclude(module = "opus-java")
    }
    implementation("com.sedmelluq", "jda-nas", "1.1.0")
    implementation("com.sedmelluq", "lavaplayer", "1.3.34")

    implementation("redis.clients", "jedis", "3.2.0")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    testImplementation("junit", "junit", "4.12")
}

tasks {
    wrapper {
        gradleVersion = "6.2.2"
        distributionType = Wrapper.DistributionType.BIN
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    jar {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }
}