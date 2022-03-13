package com.example.keepthetime_220312

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivitySignUpBinding
import com.example.keepthetime_220312.datas.BasicResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        setupEvents()
        setValues()

    }

    override fun setupEvents() {

//        도전과제 : 닉네임 중복 확인 기능 추가.
//          1) 중복확인 버튼배치 / 클릭시 이벤트 처리
//          2) APIList 인터페이스에 함수 추가 필요 X. (기존의 getRequestDuplicatedCheck  기능 재활용)
//          3) 닉네임 중복 API 실행 (type - "NICK_NAME") / 응답 처리리

       binding.btnSignUp.setOnClickListener {

            val inputEmail = binding.edtEmail.text.toString()
            val inputPassword = binding.edtPassword.text.toString()
            val inputNickname = binding.edtNickname.text.toString()

//            레트로핏 세팅 >> 회원가입 진행.

            apiList.putRequestSignUp(inputEmail,inputPassword,inputNickname).enqueue( object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    if (response.isSuccessful)

//                        BaseActivity에서 미리 세팅해준 this에 해당하는, mContext 변수 활용
                        Toast.makeText(mContext, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()


                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })
        }

        binding.btnEmailCheck.setOnClickListener {

//            입력된 이메일 추출 > 서버에 중복을 물어보자.
            val inputEmail = binding.edtEmail.text.toString()

//            실제로 API 기능 사용.
            apiList.getRequestDuplicatedCheck("EMAIL", inputEmail).enqueue(object :Callback<BasicResponse>{
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
//                  response 변수가, 서버의 응답을 들고 있다. => 그 내부 분석.
//                    1) 최종 성공? - 모든 중복을 피해서, 사용해도 되는 경우만.
//                    2) 본문에 들어있는 내용?

                    if(response.isSuccessful) {

                        val br = response.body()!!

                        Toast.makeText(mContext, br.message, Toast.LENGTH_SHORT).show()
                    }
                    else {

//                        응답은 돌아왔지만, 그 내용이 실패로 판정. => 중복이라 사용안됨.
//                        실패시에는 , BasicResponse 자동 분석 기능 X . 별도로 JSONObject를 직접 다뤄야함.
//                        에러의 경우, errorBody()에 서버가 준 응답이 담겨있다. body() 아님.
//                        String() 으로 불러내야 String 형태로 변환 해줌.

                        val jsonObj = JSONObject(response.errorBody()!!.string())

                        val message = jsonObj.getString("message")

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }
            })
        }
    }

    override fun setValues() {

    }
}