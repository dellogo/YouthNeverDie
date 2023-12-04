package com.example.youthneverdie.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.R
import com.example.youthneverdie.databinding.ActivityBoardEditBinding
import com.example.youthneverdie.databinding.ActivityLicenseFdetailBinding
import com.example.youthneverdie.utils.FirebaseAuth
import com.example.youthneverdie.utils.FirebaseReF
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BoardEditActivity : AppCompatActivity() {
    private lateinit var key: String
    private lateinit var binding: ActivityBoardEditBinding
    private val TAG = BoardEditActivity::class.java.simpleName
    private lateinit var writerUid : String
    private lateinit var writePosition :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)

        key = intent.getStringExtra("key").toString()
        getBoardData(key)

        binding.editBtn.setOnClickListener {
            editBoardData(key)
        }
    }
    private fun editBoardData(key: String){
        FirebaseReF.boardRef
            .child(key)
            .setValue(
                BoardModel(
                    binding.postTitle.text.toString(),
                    binding.postDetail.text.toString(),
                    writerUid,
                    FirebaseAuth.getTime(),
                    writePosition)
            )
        Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show()
        finish()
    }
    private fun getBoardData(key: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                Log.d(TAG, dataModel!!.title)

                binding.postTitle.setText(dataModel?.title)
                binding.postDetail.setText(dataModel?.content)
                writerUid = dataModel!!.uid
                writePosition = dataModel!!.position
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseReF.boardRef.child(key).addValueEventListener(postListener)
    }
}