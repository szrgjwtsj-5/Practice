package com.whx.testplugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class TestPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("testPlugin", TestPluginExtension)

//        project.task('hello') {
//            doLast {
//                println("Hello ${project.testPlugin.name}")
//
//                new TestJava().func()
//            }
//        }

        def task = project.tasks.create("ListAllFiles", MagicTask)

        registerTransform(project)
    }

    private static void registerTransform(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new MyTransform(project))
    }
}
