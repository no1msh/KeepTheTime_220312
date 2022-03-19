package com.example.keepthetime_220312

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.example.keepthetime_220312.adapters.MainViewPager2Adapter
import com.example.keepthetime_220312.adapters.MainViewPagerAdapter
import com.example.keepthetime_220312.databinding.ActivityMainBinding
import com.example.keepthetime_220312.datas.BasicResponse
import com.example.keepthetime_220312.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    lateinit var mvp2a : MainViewPager2Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this,R.layout.activity_main)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {
//    바텀 네비게이션의 이벤트 처리.

        binding.mainBottomNav.setOnItemSelectedListener {

//            it 변수 : 선택된 메뉴가 뭔지 알려줌.

            binding.mainViewPager2.currentItem = when(it.itemId) {
                R.id.home -> 0
                else -> 1
            }

            return@setOnItemSelectedListener true
        }

//    페이지 이동시 > 바텀네비게이션 메뉴 선택

        binding.mainViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

//            추상 메소드가 아니어서 이벤트 처리 함수를 직접 오버라이딩 해야합니다.
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.mainBottomNav.selectedItemId = when(position) {
                    0 -> R.id.home
                    else -> R.id.profile
                }
            }
        })
    }

    override fun setValues() {
        mvp2a = MainViewPager2Adapter(this)

        binding.mainViewPager2.adapter = mvp2a

//        상속받은 imgBack 숨김처리

        imgBack.visibility = View.GONE
    }


}