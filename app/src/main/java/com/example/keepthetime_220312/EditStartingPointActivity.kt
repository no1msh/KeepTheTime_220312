package com.example.keepthetime_220312

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivityEditStartingPointBinding
import com.example.keepthetime_220312.datas.BasicResponse
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditStartingPointActivity : BaseActivity() {

    lateinit var binding : ActivityEditStartingPointBinding

//    하나의 마커가 계속 위치가 변경. => 멤버변수

//    처음에는 안찍혀있게.(마커가 없게) => null로 초기값 설정

    var pointMarker : Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_starting_point)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.btnSave.setOnClickListener {

            val inputName = binding.edtStartingPointName.text.toString()

            if(inputName.isEmpty()) {

                Toast.makeText(mContext, "출발지 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            마커가 지도에 없다면? 아직 위치 선택 x

            if(pointMarker == null) {
                Toast.makeText(mContext, "지도에서 출발지 위치를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            실제 보내줄 데이터 정리

            val startLat = pointMarker!!.position.latitude
            val startLng = pointMarker!!.position.longitude

            val isPrimary = binding.primaryCheckBox.isChecked

            apiList.postRequestAddStartingPoint(
                inputName,
                startLat,
                startLng,
                isPrimary
            ).enqueue( object : Callback<BasicResponse>{
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if(response.isSuccessful){
                        Toast.makeText(mContext, "출발지 등록에 성공했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                }
            })
        }
    }

    override fun setValues() {


        binding.naverMapView.getMapAsync {

            val naverMap = it

            naverMap.setOnMapClickListener { pointF, latLng ->

//                latLng 변수가 클릭된 좌표. => 마커로 표시.

                if (pointMarker == null) {
                    pointMarker = Marker()
                }

                pointMarker!!.position = latLng
                pointMarker!!.map = naverMap
            }

        }
    }
}