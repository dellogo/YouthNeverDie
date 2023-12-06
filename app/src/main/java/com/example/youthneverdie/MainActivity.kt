package com.example.youthneverdie

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.youthneverdie.Adapter.FragmentAdapter
import com.example.youthneverdie.databinding.ActivityLoginBinding
import com.example.youthneverdie.databinding.ActivityMainBinding
import com.example.youthneverdie.databinding.ActivitySignupBinding
import com.example.youthneverdie.fragment.CommunityFragment
import com.example.youthneverdie.fragment.HomeFragment
import com.example.youthneverdie.fragment.OpenbookFragment
import com.example.youthneverdie.fragment.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.regex.Pattern
import kotlin.math.sign

lateinit var auth: FirebaseAuth

fun passwordRegex(password: String) : Boolean {
    return password.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$".toRegex())
}

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent= Intent( this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun detectEmailAndPasswordEmpty() {
            binding.loginBtn.isEnabled = false

            binding.IDText.addTextChangedListener{
                val email = binding.IDText.text.toString()
                val password = binding.PWText.text.toString()
                val enalbed = email.isNotEmpty() && password.isNotEmpty()
                binding.loginBtn.isEnabled = enalbed
            }
            binding.PWText.addTextChangedListener{
                val email = binding.IDText.text.toString()
                val password = binding.PWText.text.toString()
                val enalbed = email.isNotEmpty() && password.isNotEmpty()
                binding.loginBtn.isEnabled = enalbed
            }
        }
        detectEmailAndPasswordEmpty()

        binding.signupBtn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener{
            val email: String = binding.IDText.text.toString()
            val password: String = binding.PWText.text.toString()

            MyApplication.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    binding.IDText.text?.clear()
                    binding.PWText.text?.clear()
                    if(task.isSuccessful){
                        if (MyApplication.checkAuth()){
                            MyApplication.email = email
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(baseContext,
                                "전송된 메일로 이메일 인증이 완료되지 않았습니다.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(baseContext,"이메일 또는 비밀번호를 잘못 입력했습니다.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "회원가입"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{3,})$"

        fun checkEmail(): Boolean{
            var email = binding.emailText.text.toString().trim()
            val pat = Pattern.matches(emailValidation, email)
            if (pat) {
                binding.emailCheckError.setText("올바른 이메일 형식입니다.")
                binding.emailCheckError.setTextColor(Color.parseColor("#87CEFA"))
                return true
            } else {
                binding.emailCheckError.setText("올바르지 않은 이메일 형식입니다.")
                binding.emailCheckError.setTextColor(Color.parseColor("#D32F2F"))
                return false
            }
        }
        binding.emailText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){
                checkEmail()
                if (!checkEmail()){
                    binding.signupGogoBtn.isEnabled=false
                }else {
                    binding.signupGogoBtn.isEnabled=true
                }
            }
        })

        binding.signupPwText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.signupPwText.getText().toString().equals(binding.signupPwCheckText.getText().toString())){
                    binding.pwCheckError.setText("비밀번호가 일치합니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#87CEFA"))
                    binding.signupGogoBtn.isEnabled=true
                }
                else{
                    binding.pwCheckError.setText("비밀번호가 일치하지 않습니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#D32F2F"))
                    binding.signupGogoBtn.isEnabled=false
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.signupPwCheckText.getText().toString().equals(binding.signupPwText.getText().toString())){
                    binding.pwCheckError.setText("비밀번호가 일치합니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#87CEFA"))
                    binding.signupGogoBtn.isEnabled=true
                }
                else{
                    binding.pwCheckError.setText("비밀번호가 일치하지 않습니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#D32F2F"))
                    binding.signupGogoBtn.isEnabled=false
                }
            }
        })
        binding.signupPwCheckText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.signupPwText.getText().toString().equals(binding.signupPwCheckText.getText().toString())){
                    binding.pwCheckError.setText("비밀번호가 일치합니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#87CEFA"))
                    binding.signupGogoBtn.isEnabled=true
                }
                else{
                    binding.pwCheckError.setText("비밀번호가 일치하지 않습니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#D32F2F"))
                    binding.signupGogoBtn.isEnabled=false
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.signupPwCheckText.getText().toString().equals(binding.signupPwText.getText().toString())){
                    binding.pwCheckError.setText("비밀번호가 일치합니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#87CEFA"))
                    binding.signupGogoBtn.isEnabled=true
                }
                else{
                    binding.pwCheckError.setText("비밀번호가 일치하지 않습니다.")
                    binding.pwCheckError.setTextColor(Color.parseColor("#D32F2F"))
                    binding.signupGogoBtn.isEnabled=false
                }
            }
        })

        fun detectEmailAndPasswordEmpty() {
            binding.signupGogoBtn.isEnabled = false

            binding.emailText.addTextChangedListener{
                val email = binding.emailText.text.toString()
                val password = binding.signupPwText.text.toString()
                val passwordcheck = binding.signupPwCheckText.text.toString()
                val enalbed = email.isNotEmpty() && password.isNotEmpty() && passwordcheck.isNotEmpty()
                binding.signupGogoBtn.isEnabled = enalbed
            }
            binding.signupPwText.addTextChangedListener{
                val email = binding.emailText.text.toString()
                val password = binding.signupPwText.text.toString()
                val passwordcheck = binding.signupPwCheckText.text.toString()
                val enalbed = email.isNotEmpty() && password.isNotEmpty() && passwordcheck.isNotEmpty()
                binding.signupGogoBtn.isEnabled = enalbed
            }
            binding.signupPwCheckText.addTextChangedListener {
                val email = binding.emailText.text.toString()
                val password = binding.signupPwText.text.toString()
                val passwordcheck = binding.signupPwCheckText.text.toString()
                val enalbed = email.isNotEmpty() && password.isNotEmpty() && passwordcheck.isNotEmpty()
                binding.signupGogoBtn.isEnabled = enalbed
            }
        }
        detectEmailAndPasswordEmpty()

        binding.signupGogoBtn.setOnClickListener{
            val email: String = binding.emailText.text.toString()
            val password: String = binding.signupPwText.text.toString()
            val passwordcheck: String = binding.signupPwCheckText.text.toString()
            MyApplication.auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    binding.emailText.text?.clear()
                    binding.signupPwText.text?.clear()
                    binding.signupPwCheckText.text?.clear()

                    if (task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(baseContext,
                                        "가입 인증 메일이 발송되었습니다." +
                                                "전송된 메일을 확인하여 주세요.",
                                        Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = FragmentAdapter(this)

        binding.pager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.bottom.menu.getItem(position).isChecked = true
                }
            }
        )
        binding.bottom.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                binding.pager.currentItem = 0
                return true
            }
            R.id.open_book -> {
                binding.pager.currentItem = 1
                return true
            }
            R.id.community -> {
                binding.pager.currentItem = 2
                return true
            }
            R.id.setting -> {
                binding.pager.currentItem = 3
                return true
            }
            else -> {
                return false
            }
        }
    }
}