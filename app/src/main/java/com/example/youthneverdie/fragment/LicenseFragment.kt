package com.example.youthneverdie.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.youthneverdie.Adapter.BoardListViewAdapter
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.R
import com.example.youthneverdie.board.LicenseFDetailActivity
import com.example.youthneverdie.board.WorkFDetailActivity
import com.example.youthneverdie.databinding.ActivityCommunitypostBinding
import com.example.youthneverdie.databinding.FragmentLicenseBinding
import com.example.youthneverdie.utils.FirebaseReF
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LicenseFragment : Fragment() {
    private lateinit var binding : FragmentLicenseBinding
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private val TAG = LicenseFragment::class.java.simpleName
    private lateinit var boardRVAdapter: BoardListViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLicenseBinding.inflate(inflater, container, false)

        boardRVAdapter = BoardListViewAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        binding.boardListView.setOnItemClickListener{ parent, view, position, id ->
            val intent = Intent(context, LicenseFDetailActivity::class.java)

            intent.putExtra("key", boardKeyList[position])

            startActivity(intent)
        }

        getFBBoardData()

        return binding.root
    }
    private fun getFBBoardData(){
        val postListener = object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    if(item?.position == "자격증 게시판") {
                        boardDataList.add(item!!)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged()
                Log.d(TAG,boardDataList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseReF.boardRef.addValueEventListener(postListener)

    }
}