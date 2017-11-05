package com.lahzouz.sparow.rxsearch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_departments.view.*

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var departments: List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = departments.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.textView.text = departments[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_departments, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}