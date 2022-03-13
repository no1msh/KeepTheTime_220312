package com.example.keepthetime_220312

import android.os.Bundle
import android.service.autofill.UserData
import androidx.databinding.DataBindingUtil
import com.example.keepthetime_220312.databinding.ActivityManageFriendListBinding

class ManageFriendListActivity : BaseActivity() {

    lateinit var binding: ActivityManageFriendListBinding

    var mMyFriendList = ArrayList<UserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_friend_list)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

//        내 친구목록에 데이터 채우기.
//         => 서버가 실제로 내려주는 친구목록을 채워보자. (API 통신과 결합)
    }
}