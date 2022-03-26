package com.example.keepthetime_220312.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keepthetime_220312.R
import com.example.keepthetime_220312.adapters.RequestedFriendRecyclerAdapter
import com.example.keepthetime_220312.databinding.FragmentRequestedUserListBinding
import com.example.keepthetime_220312.datas.BasicResponse
import com.example.keepthetime_220312.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestedUserListFragment : BaseFragment() {

    lateinit var binding: FragmentRequestedUserListBinding
    val mRequestedFriendList = ArrayList<UserData>()
    lateinit var mAdapter: RequestedFriendRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_requested_user_list,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupEvents()
        setValues()

    }


    override fun setupEvents() {

    }

    override fun setValues() {
        getRequestFriendListFromServer()

        mAdapter = RequestedFriendRecyclerAdapter(mContext,mRequestedFriendList)
        binding.requestFriendRecyclerView.adapter = mAdapter
        binding.requestFriendRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }

    fun getRequestFriendListFromServer() {
        apiList.getRequestFriendList(
            "requested"
        ).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val br = response.body()!!
                    mRequestedFriendList.addAll(br.data.friends)

                    mAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }

}