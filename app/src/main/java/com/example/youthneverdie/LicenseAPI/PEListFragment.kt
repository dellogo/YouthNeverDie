package com.example.youthneverdie.LicenseAPI

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.youthneverdie.ListValue.ApiResponse
import com.example.youthneverdie.ListValue.Item
import com.example.youthneverdie.databinding.FragmentPEList2Binding
import com.google.gson.GsonBuilder
/*import com.fasterxml.jackson.dataformat.xml.XmlMapper*/
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.*
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jaxb.JaxbConverterFactory
/*import retrofit2.converter.jackson.JacksonConverterFactory*/
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

private var text: String = ""

interface ApiService {
    @GET("getPEList?")
    suspend fun getPEList(
        @Query("_type") type: String = "xml",
        @Query("serviceKey") serviceKey: String
    ): Response<ApiResponse>
}

class PEListFragment : Fragment() {
    private lateinit var binding: FragmentPEList2Binding
    private val serviceKey =
        ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPEList2Binding.inflate(inflater, container, false)
        binding.PETextV.text = ""

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(httpClient.build())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        binding.PEbtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("API Call", "API 호출 시작")

                    val response = withContext(Dispatchers.IO) {
                        apiService.getPEList(serviceKey = serviceKey)
                    }
                    Log.d("API Response", "Raw Response: ${response.errorBody()?.toString()}")
                    if (response.isSuccessful) {
                        Log.d("API Response", "Response: ${response.body()}")
                        val apiResponse = response.body()
                        val items = apiResponse?.body?.items?.itemList ?: emptyList()
                        if (items.isNotEmpty()) {
                            val result = buildResultString(items)
                            binding.PETextV.text = result
                        } else {
                            binding.PETextV.text = "데이터 없음"
                        }
                    } else {
                        binding.PETextV.text = "API 호출 실패"
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
            stringBuilder.append("기술사\n")
            stringBuilder.append("회차 : ${item.description}\n")
            stringBuilder.append("필기시험원서접수 : ${item.docregstartdt} ~ ${item.docregenddt}\n")
            stringBuilder.append("필기시험시작일자 : ${item.docexamdt}\n")
            stringBuilder.append("필기시험 합격(예정자) 발표일자 : ${item.docpassdt}\n")
            stringBuilder.append("응시자격 서류제출기간 : ${item.docsubmitstartdt} ~ ${item.docsubmitentdt}\n")
            stringBuilder.append("면접시험원서접수 : ${item.pracregstartdt} ~ ${item.pracregenddt}\n")
            stringBuilder.append("면접시험기간 : ${item.pracexamstartdt} ~ ${item.pracexamenddt}\n")
            stringBuilder.append("면접시험 합격자 발표일 : ${item.pracpassdt}\n")

        }

        return stringBuilder.toString()
    }
}