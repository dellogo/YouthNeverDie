package com.example.youthneverdie.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.youthneverdie.R
import com.example.youthneverdie.comment.CommentModel

class CommentListAdapter(val commentList: MutableList<CommentModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return commentList.size
    }

    override fun getItem(position: Int): Any {
        return commentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.comment_list_item, parent, false)
        }
        val title = convertView?.findViewById<TextView>(R.id.titleArea)
        val time = convertView?.findViewById<TextView>(R.id.timeArea)
        title!!.text = commentList[position].commentTitle
        time!!.text = commentList[position].commentCreateTime


        return convertView!!
    }
}