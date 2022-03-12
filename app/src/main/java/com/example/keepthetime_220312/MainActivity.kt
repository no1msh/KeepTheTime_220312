package com.example.keepthetime_220312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.api.APILIst
import com.example.keepthetime_220312.api.ServerAPI
import com.example.keepthetime_220312.databinding.ActivityMainBinding
import com.example.keepthetime_220312.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

//    binding : 어떤 xml을 접근하는지. 자료형으로 설정.
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_main)
        setUpEvents()
        setValues()
    }

    fun setUpEvents(){

        binding.btnLogin.setOnClickListener {

            val inputId = binding.edtId.text.toString()
            val inputPw = binding.edtPassword.text.toString()

//            keepthetime.xyz / 로그인 기능에 아이디 / 비번을 보내보자.

            val myRetrofit = ServerAPI.getRetrofit()
            val myApiList = myRetrofit.create(APILIst :: class.java)

            myApiList.postRequestLogin( inputId , inputPw).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
//                    로그인 결과가 성공이던 / 실패던 응답(response 변수) 자체는 돌아온 경우
                    if (response.isSuccessful){

                        val br = response.body()!! // 기본 분석 완료된 BasicResponse 를 br 변수에 담자.

                        Toast.makeText(this@MainActivity, br.message , Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                    아예 물리적으로 서버 연결 자체를 실패.
                }

            })
        }

        binding.btnSignUp.setOnClickListener {
            val myIntent = Intent(this, SignUpActivity :: class.java)
            startActivity(myIntent)
        }

    }

    fun setValues(){

    }
}