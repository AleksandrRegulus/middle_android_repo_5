package com.yandex.practicum.middle_homework_5.gradle_plugins

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class FindUntranslatedStringsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("untranslatedStrings", FindUntranslatedStringsTask::class.java)
    }
}

abstract class FindUntranslatedStringsTask : DefaultTask() {
    @TaskAction
    fun findUntranslatedStrings() {
        val resDir = File(project.projectDir, "src/main/res")
        val strings = File(resDir, "values/strings.xml")
        val stringIdentities = getStringIdentitiesFromFile(strings)
        val missingStrings = mutableMapOf<String, List<String>>()

        resDir.walkTopDown().filter { it.name.contains("values-") }
            .forEach { valuesFolder ->
                val foreingStrings = File(valuesFolder, "/strings.xml")
                val foreingStringIdentities = getStringIdentitiesFromFile(foreingStrings)
                val language = valuesFolder.name
                val missingIdentities =
                    stringIdentities.filter { !foreingStringIdentities.contains(it) }
                missingStrings[language] = missingIdentities
            }

        if (missingStrings.isNotEmpty()) {
            val stringBuilderErrorText =
                StringBuilder("Missing translations").append(System.lineSeparator())
            missingStrings.forEach { missing ->
                stringBuilderErrorText
                    .append("=== ${missing.key} ===")
                    .append(System.lineSeparator())
                    .append(missing.value.joinToString(separator = System.lineSeparator()))
                    .append(System.lineSeparator())
            }
            throw GradleException(stringBuilderErrorText.toString())
        }

    }

    private fun getStringIdentitiesFromFile(file: File): List<String> {
        val stringsFromXml = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(file)
            .getElementsByTagName("string")

        return stringsFromXml.let { nodeList ->
            (0 until nodeList.length).map { i ->
                val node = nodeList.item(i)
                val name = node.attributes?.getNamedItem("name")?.nodeValue ?: ""
                name
            }
        }
    }
}
