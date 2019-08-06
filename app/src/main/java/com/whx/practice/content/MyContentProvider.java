package com.whx.practice.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by whx on 2017/12/1.
 */

public class MyContentProvider extends ContentProvider{

    private static final int TEST = 100;

    private static final String DB_NAME = "my_database";
    private static final String TABLE_NAME = "main";
    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME +
            " (" +
            "_id INTEGER PRIMARY KEY, " +
            "word TEXT, " +
            "frequency INTEGER, " +
            "locale TEXT )";

    private MyDatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MyContract.CONTENT_AUTHORITY, MyContract.PATH_TEST, TEST);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new MyDatabaseHelper(getContext());
        db = mDbHelper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri = Uri.EMPTY;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case TEST:
                _id = db.insert(MyContract.PATH_TEST, null, values);
                if (_id > 0) {
                    returnUri = MyContract.buildUri(_id);
                } else {
                    throw new SQLException("failed to insert");
                }
                break;
        }
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) {
            case TEST:
                db.update(MyContract.PATH_TEST, values, selection, selectionArgs);
        }
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case TEST:
                cursor = db.query(MyContract.PATH_TEST, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int res = 0;
        switch (sUriMatcher.match(uri)) {
            case TEST:
                res = db.delete(TABLE_NAME, selection, selectionArgs);
        }
        return res;
    }

    static class MyDatabaseHelper extends SQLiteOpenHelper {

        public MyDatabaseHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
