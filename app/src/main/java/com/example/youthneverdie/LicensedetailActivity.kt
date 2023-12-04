package com.example.youthneverdie

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
/*import com.example.youthneverdie.Adapter.LicenseApiAdapter*/
import com.example.youthneverdie.LicenseAPI.*
import com.example.youthneverdie.databinding.ActivityLicensedetailBinding
import com.google.android.gms.common.api.Response
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/*import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper*/
import okhttp3.Request


class LicensedetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityLicensedetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLicensedetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val LBlankFragment = LBlankFragment()
        val PEListFragment = PEListFragment()
        val MCListFragment = MCListFragment()
        val EListFragment = EListFragment()
        val CListFragment = CListFragment()
        supportFragmentManager.beginTransaction().add(R.id.FContainer, LBlankFragment).commit()
        binding.selectFinishBtn.setOnClickListener{
            val selectedItem = binding.spinners.getChildAt(0)as AppCompatSpinner
            val selectValue = selectedItem.selectedItem.toString()

            val transaction = supportFragmentManager.beginTransaction()
            if (selectValue == "기술사 시험 시행일정 조회"){
                transaction.replace(R.id.FContainer,PEListFragment)
                transaction.commit()
            }
            else if(selectValue == "기능장 시험 시행일정 조회"){
                transaction.replace(R.id.FContainer,MCListFragment)
                transaction.commit()
            }
            else if(selectValue == "기사, 산업기사 시험 시행일정 조회"){
                transaction.replace(R.id.FContainer,EListFragment)
                transaction.commit()
            }
            else if(selectValue == "기능사 시험 시행일정 조회"){
                transaction.replace(R.id.FContainer,CListFragment)
                transaction.commit()
            }
            else {
                Toast.makeText(this, "종목을 선택해 주십시오.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}