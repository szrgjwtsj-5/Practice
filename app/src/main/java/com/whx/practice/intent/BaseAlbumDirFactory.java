package com.whx.practice.intent;

import android.os.Environment;

import java.io.File;

/**
 * Created by whx on 2017/9/5.
 */

public final class BaseAlbumDirFactory extends AlbumStorageDirFactory{

    private static final String CAMERA_DIR = "/dcim/";
    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
    }
}
