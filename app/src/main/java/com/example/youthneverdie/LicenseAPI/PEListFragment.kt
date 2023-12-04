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
        /*@Query("jmCd") jmcCd: String = "",
        @Query("_wadl") wadl: String = "?",*/
        @Query("_type") type: String = "xml",
        @Query("serviceKey") serviceKey: String
    ): Response<ApiResponse>/*<ResponseBody>*/
}

class PEListFragment : Fragment() {
    private lateinit var binding: FragmentPEList2Binding
/*    open val baseUrl : String
        get() = "http://openapi.q-net.or.kr/"*/
    private val serviceKey =
        "dOG7sXkHheFFUg9Xk0bk5OjyRnb4ta51bsno7uzH/su8Hql4H6i1UyHuprT6dwHXDxqeN/gwIZA0FYFR2ld1/w=="

    /*private fun parseResponse(xmlString: String): List<Item> {
        val items = mutableListOf<Item>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlString))

            var eventType = parser.eventType
            var currentItem: Item? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name
                        if (tagName == "item") {
                            currentItem = Item()
                        } else if (currentItem != null) {
                            when (tagName) {
                                "description" -> currentItem.description = parser.nextText()
                                "docregstartdt" -> currentItem.docregstartdt = parser.nextText()
                                "docregenddt" -> currentItem.docregenddt = parser.nextText()
                                // 필요한 정보가 추가로 있다면 여기에 추가
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name
                        if (tagName == "item" && currentItem != null) {
                            items.add(currentItem)
                            currentItem = null
                        }
                    }
                }

                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return items
    }*/

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
        /*val xmlMapper = XmlMapper()
        val factory = JacksonConverterFactory.create(xmlMapper)*/
        val retrofit = Retrofit.Builder()
            .baseUrl("http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/")
            /*.addConverterFactory(factory)*/
            .addConverterFactory(SimpleXmlConverterFactory.create())
            /*.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))*/
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
                        /*val xmlString = response.body()?.string()*/
                        val apiResponse = response.body()
                        val items = apiResponse?.body?.items?.itemList ?: emptyList()
                        /*if (!xmlString.isNullOrEmpty()) {
                            val items = parseResponse(xmlString)*/
                        if (items.isNotEmpty()) {
                            val result = buildResultString(items)
                            binding.PETextV.text = result
                        } else {
                            binding.PETextV.text = "데이터 없음"
                        }
                    } else {
                        binding.PETextV.text = "API 호출 실패"
                    }
                    /*} else {
                        val errorCode = response.code()
                        Log.d("API Call", "API 호출 실패. HTTP 상태 코드: $errorCode")
                    }*/

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
/*
class PEListFragment : Fragment() {
    private lateinit var binding: FragmentPEList2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPEList2Binding.inflate(inflater, container, false)

        binding.PETextV.text = ""

        val serviceKey = "dOG7sXkHheFFUg9Xk0bk5OjyRnb4ta51bsno7uzH%2Fsu8Hql4H6i1UyHuprT6dwHXDxqeN%2FgwIZA0FYFR2ld1%2Fw%3D%3D"
        */
/*val url = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC?_wadl&_type=xml&key=$key"*//*

        val baseUrl = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/"

        val client = OkHttpClient.Builder()
            .addInterceptor{chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val headers = response.headers()

                val resultCode = headers["resultCode"]
                if (resultCode != null) {
                    val resultMsg = headers["resultMsg"]

                    val updatedResponse = response.newBuilder()
                        .body(response.body())
                        .header("resultCode", resultCode)
                        .header("resultMsg", resultMsg)
                        .build()
                    return@addInterceptor updatedResponse
                }else{
                    Log.d("API Call", "API 호출 실패. resultCode가 null임.")
                    return@addInterceptor response
                }

            }
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃 설정
            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃 설정
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        binding.PEbtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.d("API Call", "API 호출 시작")
                    val response = apiService.getInformation("_wadl", "xml", serviceKey)
                    val headers = response.headers()
                    val resultCode = headers["resultCode"]
                    if(resultCode != null && resultCode == "00") {
                        val body = response.body
                        if (body != null) {
                            val items = body.items?.itemList
                            if (items != null && items.isNotEmpty()) {
                                val result = buildResultString(items)
                                binding.PETextV.text = result
                            } else {
                                binding.PETextV.text = "데이터 없음"
                            }
                        } else {
                            binding.PETextV.text = "API 호출 실패"
                        }
                    }else{
                        val errorCode = headers["resultCode"]
                        Log.d("API Call", "API 호출 실패. HTTP 상태 코드: $errorCode")
                    }
                    Log.d("API Call", "API 호출 완료")
                        */
/*response.items?.let { items ->
                            buildResultString(items)
                            val result = buildResultString(items)
                            binding.PETextV.text = result
                        } ?: run{
                            binding.PETextV.text = "API 호출 실패"
                        }*//*


                    Log.d("API Call", "API 호출 완료")
                } catch (e: Exception){
                    Log.d("API Call", "API 호출 실패 :" + e.toString())
            }

            }
        }
        return binding.root
    }
    private fun buildResultString(items: List<Item>?): String{
        val stringBuilder = StringBuilder()

        items?.let { itemList ->
            for (item in itemList) {
                stringBuilder.append("기술사\n")
                stringBuilder.append("회차 : ${item.description}\n")
                stringBuilder.append("원서접수시작일자 : ${item.docregstartdt}\n")
                stringBuilder.append("회차 : ${item.docregenddt}\n")
            }
        }
        return stringBuilder.toString()
    }
    */
/*inner class NetworkThread: Thread(){
        val fragment = this@PEListFragment
        suspend fun execute() {
            try {
                val key = "dOG7sXkHheFFUg9Xk0bk5OjyRnb4ta51bsno7uzH%2Fsu8Hql4H6i1UyHuprT6dwHXDxqeN%2FgwIZA0FYFR2ld1%2Fw%3D%3D"
                val url = "http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC?_wadl&_type=xml&key=$key"

                var conn = URL(url).openConnection() as HttpURLConnection
                var input = conn.getInputStream()

                var factory = DocumentBuilderFactory.newInstance()
                var builder = factory.newDocumentBuilder()

                var doc = builder.parse(input)
                var root = doc.documentElement

                var item_node_list = root.getElementsByTagName("item")

                for(i in 0 until item_node_list.length){
                    var item_element = item_node_list.item(i) as Element

                    var turnData_list = item_element.getElementsByTagName("description")
                    var ReceptionS_list = item_element.getElementsByTagName("docregstartdt")
                    var ReceptionE_list = item_element.getElementsByTagName("docregenddt")

                    var turnData_node = turnData_list.item(0) as Element
                    var ReceptionS_node = ReceptionS_list.item(0) as Element
                    var ReceptionE_node = ReceptionE_list.item(0) as Element

                    var turnData = turnData_node.textContent
                    var ReceptionS = ReceptionS_node.textContent
                    var ReceptionE = ReceptionE_node.textContent


                    activity?.runOnUiThread{
                        binding.PETextV.append("회차 : ${turnData}\n")
                        binding.PETextV.append("원서접수시작일자 : ${ReceptionS}\n")
                        binding.PETextV.append("원서접수종료일자 : ${ReceptionE}\n")
                    }
                }
                Log.d("TTT","호출완료")
            }catch (e: Exception){
                e.printStackTrace()
                Log.d("TTT", "호출실패")
            }
        }
    }*//*

}
*/


/*class NetworkThread(private val url: String) {
    suspend fun execute(): String {
        var result = ""
        try {
            Log.d("ttt", "API 호출 시작")
            val xml: Document =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL(url).openStream())
            xml.documentElement.normalize()
            val list: NodeList = xml.getElementsByTagName("item")
            for (i in 0 until list.length) {
                val n: Node = list.item(i)
                if (n.nodeType == Node.ELEMENT_NODE) {
                    val elem = n as Element
                    result += "기술사\n"
                    result += "회차 : ${elem.getElementsByTagName("description").item(0).textContent}\n"
                    result += "원서접수시작일자 : ${elem.getElementsByTagName("docregstartdt").item(0).textContent}\n"
                    result += "원서접수종료일자 : ${elem.getElementsByTagName("docregenddt").item(0).textContent}\n"
                }
            }
            Log.d("ttt", "API 호출 완료")
        } catch (e: Exception) {
            Log.d("ttt", "API 호출 실패: " + e.toString())
        }
        Log.d("API 결과", result)
        return result
    }
}*/

/*fun main(args: Array<String>) {
    val urlBuilder =
        StringBuilder("http://openapi.q-net.or.kr/api/service/rest/InquiryTestInformationNTQSVC/getPEList") *//*URL*//*
    urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "dOG7sXkHheFFUg9Xk0bk5OjyRnb4ta51bsno7uzH%2Fsu8Hql4H6i1UyHuprT6dwHXDxqeN%2FgwIZA0FYFR2ld1%2Fw%3D%3D") *//*Service Key*//*
    val url = URL(urlBuilder.toString())
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    conn.setRequestProperty("Content-type", "application/json")
    println("Response code: " + conn.responseCode)
    val rd: BufferedReader
    rd = if (conn.responseCode >= 200 && conn.responseCode <= 300) {
        BufferedReader(InputStreamReader(conn.inputStream))
    } else {
        BufferedReader(InputStreamReader(conn.errorStream))
    }
    val sb = StringBuilder()
    var line: String?
    while (rd.readLine().also { line = it } != null) {
        sb.append(line)
    }
    rd.close()
    conn.disconnect()
    println(sb.toString())
}*/
