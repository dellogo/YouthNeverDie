package com.example.youthneverdie.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.youthneverdie.Adapter.CommentListAdapter
import com.example.youthneverdie.FButil.FBRef
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.R
import com.example.youthneverdie.comment.CommentModel
import com.example.youthneverdie.databinding.ActivityLicenseFdetailBinding
import com.example.youthneverdie.utils.FirebaseAuth
import com.example.youthneverdie.utils.FirebaseAuth.Companion.getUid
import com.example.youthneverdie.utils.FirebaseReF
import com.google.firebase.database.*

class LicenseFDetailActivity : AppCompatActivity() {
    private val TAG = LicenseFDetailActivity::class.java.simpleName
    private lateinit var binding : ActivityLicenseFdetailBinding
    private lateinit var key: String
    private val commentDataList = mutableListOf<CommentModel>()
    private  lateinit var commentAdapter : CommentListAdapter
    private lateinit var alertDialog: AlertDialog
    private var isFavorite: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_fdetail)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license_fdetail)

        binding.boardSettingIcon.setOnClickListener{
            showDialog()
        }

        key = intent.getStringExtra("key").toString()
        getBoardData(key)

        binding.commentBtn.setOnClickListener{
            insertComment(key)
        }
        binding.heartIcon.setOnClickListener {
            val postReF = FirebaseReF.boardRef.child(key)
            onHeartClicked(postReF)
        }
        getCommentData(key)
        commentAdapter = CommentListAdapter(commentDataList)
        binding.commentList.adapter = commentAdapter
    }
    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_delete_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        alertDialog = mBuilder.show()

        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(this, "수정화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key",key)
            startActivity(intent)
        }
        alertDialog.findViewById<Button>(R.id.deleteBtn)?.setOnClickListener{
            FirebaseReF.boardRef.child(key).removeValue()
            Toast.makeText(this, "게시글 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            finish()
        }
    }
    fun getCommentData(key: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentDataList.clear()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseReF.commentRef.child(key).addValueEventListener(postListener)
    }
    fun insertComment(key: String){
        val commentText = binding.commentArea.text.toString().trim()

        if (commentText.isNotEmpty()) {
            FirebaseReF.commentRef.child(key).push()
                .setValue(
                    CommentModel(
                        binding.commentArea.text.toString(),
                        FirebaseAuth.getTime()
                    )
                )
            Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
        binding.commentArea.setText("")
    }
    private fun getBoardData(key: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    binding.titleArea.text = dataModel!!.title
                    binding.contentArea.text = dataModel!!.content
                    binding.timeArea.text = dataModel!!.time

                    val myUid = getUid()
                    val writerUid = dataModel.uid

                    if (myUid == writerUid){
                        binding.boardSettingIcon.isVisible = true
                    }else{
                        Log.d(TAG,"작성자 아님")
                    }
                    if (dataModel.stars.containsKey(myUid)) {
                        binding.heartIcon.setImageResource(R.drawable.ic_heart_black)
                        isFavorite = true
                    } else {
                        binding.heartIcon.setImageResource(R.drawable.ic_heart_white)
                        isFavorite = false
                    }
                    binding.heartCount.text = dataModel.starCount.toString()

                }catch (e : Exception){
                    Log.d(TAG, "삭제완료")
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseReF.boardRef.child(key).addValueEventListener(postListener)
    }
    private fun onHeartClicked(postReF: DatabaseReference){
        postReF.runTransaction(object : Transaction.Handler{
            override fun doTransaction(mutableData: MutableData): Transaction.Result{
                val p = mutableData.getValue(BoardModel::class.java)
                    ?: return Transaction.success(mutableData)

                val uid = FirebaseAuth.getUid()

                if (p.stars.containsKey(uid)){
                    p.starCount = p.starCount -1
                    p.stars.remove(uid)
                    isFavorite = false
                }else{
                    p.starCount = p.starCount + 1
                    p.stars[uid] = true
                    isFavorite = true
                }
                mutableData.value = p
                return Transaction.success(mutableData)
            }
            override fun onComplete(databaseError: DatabaseError?, commutted: Boolean, currentData: DataSnapshot?){
                if (isFavorite){
                    binding.heartIcon.setImageResource(R.drawable.ic_heart_black)
                }else{
                    binding.heartIcon.setImageResource(R.drawable.ic_heart_white)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if(::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }
}