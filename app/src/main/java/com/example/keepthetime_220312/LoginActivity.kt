package com.example.keepthetime_220312

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.api.APILIst
import com.example.keepthetime_220312.api.ServerAPI
import com.example.keepthetime_220312.databinding.ActivityLoginBinding
import com.example.keepthetime_220312.datas.BasicResponse
import com.example.keepthetime_220312.utils.ContextUtil
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    //    binding : 어떤 xml을 접근하는지. 자료형으로 설정.
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setupEvents()
        setValues()
        setCustomActionBar()
    }

    override fun setupEvents() {

        binding.autoLoginCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->

//            isChecked변수에, 지금 체크 되었는지? 해제되었는지? 알려줌.

            ContextUtil.setAutoLogin(mContext, isChecked)
        }


        binding.btnLogin.setOnClickListener {

            val inputId = binding.edtId.text.toString()
            val inputPw = binding.edtPassword.text.toString()

//            keepthetime.xyz / 로그인 기능에 아이디 / 비번을 보내보자.

            val myRetrofit = ServerAPI.getRetrofit(mContext)
            val myApiList = myRetrofit.create(APILIst::class.java)

            apiList.postRequestLogin(inputId, inputPw).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
//                    로그인 결과가 성공이던 / 실패던 응답(response 변수) 자체는 돌아온 경우
                    if (response.isSuccessful) {

                        val br = response.body()!! // 기본 분석 완료된 BasicResponse 를 br 변수에 담자.

                        Toast.makeText(mContext, br.message, Toast.LENGTH_SHORT).show()

//                        data > token 변수 로그로 찍어보기

                        Log.d("토큰", br.data.token)

//                        받아온 토큰값을 기기에 저장 => 나중에 많은 화면에서 활용
                        ContextUtil.setToken(mContext, br.data.token)

//                        로그인한 사람의 닉네임을, 토스트로 띄워보기.
                        Toast.makeText(
                            mContext,
                            "${br.data.user.nick_name}님 환영합니다.",
                            Toast.LENGTH_SHORT
                        ).show()

//                        메인화면으로 이동, 로그인 화면 종료

                        val myIntent = Intent(mContext, MainActivity::class.java)
                        startActivity(myIntent)

                        finish()
                    }
                    else {
//                        로그인에 성공 아닌경우. (비번틀림, 아이디 틀림 등등..)
//                        BasicResponse 변환 x. => JSONObject 받아내서 직접 파싱.

                        val jsonObj = JSONObject ( response.errorBody()!!.string()) // .toString() 아님!!

                        val message = jsonObj.getString("message")

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                    아예 물리적으로 서버 연결 자체를 실패.
                }

            })
        }

        binding.btnSignUp.setOnClickListener {
            val myIntent = Intent(mContext, SignUpActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {
//        저장해둔 자동로그인 여부를 , 체크박스의 isChecked속성에 대입.

        binding.autoLoginCheckBox.isChecked = ContextUtil.getAutoLogin(mContext)
    }
}