package com.example.keepthetime_220312.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.keepthetime_220312.R
import com.example.keepthetime_220312.datas.StartingPointData

class StartingPointSpinnerAdapter(
    val mContext : Context,
    resId : Int,
    val mList : ArrayList<StartingPointData>,
) : ArrayAdapter<StartingPointData>(mContext,resId,mList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var tempRow = convertView

        if (tempRow == null) {
            tempRow = LayoutInflater.from(mContext).inflate(R.layout.starting_point_list_item, null)
        }

        val row = tempRow!!

        val data = mList[position]

        val txtStartingPointName = row.findViewById<TextView>(R.id.txtStartingPointName)
        val txtPrimary = row.findViewById<TextView>(R.id.txtPrimary)

        txtStartingPointName.text = data.name

        if(data.is_primary) {
            txtPrimary.visibility = View.VISIBLE
        }
        else {
            txtPrimary.visibility = View.GONE
        }

        return row
    }
}