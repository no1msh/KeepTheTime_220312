package com.example.keepthetime_220312

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivityEditAppointmentBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding : ActivityEditAppointmentBinding

//    선택한 약속일시를 저장하는 Calendar 변수

    val mSelectedDatetimeCal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_appointment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

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

                    val sdf = SimpleDateFormat("a htl m분")

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

    }

    override fun setValues() {

    }
}