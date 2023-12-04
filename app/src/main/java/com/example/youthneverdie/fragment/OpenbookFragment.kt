package com.example.youthneverdie.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youthneverdie.Adapter.TermListViewAdapter
import com.example.youthneverdie.Adapter.TodayListViewAdapter
import com.example.youthneverdie.FButil.FBRef
import com.example.youthneverdie.R
import com.example.youthneverdie.auth

import com.example.youthneverdie.databinding.FragmentOpenbookBinding
import com.example.youthneverdie.model.TermModel
import com.example.youthneverdie.model.TodayModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class OpenbookFragment : Fragment() {
    private val binding: FragmentOpenbookBinding by lazy { FragmentOpenbookBinding.inflate(layoutInflater) }

    private val TodayDataList = mutableListOf<TodayModel>()
    private val TermDataList = mutableListOf<TermModel>()

    private val TAG = OpenbookFragment::class.java.simpleName

    private lateinit var LVAdapter : TodayListViewAdapter
    private lateinit var lvadapter : TermListViewAdapter

    private var selectedDate: String = ""

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        LVAdapter = TodayListViewAdapter(TodayDataList)
        lvadapter = TermListViewAdapter(TermDataList)
        binding.openbookTodayList.adapter = LVAdapter
        binding.openbookTermList.adapter = lvadapter

        auth = com.google.firebase.auth.FirebaseAuth.getInstance()

        // 기본 선택 날짜로 오늘 날짜 설정하기
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        val defaultSelectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        selectedDate = defaultSelectedDate
        binding.selectDateOpenbook.text = defaultSelectedDate
        getTodayData(defaultSelectedDate)
        getTermData(defaultSelectedDate)

        binding.calendar.setOnDateChangeListener { _, yy, MM, dd ->
            val day: String = String.format("%04d-%02d-%02d", yy, MM + 1, dd)
            Log.d(TAG, day)
            selectedDate = day
            binding.selectDateOpenbook.text = day
            getTodayData(day)
            getTermData(day)
        }


        binding.goDagimBtn.setOnClickListener {
            val intent = Intent(context, DagimActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }

        binding.openbookTodayList.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, DagimActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }

        binding.openbookTermList.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, DagimActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getTodayData(day: String) {
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TodayDataList.clear()
                for (dataModel in snapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(TodayModel::class.java)
                    val user = auth.currentUser?.uid
                    if (item?.date == day && user == item.uid ) {
                        TodayDataList.add(item)
                    }
                }

                LVAdapter.notifyDataSetChanged()
                Log.d(TAG, TodayDataList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost::onCancelled", error.toException())
            }
        }
        FBRef.todayRef.addValueEventListener(postListener)
    }

    private fun getTermData(day: String) {
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TermDataList.clear()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                val selectedDateObj = dateFormat.parse(day)
                for (dataModel in snapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(TermModel::class.java)
                    val user = auth.currentUser?.uid
                    val startDate = item?.startDate
                    val finishDate = item?.finalDate

                    val startDateObj = dateFormat.parse(startDate!!)
                    val finishDateObj = dateFormat.parse(finishDate!!)

                    if ((selectedDateObj!!.compareTo(startDateObj) >= 0 && selectedDateObj.compareTo(finishDateObj) <= 0) && user == item.uid) {
                        TermDataList.add(item)
                    }
                }

                lvadapter.notifyDataSetChanged()
                Log.d(TAG, TermDataList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost::onCancelled", error.toException())
            }
        }
        FBRef.termRef.addValueEventListener(postListener)
    }

}
