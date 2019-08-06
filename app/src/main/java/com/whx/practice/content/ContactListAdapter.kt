package com.whx.practice.content

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.whx.practice.R

/**
 * Created by whx on 2018/1/2.
 */
class ContactListAdapter : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    private val data = mutableListOf<ListData>()

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.contact_list_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.setData(data[position])
    }

    fun setData(data: MutableList<ListData>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val portrait = view.findViewById<ImageView>(R.id.portrait)
        val name = view.findViewById<TextView>(R.id.name)
        val number = view.findViewById<TextView>(R.id.number)

        fun setData(item: ListData) {
            Picasso.with(itemView.context)
                    .load(Uri.parse(item.uri))
                    .into(portrait)
            name.text = item.name
            number.text = item.number
        }
    }

    class ListData(val uri: String, val name: String, val number: String)
}