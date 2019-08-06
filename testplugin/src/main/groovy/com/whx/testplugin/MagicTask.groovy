package com.whx.testplugin

import com.whx.testplugin.SolveFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MagicTask extends DefaultTask{

    String filePath

    @TaskAction
    def taskMethod() {
        filePath = project.testPlugin.filePath

        def file = new File(filePath)

        file.eachFileRecurse {

        }
        new SolveFile().solve(filePath)
    }
}