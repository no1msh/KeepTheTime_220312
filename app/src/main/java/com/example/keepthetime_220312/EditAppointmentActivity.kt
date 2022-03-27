package com.example.keepthetime_220312

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.adapters.StartingPointSpinnerAdapter
import com.example.keepthetime_220312.databinding.ActivityEditAppointmentBinding
import com.example.keepthetime_220312.datas.BasicResponse
import com.example.keepthetime_220312.datas.StartingPointData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding : ActivityEditAppointmentBinding

//    선택한 약속일시를 저장하는 Calendar 변수

    val mSelectedDatetimeCal = Calendar.getInstance()

//
    var myMarker : Marker? = null

    val mStartingPointList = ArrayList<StartingPointData>()

    lateinit var mStartingPointSpinnerAdapter : StartingPointSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_appointment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

//        지도 / 스크롤뷰의 상하 스크롤이 겹쳐서 지도에 터치 관련 문제 발생
//        지도 위에 텍스트뷰를 텊어두고, 해당 텍스트뷰에 손이 닿으면(터치O 클릭X) => 스크롤뷰의 스크롤을 일시정지.

        binding.txtScrollHelp.setOnTouchListener { view, motionEvent ->

            binding.scrollView.requestDisallowInterceptTouchEvent(true)

//         리턴처리 필요 : 손이 닿아도 밑에 깔린 지도의 이벤트도 실행
            return@setOnTouchListener false;
        }

        binding.txtDate.setOnClickListener {

//            날짜가 선택되면 할 일 저장

            val dsl = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

//                    year, month, dayOfMonth = > 달력을 통해서 선택한 일자 정보
//                    Toast.makeText(mContext, "${year}년 ${month}월 ${dayOfMonth}일", Toast.LENGTH_SHORT).show()

//                    선택된 일시를 저장할 변수에, 연/월/일 세팅.

                    mSelectedDatetimeCal.set(year,month,dayOfMonth)

//                    약속 일자 텍스브튜의 문구를 3월 20일 형태로 출력
//                    Calendar(내부의 데이터)를 String으로 가공 전문(SimpleDataFormat) 활용

                    val sdf = SimpleDateFormat("M월 d일")

//                    sdf로 format해낸 String을, txtDate의 문구로 반영
                    binding.txtDate.text = sdf.format(mSelectedDatetimeCal.time)
                }

            }

//            실제로 달력 팝업 띄우기.

//            선택한 일시 (기본값 : 현재일시) 의 연월일을 띄워보자
        val dpd = DatePickerDialog(
            mContext,
            dsl,
            // 팝업이 떴을 때 기본값 설정
            mSelectedDatetimeCal.get(Calendar.YEAR), // 선택일시의 년도만 배치
            mSelectedDatetimeCal.get(Calendar.MONTH), // 선택일시의 년도만 배치
            mSelectedDatetimeCal.get(Calendar.DAY_OF_MONTH), // 선택일시의 년도만 배치

        ).show()



        }

        binding.txtTime.setOnClickListener {

            val tsl = object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

//                    선택된 일시에, 시간/ 분 저장
                    mSelectedDatetimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    mSelectedDatetimeCal.set(Calendar.MINUTE, minute)
//                    txtTime의 문구를 "오후 7시 5분" 양식으로 가공

                    val sdf = SimpleDateFormat("a h시 m분")

                    binding.txtTime.text = sdf.format(mSelectedDatetimeCal.time) // Date 형태인 time 변수 활용

                }

            }

            val tpd = TimePickerDialog(
                mContext,
                tsl,
                12,
                30,
                false // 24h 설정
            ).show()
        }

        binding.btnSave.setOnClickListener {

//            입력한 값들 추출 = > 서버에 전송

            var inputTitle = binding.edtTitle.text.toString()
            if ((inputTitle.isEmpty())){
                Toast.makeText(mContext, "제목을 입력해야 합니다.", Toast.LENGTH_SHORT).show()
//                실행중인 함수 강제 종료 => 결과 임의 설정.
                return@setOnClickListener
            }


//            약속일시 : mSelectedDatetimeCal 의 일시를 => "2022-03-20 14:19:50" 형태로 가공해서 첨부


//            약속 일시 가공전에, 일자 / 시간 모두 선택했는지 체크. 선택하지 않은 항목이 있다면 안내 + 함수 종료
            if (binding.txtDate.text == "약속 일자" || binding.txtTime.text == "약속 시간") {
                Toast.makeText(mContext, "일시를 모두 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val inputPlaceName = binding.edtPlaceName.text.toString()
            if (inputPlaceName.isEmpty()) {
                Toast.makeText(mContext, "약속 장소 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val serverDateTimeStr = serverFormat.format(mSelectedDatetimeCal.time) // 첨부할 약속 일시

//            네이버 지도에 마커로 찍어둔 장소 > 서버에 전송?





//            myMarker가 실제로 만들어져 있는지? 그렇지 않다면 장소 입력 안내 + 함수 종료.
            if (myMarker == null) {
                Toast.makeText(mContext, "지도를 클릭해서, 약속 장소를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


//            내가 찍어둔 마커가 있다고 전제하고 코딩
            val lat = myMarker!!.position.latitude // 찍힌 마커의 위도 추출
            val lng = myMarker!!.position.longitude // 찍힌 마커의 경도 추출

//            출발지 목록 Spinner에서 , 어떤 출발지를 선택했는지 받아오자. ==> 출발지 정보로 서버에 첨부.

//            스피너의 선택 위치 추출
            val selectedPosition = binding.startingPointSpinner.selectedItemPosition
//            해당 위치에 맞는 출발지 데이터 가져오기
            val selectedStartingPoint = mStartingPointList[selectedPosition]

//            서버에 파라미터값들 전송. (API 호출)
            apiList.postRequestAppointment(
                inputTitle,
                serverDateTimeStr,
                selectedStartingPoint.name,
                selectedStartingPoint.latitude,
                selectedStartingPoint.longitude,
                inputPlaceName,
                lat,
                lng
            ).enqueue(object : Callback<BasicResponse>{
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful){
//                        무조건 성공으로 처리. 화면 종료

                        Toast.makeText(mContext, "약속을 등록했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })
        }
    }

    override fun setValues() {
        binding.mapView.getMapAsync {
//           it 변수 대신 문서와 같은 이름의 변수 naverMap에 옮겨 담고 사용.
            val naverMap = it

//            기본 지도의 시작화면 : 서울 시청 => 네이버 지도의 시작 좌표 : 부천 종합 운동장

            val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.502790283581284, 126.79867835775708))
            naverMap.moveCamera(cameraUpdate)

//            마커띄우기

            val marker = Marker()
            marker.position = LatLng(37.502790283581284, 126.79867835775708)
            marker.map = naverMap

//            마커 색상 변경
            marker.icon = MarkerIcons.LIGHTBLUE // 원하는 색 커스텀
            marker.iconTintColor = Color.parseColor("#FF0000") // 안드로이드가 제공하는 색

//            마커크기 변경
            marker.width = 40
            marker.height = 40

//            네이버 지도의 클릭 이벤트

            naverMap.setOnMapClickListener { pointF, latLng ->

//                클릭된 좌표 latLng 변수의 내용을 토스트로 출력
//                Toast.makeText(mContext, "위도 : ${latLng.latitude} , 경도 : ${latLng.longitude}", Toast.LENGTH_SHORT).show()

//                마커를 클릭된 지점에 설정.

//                myMarker가 만들어진게 없다면 새로 마커 생성.
//                만들어진게 있다면, 기존 마커 재활용.

                if (myMarker == null)
                {
                    myMarker = Marker()
                }
                myMarker!!.position = latLng
                myMarker!!.map = naverMap
            }

        }

        getMyStartingPointFromServer()

        mStartingPointSpinnerAdapter = StartingPointSpinnerAdapter(mContext, R.layout.starting_point_list_item, mStartingPointList)
        binding.startingPointSpinner.adapter = mStartingPointSpinnerAdapter
    }

//    내 출발지 목록이 어떤것들이 있는지 불러오자.

    fun getMyStartingPointFromServer() {
        apiList.getRequestMyStartingPoint().enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                val br = response.body()!!

                mStartingPointList.addAll( br.data.places )

                mStartingPointSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }
        })
    }
}