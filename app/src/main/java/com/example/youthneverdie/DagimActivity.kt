package com.example.youthneverdie.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.youthneverdie.Adapter.FeedbackListViewAdapter
import com.example.youthneverdie.Adapter.TermListViewAdapter
import com.example.youthneverdie.Adapter.TodayListViewAdapter
import com.example.youthneverdie.FButil.FBAuth
import com.example.youthneverdie.FButil.FBRef
import com.example.youthneverdie.R
import com.example.youthneverdie.auth
import com.example.youthneverdie.databinding.ActivityDajimBinding
import com.example.youthneverdie.model.FeedbackModel
import com.example.youthneverdie.model.TermModel
import com.example.youthneverdie.model.TodayModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.*

class DagimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDajimBinding

    private val TodayDataList = mutableListOf<TodayModel>()
    private val TodayKeyList = mutableListOf<String>()
    private val TermDataList = mutableListOf<TermModel>()
    private val TermKeyList = mutableListOf<String>()
    private val FeedbackDataList = mutableListOf<FeedbackModel>()
    private val FeedbackKeyList = mutableListOf<String>()

    private val TAG = OpenbookFragment::class.java.simpleName

    private lateinit var LVAdapter : TodayListViewAdapter
    private lateinit var lvadapter : TermListViewAdapter
    private lateinit var Lvadapter : FeedbackListViewAdapter

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDajimBinding.inflate(layoutInflater)
        auth = com.google.firebase.auth.FirebaseAuth.getInstance()

        val intent = intent
        val selectedDate = intent.getStringExtra("selectedDate")
        binding.selectDate.text = selectedDate

        getTodayData(selectedDate)
        getTermData(selectedDate)
        getFeedbackData(selectedDate)

        setContentView(binding.root)

        LVAdapter = TodayListViewAdapter(TodayDataList)
        lvadapter = TermListViewAdapter(TermDataList)
        Lvadapter = FeedbackListViewAdapter(FeedbackDataList)
        binding.todayTargetText.adapter = LVAdapter
        binding.termTargetText.adapter = lvadapter
        binding.feedbackTextList.adapter = Lvadapter

        binding.todayWrite.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.today_write, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = dialogBuilder.create()

            val editTextDialog = dialogView.findViewById<EditText>(R.id.today_target_edit)
            val todayfinishbtn = dialogView.findViewById<Button>(R.id.today_target_finish_btn)

            todayfinishbtn.setOnClickListener {
                val content = editTextDialog.text.toString()
                val uid = FBAuth.getUid()
                val time = FBAuth.getTime()
                val date = selectedDate.toString()

                FBRef.todayRef
                    .push()
                    .setValue(TodayModel(content, uid, time, date))
                Toast.makeText(this, "오늘의 목표 입력 완료", Toast.LENGTH_LONG).show()

                dialog.dismiss()
            }
            dialog.show()
        }

        binding.termWrite.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.term_write, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            val dialog = mBuilder.create()

            val editTextDialog = mDialogView.findViewById<EditText>(R.id.term_target_edit)
            val termfinishbtn = mDialogView.findViewById<Button>(R.id.term_target_finish_btn)
            val startDate = mDialogView.findViewById<TextView>(R.id.start_date_view)
            val finalDate = mDialogView.findViewById<TextView>(R.id.final_date_view)

            termfinishbtn.setOnClickListener {
                val content = editTextDialog.text.toString()
                val uid = FBAuth.getUid()
                val time = FBAuth.getTime()
                val startDate = startDate.text.toString()
                val finalDate = finalDate.text.toString()

                FBRef.termRef
                    .push()
                    .setValue(TermModel(content, uid, time, startDate, finalDate))
                Toast.makeText(this, "진행중인 목표 입력 완료", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }

            startDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val data1 = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    startDate.text = String.format("%04d-%02d-%02d", year, month + 1, day)
                }
                DatePickerDialog(
                    this,
                    data1,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            finalDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val data2 = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    finalDate.text = String.format("%04d-%02d-%02d", year, month + 1, day)
                }
                DatePickerDialog(
                    this,
                    data2,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            dialog.show()
        }

        binding.feedbackWrite.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.feedback_write, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = dialogBuilder.create()

            val editTextDialog = dialogView.findViewById<EditText>(R.id.feedback_edit)
            val feedbackfinishbtn = dialogView.findViewById<Button>(R.id.feedback_finish_btn)

            feedbackfinishbtn.setOnClickListener {
                val content = editTextDialog.text.toString()
                val uid = FBAuth.getUid()
                val time = FBAuth.getTime()
                val date = selectedDate.toString()

                FBRef.feedbackRef
                    .push()
                    .setValue(FeedbackModel(content, uid, time, date))
                Toast.makeText(this, "오늘의 나에게 입력 완료", Toast.LENGTH_LONG).show()

                dialog.dismiss()
            }
            dialog.show()
        }

        binding.todayTargetText.setOnItemLongClickListener { parent, view, position, id ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.today_edit, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = dialogBuilder.create()

            val today_target_edit_edit = dialogView.findViewById<EditText>(R.id.today_target_edit_edit)
            val deletebtn = dialogView.findViewById<ImageButton>(R.id.today_target_delete_btn)
            val todayeditfinishbtn = dialogView.findViewById<Button>(R.id.today_target_edit_finish_btn)

            Log.i(TAG, TodayKeyList[position])

            // 작성 완료 버튼 눌렀을 경우
            todayeditfinishbtn.setOnClickListener {
                val editedText = today_target_edit_edit.text.toString()
                val selectedKey1 = TodayKeyList[position] // 선택한 항목의 키 가져오기

                FBRef.todayRef.child(selectedKey1).child("content").setValue(editedText)
                    .addOnSuccessListener {
                        Toast.makeText(this, "수정 완료", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "수정 실패: $exception", Toast.LENGTH_LONG).show()
                    }
                TodayKeyList.clear()
                TodayDataList.clear()
                LVAdapter.notifyDataSetChanged()
            }
            // 삭제 아이콘을 눌렀을 경우
            deletebtn.setOnClickListener {
                val selectedKey1 = TodayKeyList[position] // 수정된 부분
                FBRef.todayRef.child(selectedKey1).removeValue() // 선택한 키로 해당 항목 삭제
                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show()
                TodayKeyList.clear()
                TodayDataList.clear()
                LVAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.show()
            true
        }

        binding.termTargetText.setOnItemLongClickListener { parent, view, position, id ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.term_edit, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = dialogBuilder.create()

            val startDate = dialogView.findViewById<TextView>(R.id.edit_start_date_view)
            val finalDate = dialogView.findViewById<TextView>(R.id.edit_final_date_view)
            val term_target_edit_edit = dialogView.findViewById<EditText>(R.id.term_target_edit_edit)
            val deletebtn = dialogView.findViewById<ImageButton>(R.id.term_target_delete_btn)
            val termeditfinishbtn = dialogView.findViewById<Button>(R.id.term_target_edit_finish_btn)

            startDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val data1 = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    startDate.text = String.format("%04d-%02d-%02d", year, month + 1, day)
                }
                DatePickerDialog(
                    this,
                    data1,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            finalDate.setOnClickListener {
                val cal = Calendar.getInstance()
                val data2 = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    finalDate.text = String.format("%04d-%02d-%02d", year, month + 1, day)
                }
                DatePickerDialog(
                    this,
                    data2,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            // 작성 완료 버튼 눌렀을 경우
            termeditfinishbtn.setOnClickListener {
                val editedText = term_target_edit_edit.text.toString()
                val startDate = startDate.text.toString()
                val finalDate = finalDate.text.toString()

                val selectedKey2 = TermKeyList[position] // 선택한 항목의 키 가져오기

                FBRef.termRef.child(selectedKey2).child("content").setValue(editedText)
                FBRef.termRef.child(selectedKey2).child("startDate").setValue(startDate)
                FBRef.termRef.child(selectedKey2).child("finalDate").setValue(finalDate)
                    .addOnSuccessListener {
                        Toast.makeText(this, "수정 완료", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "수정 실패: $exception", Toast.LENGTH_LONG).show()
                    }
                TermKeyList.clear()
                TermDataList.clear()
                lvadapter.notifyDataSetChanged()
            }
            // 삭제 아이콘을 눌렀을 경우
            deletebtn.setOnClickListener {
                val selectedKey2 = TermKeyList[position] // 수정된 부분
                FBRef.termRef.child(selectedKey2).removeValue() // 선택한 키로 해당 항목 삭제
                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show()
                TermKeyList.clear()
                TermDataList.clear()
                lvadapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.show()
            true
        }

        binding.feedbackTextList.setOnItemLongClickListener { parent, view, position, id ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.feedback_edit, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
            val dialog = dialogBuilder.create()

            val feedback_edit_edit = dialogView.findViewById<EditText>(R.id.feedback_edit_edit)
            val deletebtn = dialogView.findViewById<ImageButton>(R.id.feedback_delete_btn)
            val feedbackeditfinishbtn = dialogView.findViewById<Button>(R.id.feedback_edit_finish_btn)

            // 작성 완료 버튼 눌렀을 경우
            feedbackeditfinishbtn.setOnClickListener {
                val editedText = feedback_edit_edit.text.toString()
                val selectedKey3 = FeedbackKeyList[position] // 선택한 항목의 키 가져오기

                FBRef.feedbackRef.child(selectedKey3).child("content").setValue(editedText)
                    .addOnSuccessListener {
                        Toast.makeText(this, "수정 완료", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "수정 실패: $exception", Toast.LENGTH_LONG).show()
                    }
                FeedbackKeyList.clear()
                FeedbackDataList.clear()
                Lvadapter.notifyDataSetChanged()
            }
            // 삭제 아이콘을 눌렀을 경우
            deletebtn.setOnClickListener {
                val selectedKey3 = FeedbackKeyList[position] // 수정된 부분
                FBRef.feedbackRef.child(selectedKey3).removeValue() // 선택한 키로 해당 항목 삭제
                Toast.makeText(this, "삭제 완료", Toast.LENGTH_LONG).show()
                FeedbackKeyList.clear()
                FeedbackDataList.clear()
                Lvadapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            dialog.show()
            true
        }
    }



    private fun getTodayData(selectedDate: String?) {
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TodayDataList.clear()
                TodayKeyList.clear()
                for (dataModel in snapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(TodayModel::class.java)
                    val user = auth.currentUser?.uid
                    if (item?.date == selectedDate && user == item?.uid ) {
                        TodayDataList.add(item!!)
                        TodayKeyList.add(dataModel.key.toString())
                    }
                }
                LVAdapter.notifyDataSetChanged()
                Log.d(TAG, TodayDataList.toString())
                Log.i(TAG, TodayKeyList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost::onCancelled", error.toException())
            }
        }
        FBRef.todayRef.addValueEventListener(postListener)
    }

    private fun getTermData(selectedDate: String?) {
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TermDataList.clear()
                TermKeyList.clear()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                val selectedDateObj = dateFormat.parse(selectedDate!!)
                for (dataModel in snapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(TermModel::class.java)
                    val user = auth.currentUser?.uid
                    val startDate = item?.startDate
                    val finishDate = item?.finalDate

                    // 날짜 형식을 파싱하여 날짜 객체로 변환합니다.
                    val startDateObj = dateFormat.parse(startDate!!)
                    val finishDateObj = dateFormat.parse(finishDate!!)

                    // 선택한 날짜가 시작일과 종료일 사이에 있는지 확인합니다.
                    if ((selectedDateObj!!.compareTo(startDateObj) >= 0 && selectedDateObj.compareTo(finishDateObj) <= 0) && user == item.uid) {
                        TermDataList.add(item)
                        TermKeyList.add(dataModel.key.toString())
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


    private fun getFeedbackData(selectedDate: String?) {
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                FeedbackDataList.clear()
                FeedbackKeyList.clear()
                for (dataModel in snapshot.children){
                    Log.d(TAG, dataModel.toString())

                    val item = dataModel.getValue(FeedbackModel::class.java)
                    val user = auth.currentUser?.uid
                    if (item?.date == selectedDate && user == item?.uid ) {
                        FeedbackDataList.add(item!!)
                        FeedbackKeyList.add(dataModel.key.toString())
                    }
                }

                Lvadapter.notifyDataSetChanged()
                Log.d(TAG, FeedbackDataList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost::onCancelled", error.toException())
            }
        }
        FBRef.feedbackRef.addValueEventListener(postListener)
    }
}