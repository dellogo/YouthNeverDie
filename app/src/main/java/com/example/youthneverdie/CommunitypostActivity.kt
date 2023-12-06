package com.example.youthneverdie

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.databinding.ActivityCommunitypostBinding
import com.example.youthneverdie.utils.FirebaseAuth
import com.example.youthneverdie.utils.FirebaseReF

class CommunitypostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCommunitypostBinding
    private val TAG = CommunitypostActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunitypostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "글 작성"

        var sData = resources.getStringArray(R.array.select_posting)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sData)
        binding.spinner.adapter = adapter

        binding.spinner.setSelection(0)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {

                    }
                    1 -> {

                    }
                    2 -> {

                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.finishBtn.setOnClickListener{
            val title = binding.postTitle.text.toString()
            val content = binding.postDetail.text.toString()
            val uid = FirebaseAuth.getUid()
            val time = FirebaseAuth.getTime()
            val position = binding.spinner.selectedItem.toString()

            Log.d(TAG, title)
            Log.d(TAG, content)

            FirebaseReF.boardRef
                .push()
                .setValue(BoardModel(title, content, uid, time, position))

            Toast.makeText(this, "게시글 업로드 완료", Toast.LENGTH_SHORT).show()
            finish()

        }
    }
}