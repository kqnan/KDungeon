import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.54"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"

}

taboolib {
    install("common")
    install("common-5")
    install("module-chat")
    install("module-configuration")
    install("module-database")
    install("module-ui")
    install("platform-bukkit")
    install("expansion-command-helper")
    install("module-nms-util")
    install("module-nms")
    classifier = null
    version = "6.0.10-55"
    description {
        dependencies{
            name("ProtocolLib")
            name("MythicMobs")
            name("FastAsyncWorldEdit")

        }
    }
}

repositories {
    maven("https://jitpack.io")
    maven("https://maven.aliyun.com/repository/public")
    maven( "https://maven.aliyun.com/repository/central" )
    maven ( "https://maven.aliyun.com/repository/google")
    maven ( "https://maven.aliyun.com/repository/public" )
    maven(  "https://maven.aliyun.com/repository/gradle-plugin" )
    maven ("https://repo.codemc.org/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("com.belerweb:pinyin4j:2.5.0")
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    compileOnly(kotlin("stdlib"))
    taboo("de.tr7zw:item-nbt-api:2.10.0")
    taboo("com.belerweb:pinyin4j:2.5.0")
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
//        maven {
//            url = uri("https://repo.tabooproject.org/repository/releases")
//            credentials {
//                username = project.findProperty("taboolibUsername").toString()
//                password = project.findProperty("taboolibPassword").toString()
//            }
//            authentication {
//                create<BasicAuthentication>("basic")
//            }
//        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            groupId = project.group.toString()
        }
    }
}