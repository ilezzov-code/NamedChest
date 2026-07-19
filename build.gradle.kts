import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.named

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.5.1"
    id("com.github.gmazzo.buildconfig") version "6.0.10"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.okaeri.cloud/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.1.0-beta.4")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:6.1.0-beta.4")

    implementation("org.bstats:bstats-bukkit:3.2.1");
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

fun property(name: String): String = providers.gradleProperty(name).get()

val pluginName = property("pluginName")
val pluginPackage = property("pluginPackage")
val pluginVersion = property("pluginVersion")
val pluginDescription = property("pluginDescription")
val pluginMainClass = property("pluginMainClass")
val pluginApiVersion = property("pluginApiVersion")
val pluginAuthor = property("pluginAuthor")
val pluginWebsite = property("pluginWebsite")
val pluginMainCommand = property("pluginMainCommand")
val pluginUpdateUrl = property("pluginUpdateUrl")
val pluginIssueUrl = property("pluginIssueUrl")
val pluginBasePermission = property("pluginBasePermission")
val bStatsEnable = property("bStatsEnable")
val bStatsId = property("bStatsId")
val bStatsLanguageChartId = property("bStatsLanguageChartId")

buildConfig {
    className("BuildConfig")
    packageName(pluginPackage)

    buildConfigField("String", "NAME", "\"$pluginName\"")
    buildConfigField("String", "MAIN_CLASS", "\"$pluginMainClass\"")
    buildConfigField("String", "API_VERSION", "\"$pluginApiVersion\"")
    buildConfigField("String", "AUTHOR", "\"$pluginAuthor\"")
    buildConfigField("String", "WEBSITE", "\"$pluginWebsite\"")
    buildConfigField("String", "DESCRIPTION", "\"$pluginDescription\"")
    buildConfigField("String", "MAIN_COMMAND", "\"$pluginMainCommand\"")
    buildConfigField("String", "UPDATE_URL", "\"$pluginUpdateUrl\"")
    buildConfigField("String", "ISSUE_URL", "\"$pluginIssueUrl\"")
    buildConfigField("String", "PLUGIN_VERSION", "\"$pluginVersion\"")
    buildConfigField("String", "BASE_PERMISSION", "\"$pluginBasePermission\"")

    buildConfigField("boolean", "BSTATS_ENABLE", bStatsEnable)
    buildConfigField("int", "BSTATS_ID", bStatsId)
    buildConfigField("String", "BSTATS_LANGUAGE_CHART_ID", "\"$bStatsLanguageChartId\"")
}

val copyJar by tasks.registering(Copy::class) {
    from(tasks.named<ShadowJar>("shadowJar").get().archiveFile)
    into(file("D:/ILeZzoV Server/plugins"))
    outputs.upToDateWhen { false }
}

tasks.named<ShadowJar>("shadowJar") {
    finalizedBy(copyJar)
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    relocate("org.bstats", "${pluginPackage}.libs.stats")

    archiveClassifier.set("")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    processResources {
        val propsPluginYml = mapOf(
            "pluginName" to pluginName,
            "pluginVersion" to pluginVersion,
            "pluginDescription" to pluginDescription,
            "pluginMainClass" to pluginMainClass,
            "pluginApiVersion" to pluginApiVersion,
            "pluginAuthor" to pluginAuthor,
            "pluginWebsite" to pluginWebsite,
            "pluginMainCommand" to pluginMainCommand
        )

        val propsMessageYml = mapOf(
            "command" to pluginMainCommand
        )

        filteringCharset = "UTF-8"

        filesMatching("plugin.yml") {
            expand(propsPluginYml)
        }

        filesMatching("messages/") {
            expand(propsMessageYml)
        }
    }
}

