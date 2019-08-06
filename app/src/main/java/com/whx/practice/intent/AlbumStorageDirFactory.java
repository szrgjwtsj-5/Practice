package com.whx.practice.intent;

import java.io.File;

/**
 * Created by whx on 2017/9/5.
 */

public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
