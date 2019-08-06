package com.whx.testplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by whx on 2018/2/1.
 */

public class SolveFile {
    private List<File> fileList = new ArrayList<>();

    public void solve(String filepath) {
        getFileList(filepath);

        print();
    }

    private void getFileList(String filePath) {

        if (filePath == null || filePath.isEmpty()) {
            System.out.println("file path is empty");
            return;
        }

        File dir = new File(filePath);

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();

            for (File file : files) {
                String fileName = file.getName();

                if (file.isDirectory()) {
                    getFileList(file.getAbsolutePath());
                }else if (fileName.endsWith(".java")) {
                    fileList.add(file);
                }
            }
        }
    }

    private void print() {
        for (File file : fileList) {
            System.out.println(file.getAbsolutePath());
        }
    }

    private void hhh() {

    }
}
