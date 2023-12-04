package com.example.youthneverdie.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.youthneverdie.LoginActivity
import com.example.youthneverdie.R
import com.example.youthneverdie.auth
import com.example.youthneverdie.databinding.DialogChangePasswordBinding
import com.example.youthneverdie.databinding.FragmentSettingBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setUpView()

        binding.serviceBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.setting_service, null)
            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)

            val dialog = builder.create()

            val servicetext = dialogView.findViewById<TextView>(R.id.text_view)
            val textFromResources = resources.getString(R.string.use_service) // strings.xml에서 해당 문자열 리소스 가져오기
            servicetext.text = textFromResources
            dialog.show()
        }

        binding.informationBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.setting_service,null)
            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)

            val dialog = builder.create()

            val informationtext = dialogView.findViewById<TextView>(R.id.text_view)
            val textFromResources = resources.getString(R.string.information_check)
            informationtext.text = textFromResources
            dialog.show()
        }
        return binding.root
    }
    private fun setUpView() {
        binding.logoutBtn.setOnClickListener{
            Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth.signOut()
        }
        binding.signoutBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("회원탈퇴")
                .setMessage("정말로 회원탈퇴를 하시겠습니까?")
                .setPositiveButton("예") {
                        dialogInterface:DialogInterface, _:Int->
                    emailDelete()
                }
                .setNegativeButton("아니오", null)
                .show()
        }
        binding.changepwBtn.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("비밀번호 변경")

            val newPassword = EditText(requireContext())
            newPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            newPassword.hint = "새 비밀번호"
            builder.setView(newPassword)
            builder.setMessage("변경할 비밀번호를 입력해 주세요.")
                .setPositiveButton("확인"){
                    dialogInterface:DialogInterface,_:Int->
                        val newPassword = newPassword.text.toString()
                        if (newPassword.isNotEmpty()) {
                            passwordChange(newPassword)
                        }
                }
                .setNegativeButton("취소",null)
                .show()
        }
    }
    fun passwordChange(newPassword: String){
        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    Toast.makeText(requireContext(), "비밀번호가 변경되었습니다. 다시 로그인해주십시오.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }else{
                    Toast.makeText(requireContext(), "변경 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun emailDelete(){
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                auth.signOut()
                Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }else{
                Toast.makeText(requireContext(), "실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}