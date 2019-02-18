package com.plugin.util


import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class InjectClickUtil {

    public static final String JAVA_ASSIST_TOAST = "android.widget.Toast"

    private final static ClassPool pool = ClassPool.getDefault()

    public static void injectDir(String path, String packageName, Project project) {
        pool.appendClassPath(path)
        String androidJarPath = project.android.bootClasspath[0].toString()
        log("androidJarPath: " + androidJarPath, project)
        pool.appendClassPath(androidJarPath)
        importClass(pool)
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return
        }
        dir.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            log("filePath : " + filePath, project)
            if (filePath.endsWith(".class") && !filePath.contains('R$')
                    && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                int index = filePath.indexOf(packageName);
                log("filePath my : " + filePath + "  index:" + index, project)
                boolean isMyPackage = index != -1;
                if (!isMyPackage) {
                    return
                }
                String className = getClassName(index, filePath)
                log("className my : " + className, project)
                CtClass c = pool.getCtClass(className)
                log("CtClass my : " + c.getSimpleName(), project)
                for (CtMethod method : c.getDeclaredMethods()) {
                    log("CtMethod my : " + method.getName(), project)
                    //找到 onClick(View) 方法
                    if (checkOnClickMethod(method)) {
                        log("checkOnClickMethod my : " + method.getName(), project)
                        //解冻
                        if (c.isFrozen())
                            c.defrost()
                        injectMethod(c, method)
                        c.writeFile(path)

                    }
                }
                c.detach()//释放
            }
        }

    }

    private static boolean checkOnClickMethod(CtMethod method) {
        return method.getName().endsWith("onClick") && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].getName().equals("android.view.View");
    }

    private static void injectMethod(CtClass c, CtMethod method) {
        String insetBeforeStr = """ android.widget.Toast.makeText((\$1).getContext(),"我是被插了的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
                                            """
        method.insertBefore(insetBeforeStr)
        method.insertBefore("System.out.println(\"hello javassist\");")
        method.insertAfter("System.out.println((\$1));")
    }

    private static void log(String msg, Project project) {
        project.logger.log(LogLevel.ERROR, msg)
    }

    private static void importClass(ClassPool pool) {
        pool.importPackage(JAVA_ASSIST_TOAST)
    }

    static String getClassName(int index, String filePath) {
        int end = filePath.length() - 6 // .class = 6
        return filePath.substring(index, end).replace('\\', '.').replace('/', '.')
    }


    public static void injectDirHello(String path, String packageName, Project project) {
        pool.appendClassPath(path)
        String androidJarPath = project.android.bootClasspath[0].toString()
        log("androidJarPath: " + androidJarPath, project)
        pool.appendClassPath(androidJarPath)
        importClass(pool)
        File dir = new File(path)
        if (!dir.isDirectory()) {
            return
        }
        dir.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            log("filePath : " + filePath, project)
            if (filePath.endsWith(".class") && !filePath.contains('R$')
                    && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                int index = filePath.indexOf(packageName);
                log("filePath my : " + filePath + "  index:" + index, project)
                boolean isMyPackage = index != -1;
                if (!isMyPackage) {
                    return
                }
                String className = getClassName(index, filePath)
                log("className my : " + className, project)
                CtClass c = pool.getCtClass(className)
                log("CtClass my : " + c.getSimpleName(), project)
                for (CtMethod method : c.getDeclaredMethods()) {
                    log("CtMethod my : " + method.getName(), project)
                    //解冻
                    if (c.isFrozen())
                        c.defrost()
                    method.insertAfter("System.out.println(\"print hello javassist\");")
                    c.writeFile(path)
                    log("CtMethod my   end: " + method.getName(), project)
                }
                c.detach()//释放
            }
        }

    }
}
