package com.example.keepthetime_220312

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivityViewMapBinding
import com.example.keepthetime_220312.datas.AppointmentData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

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

            naverMap.moveCamera(cameraUpdate)

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

            val odSay = ODsayService.init(mContext, "ik7tX98HxBmQ0FiXS7YdjDPOKvFQg1JyBUdOU0C3pFo")

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
//                        단순 텍스트가 아니라, 복잡한 모양의 말풍선
                        val infoWindow = InfoWindow()

                        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {

                            override fun getContentView(p0: InfoWindow): View {
//                              리스트 뷰의 getView 함수와 비슷한 구조 (return 타입 구조)
//                              LayoutInflater로 xml을 객체로 가져와서 => 리턴해보자.

                                val view = LayoutInflater.from(mContext)
                                    .inflate(R.layout.place_info_window_content, null)

//                                view 변수 안에서, id를 가지고 태그들을 찾아서 (findViewById) = > 변수에 저장

                                val txtPlaceName = view.findViewById<TextView>(R.id.txtPlaceName)
                                val txtTotalTime = view.findViewById<TextView>(R.id.txtTotalTime)
                                val txtPayment = view.findViewById<TextView>(R.id.txtPayment)

                                txtPlaceName.text = mAppointmentData.place
                                txtTotalTime.text = "${totalTime}분"
                                txtPayment.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(payment)}"
                                return view
                            }

                        }
                        infoWindow.open(marker)

                        val path = PathOverlay()

//                        어느 점들을 지나치는지 , 좌표 목록 => 임시 : 출발지/ 도착지만
                        val pathPoints = ArrayList<LatLng>()

                        val startLatLng = LatLng (mAppointmentData.start_latitude , mAppointmentData.start_longitude)
                        pathPoints.add(startLatLng)

//                        첫번째 경로의 => 이동 경로 세부목록 파싱

                        val subPathArr = firstPathObj.getJSONArray("subPath")

//                        subPahtArr에 들어있는 내용물의 갯수직전까지 반복

                        for (i in 0 until subPathArr.length() ) {
//                            subPath"Arr" 에서 , 반복문을 도는 i변수 값에 맞는 위치에 있는 , JSONObject { } 추출
                            val subPathObj = subPathArr.getJSONObject(i)

//                            세부 경로 중에서 , 정거장 목록을 주는 세부 경로만 추가 파싱.
//                            subPathObj 내부에 , "passStopList"라는 이름표의 데이터가 있는지? 확인.

                            if ( !subPathObj.isNull("passStopList")){
                                val passStopListObj = subPathObj.getJSONObject("passStopList")
//                              정거장 목록의 위도/경도 추출 => pathPoints ArrayList에 좌표 추가
                                val stationsArr = passStopListObj.getJSONArray("stations")

                                for ( j in 0 until stationsArr.length()){
                                    val stationObj = stationsArr.getJSONObject(j)
                                    Log.d("정거장 내역", stationObj.toString())

//                                    위도 (String으로 길찾기 라이브러리가 제공 ) > Double로 변환 추출 => lat 변수에 저장.
                                    val stationLat = stationObj.getString("y").toDouble()
                                    val stationLng = stationObj.getString("x").toDouble()

//                                    네이버 지도 좌표 객체로 만들자.

                                    val stationLatLng = LatLng( stationLat, stationLng)

//                                    경로선이 지나갈 좌표로 추가.
                                    pathPoints.add( stationLatLng)
                                }
                            }
                        }

                        pathPoints.add (latLng)



                        path.coords = pathPoints
                        path.map = naverMap

                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {

                    }

                }

            )

        }
    }
}