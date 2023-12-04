package com.example.youthneverdie.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.R
import com.example.youthneverdie.utils.FirebaseAuth

class BoardListViewAdapter(val boardList : MutableList<BoardModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(position: Int): Any {
        return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.board_list_item,parent,false)

        val title = convertView?.findViewById<TextView>(R.id.titleArea)
        val content = convertView?.findViewById<TextView>(R.id.contentArea)
        val time = convertView?.findViewById<TextView>(R.id.timeArea)
        val heartCount = convertView?.findViewById<TextView>(R.id.boardHeartCount)

        title!!.text = boardList[position].title
        content!!.text = boardList[position].content
        time!!.text = boardList[position].time
        heartCount!!.text = boardList[position].starCount.toString()


        return convertView!!
    }
}