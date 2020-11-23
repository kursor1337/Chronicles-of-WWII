package com.kypcop.chroniclesofwwii.game.connection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.kypcop.chroniclesofwwii.R

class HostsAdapter(private val mContext: Context, private val mHostList: List<Host>) : ArrayAdapter<Host>(mContext, R.layout.listview_missions, mHostList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(mContext).inflate(R.layout.listview_missions, LinearLayout(mContext))
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = mHostList[position].name
        return view
    }
}