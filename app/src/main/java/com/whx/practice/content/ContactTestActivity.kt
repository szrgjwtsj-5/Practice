package com.whx.practice.content

import android.Manifest
import android.app.LoaderManager
import android.content.*
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.provider.ContactsContract.Intents
import android.widget.Toast
import com.whx.practice.AbstractActivity
import com.whx.practice.R
import com.whx.practice.view.ToastCompat
import kotlinx.android.synthetic.main.activity_contact.*

/**
 * Created by whx on 2018/1/2.
 */
class ContactTestActivity : AbstractActivity() {

    private lateinit var adapter: ContactListAdapter
    private val dataList = mutableListOf<ContactListAdapter.ListData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_contact)

        query_contact.setOnClickListener {
            checkPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), true, true) {
                loadContact()
            }
        }

        adapter = ContactListAdapter()
        contact_list.adapter = adapter
        contact_list.layoutManager = LinearLayoutManager(this)

        save_contact.setOnClickListener {
            checkPermissions(arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS), true, false) {
//                addContactDirect()
                addContactByIntent()
            }
        }

    }

    private fun addContactDirect() {

        val name = contact_name.text.toString()
        val tel = contact_tel.text.toString()
        val email = contact_email.text.toString()
        val address = contact_address.text.toString()

        // 创建一个空的ContentValues
        val values = ContentValues()

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
        val rawContactId = ContentUris.parseId(rawContactUri)
        values.clear()

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
        // 向联系人URI添加联系人名字
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        values.clear()

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        // 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, tel)
        // 电话类型
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        // 向联系人电话号码URI添加电话号码
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        values.clear()

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        // 联系人的Email地址
        values.put(ContactsContract.CommonDataKinds.Email.DATA, email)
        // 电子邮件的类型
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
        // 向联系人Email URI添加Email数据
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        values.clear()

        // 向联系人添加地址
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.DATA, address)
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

        ToastCompat.makeTextCompat(this, "创建联系人成功", Toast.LENGTH_SHORT).show()
    }

    private fun addContactByIntent() {

        save_contact.setOnClickListener {
            val name = contact_name.text.toString()
            val tel = contact_tel.text.toString()
            val email = contact_email.text.toString()
            val address = contact_address.text.toString()

            val intent = Intent(Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(Intents.Insert.NAME, name)
                putExtra(Intents.Insert.EMAIL, email)
                putExtra(Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                putExtra(Intents.Insert.PHONE, tel)
                putExtra(Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                putExtra(Intents.Insert.POSTAL, address)
                putExtra(Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
            }

            startActivity(intent)
        }
    }

    private fun loadContact() {
        val callback = object : LoaderManager.LoaderCallbacks<Cursor> {
            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
                return CursorLoader(this@ContactTestActivity, ContactsContract.Contacts.CONTENT_URI, projection, null, null, null)
            }

            override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
                data?.let {
                    while (it.moveToNext()) {
                        val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                        val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val uri = it.getString(it.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                        val phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
                        val number = StringBuilder()
                        while (phoneCursor.moveToNext()) {
                            number.append(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)))
                            number.append("\n")
                        }
                        phoneCursor.close()

                        dataList.add(ContactListAdapter.ListData(uri, name, number.toString()))
                    }

                    data.close()
                    adapter.setData(dataList)
                }
            }

            override fun onLoaderReset(loader: Loader<Cursor>?) {

            }
        }

        loaderManager.initLoader(233, null, callback)
    }
}