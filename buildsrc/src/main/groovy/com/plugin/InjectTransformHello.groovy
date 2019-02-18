package com.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.plugin.util.InjectClickUtil
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class InjectTransformHello extends Transform {

    private Project mProject;

    InjectTransformHello(Project project) {
        this.mProject = project;
    }

    //transform的名称
    //transformClassesWithInjectTransformHelloForDebug 运行时的名字
    //transformClassesWith + getName() + For + Debug或Release
    @Override
    String getName() {
        return "InjectTransformHello"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //    指Transform要操作内容的范围，官方文档Scope有7种类型：
//
//    EXTERNAL_LIBRARIES        只有外部库
//    PROJECT                       只有项目内容
//    PROJECT_LOCAL_DEPS            只有项目的本地依赖(本地jar)
//    PROVIDED_ONLY                 只提供本地或远程依赖项
//    SUB_PROJECTS              只有子项目。
//    SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
//    TESTED_CODE                   由当前变量(包括依赖项)测试的代码
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println("my transfrom  **********")

        String packageName=mProject.printConfig.packageName

        println("my transfrom  **********:"+packageName)

        inputs.each { TransformInput input ->
            //遍历文件夹
            input.directoryInputs.each { DirectoryInput directoryInput ->
                println("file:" + directoryInput.file.absolutePath)
                InjectClickUtil.injectDirHello(directoryInput.file.getAbsolutePath(), packageName.replace('.','/'), mProject)

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                //将 input 的目录复制到 output 指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
                println("file.dest****:" + dest.absolutePath)
            }
            input.jarInputs.each { JarInput jarInput ->

                log("jarInput name = " + jarInput.name + ", path = " + jarInput.file.absolutePath)

//                InjectClickUtil.injectDir(jarInput.file.getAbsolutePath(), packageName.replace('.','/'), mProject)

                //重命名输出文件（同目录 copyFile 会冲突）
                def jarName = jarInput.name
                def md5Name = jarInput.file.hashCode()
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }

        }

    }
    void log(String log) {
        mProject.logger.error(log)
    }
}
