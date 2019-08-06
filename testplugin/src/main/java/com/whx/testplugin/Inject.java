package com.whx.testplugin;

import org.gradle.util.JarUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 注入类
 *
 * 注入代码分为两种情况，一种是目录，需要遍历里面的class进行注入 <br>
 * 另外一种是jar包，需要先解压jar包，注入代码之后重新打包成jar
 *
 * Created by whx on 2018/2/2.
 */
public class Inject {

    private static ClassPool pool = ClassPool.getDefault();

    /**
     * 添加classPath到ClassPool
     */
    public static void appendClassPath(String libPath) {
        try {
            pool.appendClassPath(libPath);
        } catch (NotFoundException e) {
            //
        }
    }

    public static void injectDir(String path) {
        try {
            pool.appendClassPath(path);

            File dir = new File(path);

            if (dir.isDirectory()) {
                List<File> files = JarZipUtil.getSubFiles(dir);
                for (File file : files) {
                    String filePath = file.getAbsolutePath();

                    if (filePath.endsWith(".class") && !filePath.contains("R$")
                            && !filePath.contains("R.class")
                            && !filePath.contains("BuildConfig.class")
                            // 项目Application 的名字，可以通过解析manifest 文件获得
                            && !filePath.contains("MyApplication.class")) {

                        // 应用包名，也能从manifest 文件获取
                        int index = filePath.indexOf("com/whx/practice");
                        if (index != -1 && filePath.contains("MainActivity")) {
                            int end = filePath.length() - 6;        // .class = 6
                            String className = filePath.substring(index, end)
                                    .replace('\\', '.').replace('/','.');

                            injectOnclick(className, path);
                        }
                        if (index != -1 && filePath.contains("PreferenceTestActivity")) {
                            int end = filePath.length() - 6;        // .class = 6
                            String className = filePath.substring(index, end)
                                    .replace('\\', '.').replace('/','.');

                            injectKotlin(className, path);
                        }
                    }
                }
            }
        } catch (NotFoundException e) {
            //
        }
    }

    public static void injectJar(String path) {
        if (!path.endsWith(".jar")) {
            return;
        }
        File jarFile = new File(path);

        // jar 包解压后的保存路径
        String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace(".jar", "");

        // 解压jar 包，返回jar 包中所有Class 的完整类名的集合（带.class 后缀）
        List<String> classNameList = JarZipUtil.unzipJar(path, jarZipDir);

        try {
            pool.appendClassPath(jarZipDir);
        } catch (NotFoundException e) {
            //
        }
        for (String className : classNameList) {
            if (className.endsWith(".class") && !className.contains("R$")
                    && !className.contains("R.class") && !className.contains("BuildConfig.class")) {

                className = className.substring(0, className.length() - 6);
                injectClass(className, jarZipDir);
            }
        }

    }

    // 对Class 进行注入
    private static void injectClass(String className, String path) {
        try {
            CtClass ctClass = pool.getCtClass(className);

            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            CtConstructor[] constructors = ctClass.getConstructors();

            if (constructors == null || constructors.length == 0) {
                insertNewConstructor(ctClass);
            } else {
                constructors[0].insertBeforeBody("System.out.println(\"hahaha, your code has been injected\");");
            }

            ctClass.writeFile(path);
            ctClass.detach();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            //
        }
    }

    // 插入新的构造方法
    private static void insertNewConstructor(CtClass ctClass) {
        try {
            CtConstructor constructor = new CtConstructor(new CtClass[]{pool.get("java.lang.String")}, ctClass);

            ctClass.addConstructor(constructor);
        } catch (NotFoundException | CannotCompileException e) {
            //
        }
    }

    private static void injectOnclick(String className, String path) {
        try {
            CtClass ctClass = pool.getCtClass(className);

            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            CtMethod ctMethod = ctClass.getDeclaredMethod("onClick", new CtClass[]{pool.get("android.view.View")});


            ctMethod.insertBefore("android.widget.Toast.makeText($1.getContext(), \"inject onclick\", " +
                    "android.widget.Toast.LENGTH_SHORT).show();");

            ctClass.writeFile(path);
            ctClass.detach();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void injectKotlin(String className, String path) {
        try {
            CtClass ctClass = pool.getCtClass(className);

            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }

            CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate", new CtClass[]{pool.get("android.os.Bundle")});

            ctMethod.insertBefore("android.widget.Toast.makeText(this, \"test kotlin\", " +
                    "android.widget.Toast.LENGTH_SHORT).show();");

            System.out.println(ctClass.getRefClasses());

            ctClass.writeFile(path);
            ctClass.detach();
        } catch (NotFoundException | CannotCompileException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
