package com.whx.practice.intent;

import android.os.Environment;

import java.io.File;

/**
 * Created by whx on 2017/9/5.
 */

public class FroyoAlbumDirFactory extends AlbumStorageDirFactory {
    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
    }
}
