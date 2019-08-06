package com.whx.practice.loader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.whx.practice.R;

import java.lang.ref.WeakReference;

/**
 * Created by whx on 2017/9/19.
 */

public class LoaderTestFragment extends Fragment{

    public static LoaderTestFragment getInstance() {
        LoaderTestFragment fragment = new LoaderTestFragment();

        return fragment;
    }

    static final String[] CONTACTS_SUMMARY_PROJECTION = {
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.CONTACT_STATUS,
            Contacts.CONTACT_PRESENCE,
            Contacts.PHOTO_ID,
            Contacts.LOOKUP_KEY
    };
    SimpleCursorAdapter adapter;
    String mCurFilter;
    private ListView mList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(233, null, new LoaderCallback(this));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list, container, false);
        mList = view.findViewById(R.id.list);

        adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null,
                new String[]{Contacts.DISPLAY_NAME, Contacts.CONTACT_STATUS}, new int[]{android.R.id.text1, android.R.id.text2}, 0);

        mList.setAdapter(adapter);

        return view;
    }

    private static class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {



        WeakReference<LoaderTestFragment> reference;

        LoaderCallback(LoaderTestFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            if (reference.get() != null) {
                Uri baseUri;

                if (reference.get().mCurFilter != null) {
                    baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(reference.get().mCurFilter));
                } else {
                    baseUri = Contacts.CONTENT_URI;
                }

                String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.HAS_PHONE_NUMBER
                        + "=1) AND (" + Contacts.DISPLAY_NAME + " != '' ))";

                return new CursorLoader(reference.get().getActivity(), baseUri, CONTACTS_SUMMARY_PROJECTION, select,
                        null, Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
            }

            return null;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (reference.get() != null)
                reference.get().adapter.swapCursor(null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (reference.get() != null)
                reference.get().adapter.swapCursor(data);
        }
    }
}
