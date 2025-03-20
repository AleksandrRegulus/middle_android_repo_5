plugins{
    `kotlin-dsl`
}

dependencies{
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("find-untranslated-strings-plugin") {
            id = libs.plugins.untranslated.get().pluginId
            implementationClass = "com.yandex.practicum.middle_homework_5.gradle_plugins.FindUntranslatedStringsPlugin"
        }
    }
}