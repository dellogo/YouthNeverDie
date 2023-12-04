package com.example.youthneverdie.LicenseAPI

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.youthneverdie.ListValue.ApiResponse
import com.example.youthneverdie.ListValue.Item
import com.example.youthneverdie.databinding.FragmentCListBinding
import com.example.youthneverdie.databinding.FragmentEListBinding
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.Response
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CApiService {
    @GET("getCList?")
    suspend fun getCList(
        @Query("_type") type: String = "xml",
        @Query("serviceKey") serviceKey: String
    ): Response<ApiResponse>
}
class CListFragment : Fragment() {
    private lateinit var binding: FragmentCListBinding

    private val serviceKey =
        "dOG7sXkHheFFUg9Xk0bk5OjyRnb4ta51bsno7uzH/su8Hql4H6i1UyHuprT6dwHXDxqeN/gwIZA0FYFR2ld1/w=="


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCListBinding.inflate(inflater, container, false)
        binding.CTextV.text = ""

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(CApiService::class.java)

        binding.Cbtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("API Call", "API 호출 시작")

                    val response = withContext(Dispatchers.IO) {
                        apiService.getCList(serviceKey = serviceKey)
                    }
                    Log.d("API Response", "Raw Response: ${response.errorBody()?.toString()}")
                    if (response.isSuccessful) {
                        Log.d("API Response", "Response: ${response.body()}")
                        val apiResponse = response.body()
                        val items = apiResponse?.body?.items?.itemList ?: emptyList()
                        if (items.isNotEmpty()) {
                            val result = buildResultString(items)
                            binding.CTextV.text = result
                        } else {
                            binding.CTextV.text = "데이터 없음"
                        }
                    } else {
                        binding.CTextV.text = "API 호출 실패"
                    }

                    Log.d("API Call", "API 호출 완료")
                } catch (e: Exception) {
                    Log.d("API Call", "API 호출 실패: $e")
                }
            }
        }
        return binding.root
    }

    private fun buildResultString(items: List<Item>): String {
        val stringBuilder = StringBuilder()

        for (item in items) {
            stringBuilder.append("기능사\n")
            stringBuilder.append("회차 : ${item.description}\n")
            stringBuilder.append("필기시험원서접수 : ${item.docregstartdt} ~ ${item.docregenddt}\n")
            stringBuilder.append("필기시험시작일자 : ${item.docexamdt}\n")
            stringBuilder.append("필기시험 합격(예정자) 발표일자 : ${item.docpassdt}\n")
            stringBuilder.append("응시자격 서류제출기간 : ${item.docsubmitstartdt} ~ ${item.docsubmitentdt}\n")
            stringBuilder.append("실기시험원서접수 : ${item.pracregstartdt} ~ ${item.pracregenddt}\n")
            stringBuilder.append("실기시험기간 : ${item.pracexamstartdt} ~ ${item.pracexamenddt}\n")
            stringBuilder.append("실기시험 합격자 발표일 : ${item.pracpassdt}\n")

        }

        return stringBuilder.toString()
    }
}