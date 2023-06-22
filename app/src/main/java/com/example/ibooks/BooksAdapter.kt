package com.example.ibooks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

class BooksAdapter(private val context: Activity, private val ids: Array<Int>, private val bookName: Array<String>, private val startDates: Array<String>, private val endDates: Array<String>, private val users: Array<String>, private val booksid: Array<Int>, private val description: Array<String>)
    : ArrayAdapter<String>(context, R.layout.row_item, bookName) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.row_item, null, true)
        val titleText = rowView.findViewById(R.id.bookName) as TextView
        val subtitleText = rowView.findViewById(R.id.description) as TextView
        val startDate = rowView.findViewById(R.id.datestart) as TextView
        val endDate = rowView.findViewById(R.id.dateend) as TextView
        val user = rowView.findViewById(R.id.user) as TextView
        val mIntent = Intent(context, AddActivity::class.java)

        titleText.text = bookName[position]
        subtitleText.text = description[position]
        if(ids[position] == booksid[position]){
            startDate.text = startDates[position]
            endDate.text = endDates[position]
            user.text = users[position]

        }
        mIntent.putExtra("update", true)
        mIntent.putExtra("bookid", booksid[position])
        val extras: Bundle? = mIntent.extras
        rowView.setOnClickListener { startActivity(context,mIntent, extras) }
        return rowView
    }
}