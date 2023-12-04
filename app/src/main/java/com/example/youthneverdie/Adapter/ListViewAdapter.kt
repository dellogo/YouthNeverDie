package com.example.youthneverdie.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.youthneverdie.R
import com.example.youthneverdie.model.FeedbackModel
import com.example.youthneverdie.model.TermModel
import com.example.youthneverdie.model.TodayModel
import org.w3c.dom.Text

class TodayListViewAdapter(val List: MutableList<TodayModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return List.size
    }

    override fun getItem(position: Int): Any {
        return List[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.today_listview, parent, false)
        }

        val content = view?.findViewById<TextView>(R.id.target_content)
        content!!.text = List[position].content

        return view!!
    }
}

class TermListViewAdapter(val List: MutableList<TermModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return List.size
    }

    override fun getItem(position: Int): Any {
        return List[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.term_listview, parent, false)
        }

        val content = view?.findViewById<TextView>(R.id.term_target_content)
        val startDate = view?.findViewById<TextView>(R.id.startdate_textview)
        val finalDate = view?.findViewById<TextView>(R.id.finaldate_textview)
        content!!.text = List[position].content
        startDate!!.text = List[position].startDate
        finalDate!!.text = List[position].finalDate

        return view!!
    }
}

class FeedbackListViewAdapter(val List: MutableList<FeedbackModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return List.size
    }

    override fun getItem(position: Int): Any {
        return List[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.feedback_listview, parent, false)
        }

        val content = view?.findViewById<TextView>(R.id.target_content)
        content!!.text = List[position].content

        return view!!
    }
}