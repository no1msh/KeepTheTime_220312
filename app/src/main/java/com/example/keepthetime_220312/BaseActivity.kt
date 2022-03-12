package com.example.keepthetime_220312

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.keepthetime_220312.api.APILIst
import com.example.keepthetime_220312.api.ServerAPI
import retrofit2.create

// 상속 활용 : 우리화면 > BaseActivity() > AppcompatActivity()
// 모든 화면이 공통적으로 사용할 것들 : 여기에서 코딩, 상속을 통해 쉽게 사용 가능
abstract class BaseActivity : AppCompatActivity() {

//    멤버변수 - this를 화면이 만들어 질 때 , 미리 담아두는 변수.

    lateinit var mContext : Context

//    apiList : 앱에서 활용할 수 있는 API 목록.
    lateinit var apiList: APILIst

//    다른 화면의super.onCreate가 실행될 때 , 부가적으로 실행해줄 코드들 추가
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this // this 변수를 사용할 상황 : 무조건 mContext를 대신 사용.

//    apiList 변수에 세팅

        val retrofit = ServerAPI.getRetrofit()
        apiList = retrofit.create(APILIst::class.java)
    }

//    함수 - setupEvents / setValues 모든 화면이 ( 각각 내용이 다르게 ) 구현.
//    추상 함수 - 실행 내용이 없이 물려주자. => 자식이 상속받은 함수를 반드시 구현하게 의무 부여.
//    클래스 자체도 추상 클래스여야 , 추상 함수 보유 가능
    abstract fun setupEvents()
    abstract fun setValues()
}