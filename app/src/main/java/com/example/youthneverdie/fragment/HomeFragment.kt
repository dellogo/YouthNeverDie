package com.example.youthneverdie.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youthneverdie.Adapter.BoardListViewAdapter
import com.example.youthneverdie.Adapter.SitePagerAdapter
/*import com.example.youthneverdie.Adapter.SitePagerAdapter*/
import com.example.youthneverdie.CommunitypostActivity
import com.example.youthneverdie.LicensedetailActivity
import com.example.youthneverdie.ListValue.BoardModel
import com.example.youthneverdie.board.LicenseFDetailActivity
import com.example.youthneverdie.board.WorkFDetailActivity
import com.example.youthneverdie.databinding.FragmentHomeBinding
import com.example.youthneverdie.utils.FirebaseReF
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private val binding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater)}
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()
    private val TAG = HomeFragment::class.java.simpleName
    private lateinit var boardRVAdapter: BoardListViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        boardRVAdapter = BoardListViewAdapter(boardDataList)

        getFBWorkBoardData()
        getFBLicenseBoardData()

        return (binding.root)
    }
    private fun getFBLicenseBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                boardKeyList.clear()
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    if(item?.position == "자격증 게시판") {
                        boardDataList.add(item!!)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }

                val sortedDataWithKeys = boardDataList.zip(boardKeyList).sortedByDescending { it.first.starCount }
                boardDataList.clear()
                boardKeyList.clear()
                for (pair in sortedDataWithKeys) {
                    boardDataList.add(pair.first)
                    boardKeyList.add(pair.second)
                }

                if (boardDataList.isNotEmpty()) {
                    val topPost2 = boardDataList[0]
                    val topPostKey2 = boardKeyList[0]
                    bestPost2(topPost2, topPostKey2)
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
    private fun getFBWorkBoardData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                boardKeyList.clear()
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    if(item?.position == "취업 게시판") {
                        boardDataList.add(item!!)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }

                val sortedDataWithKeys = boardDataList.zip(boardKeyList).sortedByDescending { it.first.starCount }
                boardDataList.clear()
                boardKeyList.clear()
                for (pair in sortedDataWithKeys) {
                    boardDataList.add(pair.first)
                    boardKeyList.add(pair.second)
                }

                if (boardDataList.isNotEmpty()) {
                    val topPost = boardDataList[0]
                    val topPostKey = boardKeyList[0]
                    bestPost(topPost, topPostKey)
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
    private fun bestPost(topPost: BoardModel, topPostKey: String){
        binding.titleArea.text = topPost.title
        binding.contentArea.text = topPost.content
        binding.timeArea.text = topPost.time
        binding.boardHeartCount.text = topPost.starCount.toString()

        binding.workBestWriting.setOnClickListener {
            val intent = Intent(context, WorkFDetailActivity::class.java)

            intent.putExtra("key", topPostKey)
            startActivity(intent)
        }

    }
    private fun bestPost2(topPost2: BoardModel, topPostKey2: String){
        binding.titleArea2.text = topPost2.title
        binding.contentArea2.text = topPost2.content
        binding.timeArea2.text = topPost2.time
        binding.boardHeartCount2.text = topPost2.starCount.toString()

        binding.licenseBestWriting.setOnClickListener {
            val intent = Intent(context, LicenseFDetailActivity::class.java)

            intent.putExtra("key", topPostKey2)
            startActivity(intent)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.license.setOnClickListener {
            val intent = Intent(getActivity(), LicensedetailActivity::class.java)
            startActivity(intent)
        }

        arguments?.let {
        }
        binding.site.adapter = SitePagerAdapter(this)

        var isCurrentItemZero = true

        binding.siteLeftBtn.setOnClickListener{
            if (isCurrentItemZero) {
                binding.site.setCurrentItem(0, true)
            } else {
                binding.site.setCurrentItem(1, true)
            }

            isCurrentItemZero = !isCurrentItemZero
        }
        binding.siteRightBtn.setOnClickListener{
            if (isCurrentItemZero) {
                binding.site.setCurrentItem(1, true)
            } else {
                binding.site.setCurrentItem(0, true)
            }

            isCurrentItemZero = !isCurrentItemZero
        }


        val stringArray =  arrayOf("삶이 있는 한 희망은 있다 -키케로", "산다는것 그것은 치열한 전투이다. -로망로랑", "언제나 현재에 집중할수 있다면 행복할것이다. -파울로 코엘료", "신은 용기있는자를 결코 버리지 않는다 -켄러", "피할수 없으면 즐겨라 – 로버트 엘리엇", "오랫동안 꿈을 그리는 사람은 마침내 그 꿈을 닮아 간다, -앙드레 말로")
        val randomIndex = (0 until stringArray.size).random()
        val randomString = stringArray[randomIndex]
        binding.fighting.text = randomString.toString()
    }

}