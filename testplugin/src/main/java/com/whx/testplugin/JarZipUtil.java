package com.whx.testplugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * jar 包解压、压缩工具
 * Created by whx on 2018/2/2.
 */

public class JarZipUtil {

    /**
     * 将该jar包解压到指定目录
     * @param jarPath jar包的绝对路径
     * @param destDirPath jar包解压后的保存路径，确保存在
     * @return 返回该jar包中包含的所有class的完整类名类名集合，其中一条数据如：com.aitski.hotpatch.Xxxx.class
     */
    public static List<String> unzipJar(String jarPath, String destDirPath) {
        List<String> list = new ArrayList<>();

        if (!jarPath.endsWith(".jar")) {
            System.out.println("----- is not a jar file");
            return list;
        }
        try {
            JarFile jarFile = new JarFile(jarPath);

            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    String className = entryName.replace('\\', '.').replace('/', '.');
                    list.add(className);
                }

                String outFileName = destDirPath + "/" + entryName;
                File outFile = new File(outFileName);
                outFile.getParentFile().mkdirs();

                InputStream inputStream = jarFile.getInputStream(jarEntry);

                FileOutputStream outputStream = new FileOutputStream(outFile);

                byte[] b = new byte[1024];

                while (inputStream.read(b) != -1) {
                    outputStream.write(b);
                }

                outputStream.close();
                inputStream.close();
            }

            jarFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    /**
     * jar压缩功能测试.
     * @param dir
     *            所要压缩的目录名（包含绝对路径）
     * @param jarName
     *            压缩后的文件名
     */
    public static void zipJar(String dir, String jarName) {
        File folderObject = new File(dir);
        if (folderObject.exists()) {
            try {
                List<File> fileList = getSubFiles(new File(dir));

                // 压缩文件名
                JarOutputStream zos = new JarOutputStream(new FileOutputStream(jarName));
                JarEntry ze;

                byte[] buf = new byte[1024];
                int readLen;

                for (File f : fileList) {
                    ze = new JarEntry(getAbsFileName(dir, f));

                    ze.setSize(f.length());
                    ze.setTime(f.lastModified());

                    zos.putNextEntry(ze);
                    InputStream is = new BufferedInputStream(new FileInputStream(f));

                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        zos.write(buf, 0, readLen);
                    }
                    is.close();

                }
                zos.close();
            } catch (IOException e) {
                //
            }
        } else {
            System.out.println("文件夹不存在!");
        }
    }

    /**
     * 取得指定目录以及其各级子目录下的所有文件的列表，注意，返回的列表中不含目录.
     * @param baseDir
     *            File 指定的目录
     * @return 包含java.io.File的List
     */
    public static List<File> getSubFiles(File baseDir) {
        List<File> fileList = new ArrayList<>();

        for (File aTmp : baseDir.listFiles()) {
            if (aTmp.isFile()) {
                fileList.add(aTmp);
            }
            if (aTmp.isDirectory()) {
                fileList.addAll(getSubFiles(aTmp));
            }
        }
        return fileList;
    }

    /**
     * 给定根目录及文件的实际路径，返回带有相对路径的文件名，用于zip文件中的路径.
     * 如将绝对路径，baseDir\dir1\dir2\file.txt改成 dir1/dir2/file.txt
     * @param baseDir
     *            java.lang.String 根目录
     * @param realFileName
     *            java.io.File 实际的文件名
     * @return 相对文件名
     */
    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        StringBuilder ret = new StringBuilder(real.getName());

        while (true) {
            real = real.getParentFile();
            if (real == null)
                break;
            if (real.equals(base))
                break;
            else {
                ret.insert(0, real.getName() + "/");
            }
        }
        return ret.toString();
    }

    public static void main(String[] args) {
        //unzipJar("/Users/whx/Desktop/testplugin.jar", "/Users/whx/Desktop/testplugin");


        try {
            long start1 = System.currentTimeMillis();
            zipJar("/Users/whx/Desktop/testplugin", "/Users/whx/Desktop/testplugin.jar");
            long end1 = System.currentTimeMillis();

            System.out.println("time = " + (end1 - start1));
        } catch (Exception e) {
            //
        }
    }

}
