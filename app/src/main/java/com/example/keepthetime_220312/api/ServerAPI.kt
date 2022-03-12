package com.example.keepthetime_220312.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerAPI {

//    Retrofit 클래스의 객체가 , 서버와 통신을 주고 받는다.
//    하나의 객체만 있으면, 여러번 통신이 가능. => 객체를 하나만 유지해서 공유하자.

    companion object {
//        서버통신 담당 클래스 : 레틀핏 클래스 객체를 담아줄 변수.


        private var retrofit : Retrofit? = null // 초기에는 만들어두지 않는다.

//        어느 서버에서 기능들을 활용할지. 기본 주소
        private val BASE_URL = "https://keepthetime.xyz"

//        레트로핏 객체를 받아내는 기능 (함수)
//        retrofit변수에 null이 들어있다면? = > 새로 Retrofit 객체를 생성.
//        이미 null이 아니라, 실체가 들어있다면? => 이미 들어있는 객체를 재활용.

        fun getRetrofit() : Retrofit {

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory( GsonConverterFactory.create() ) // 서버가 주는 JSON 양식을, 일반 자료형 / 클래스로 쉽게변환해주는 도구 세팅
                    .build() // 세팅이 모두 끝났으면 , Retrofit 객체로 만들어달라.
            }

//            retrofit 변수는 절대 null일 리가 없다.

            return retrofit!!

        }
    }
}