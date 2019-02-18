package com.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class InjectPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "apply ******* "
        project.extensions.create('printConfig', PrintConfig)

        project.afterEvaluate {
            print("printConfig:" + project.printConfig.packageName)
        }
        registerTransform(project)
    }

    private void registerTransform(Project project) {
        BaseExtension android = project.extensions.getByType(BaseExtension)
        InjectTransformClick transform = new InjectTransformClick(project)
        android.registerTransform(transform)
        InjectTransformHello transformHello = new InjectTransformHello(project)
        android.registerTransform(transformHello)
    }

}
