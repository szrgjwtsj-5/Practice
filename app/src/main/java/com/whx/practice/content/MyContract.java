package com.whx.practice.content;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by whx on 2017/12/5.
 */

public class MyContract {
    protected static final String CONTENT_AUTHORITY = "com.whx.practice.provider";
    protected static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    protected static final String PATH_TEST = "main";

    protected static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_TEST).build();

    public static Uri buildUri (long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
