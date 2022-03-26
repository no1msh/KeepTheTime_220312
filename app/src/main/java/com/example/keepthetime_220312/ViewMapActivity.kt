package com.example.keepthetime_220312

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivityViewMapBinding
import com.example.keepthetime_220312.datas.AppointmentData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener

class ViewMapActivity : BaseActivity() {

    lateinit var binding: ActivityViewMapBinding

    lateinit var mAppointmentData: AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_map)

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

//        약속이름을 화면의 제목으로
        txtTitle.text = mAppointmentData.title

//        지도 객체 얻어오기

        binding.mapView.getMapAsync {

            val naverMap = it

//            naverMap을 이용해서 , 약속 장소 좌표 표시

//            약속 장소 => LatLng 클래스로 저장해두자.

            val latLng = LatLng(mAppointmentData.latitude, mAppointmentData.longitude)

//            지도 조작 코드

            val cameraUpdate = CameraUpdate.scrollTo(latLng)

            naverMap.moveCamera( cameraUpdate)

            val marker = Marker()
            marker.position = latLng
            marker.map = naverMap

////            정보창 띄우기
//
//            val infoWindow = InfoWindow()
//
////            object : 추상클래스(생성자) = > 추상 클래스 객체를 담는다.
////            object : 인터페이스 {    } = > 인터페이스는 생성자가 없이 사용한다.
//            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
//                override fun getText(p0: InfoWindow): CharSequence {
////                    charSequence : String 으로 생각해도 무방.
//                    return mAppointmentData.place
//                }
//
//            }
//
//            infoWindow.open(marker)

//                대중교통 길찾기 라이브러리 활용 => 소요 시간 + 비용 정보창 띄우기.

            val odSay = ODsayService.init(mContext,"ik7tX98HxBmQ0FiXS7YdjDPOKvFQg1JyBUdOU0C3pFo")

            odSay.requestSearchPubTransPath(
                mAppointmentData.start_longitude.toString(), // 출발지 X좌표 (경도)를 String으로
                mAppointmentData.start_latitude.toString(), // 출발지 Y좌표 (위도)를 String으로
                mAppointmentData.longitude.toString(), // 도착지 (약속장소) X좌표 (경도)를 String
                mAppointmentData.latitude.toString(),
                null,
                null,
                null,
                object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {
                        // 길찾기 응답이 돌아오면 할 일.

                        val jsonObj = p0!!.json // 길찾기 응답이 돌아온 JSONObject 변수로 저장
                        Log.d("길찾기응답", jsonObj.toString())

//                        jsonObj의 내부에서, => result라는 이름표를 가진 { } 추출
//                        result가 JSONObject라고 명시 : resultObj로 변수 이름 설정.
                        val resultObj = jsonObj.getJSONObject("result")

//                        result: { } 안에서, path라는 이름의 [ ] 추출
//                        path가 JSONArray라고 명시 : path"Arr"로 변수 이름 설정.
                        val pathArr = resultObj.getJSONArray("path")

//                        0번칸 ( 맨 앞칸) 에 있는 경로만 사용 = > { } 추출

                        val firstPathObj = pathArr.getJSONObject(0)

                        Log.d("첫번째 경로 정보", firstPathObj.toString())

//                      첫번째 경로 정보 추출
                        val infoObj = firstPathObj.getJSONObject("info")

//                        시간 값 / 요금 값

                        val totalTime = infoObj.getInt("totalTime")
                        val payment = infoObj.getInt("payment")

//                        infoWindow (네이버 지도 기능)에 활용 + 로직 활용

                        val infoWindow = InfoWindow()

                        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext){
                            override fun getText(p0: InfoWindow): CharSequence {
                                return "이동시간 : ${totalTime}분 , 소용비용 : ${payment}원"
                            }


                        }
                        infoWindow.open(marker)

                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {

                    }

                }

            )

        }
    }
}