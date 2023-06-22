package com.example.ibooks

import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.ibooks.adapters.BooksAdapter
import com.example.ibooks.db.DBhandler
import com.example.ibooks.activities.AddActivity
import com.example.ibooks.databinding.ActivityMainBinding


/*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */

/*
 * Defines an array that contains resource ids for the layout views
 * that get the Cursor column contents. The id is pre-defined in
 * the Android framework, so it is prefaced with "android.R.id"
 */


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val REQUEST_IMAGE_CAPTURE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        val bottomAppBar = binding.bottomAppBar
        val dbHandler = DBhandler(this@MainActivity)

        val books = dbHandler.viewBooks()
        val pivot = dbHandler.getPivot()
        val names = ArrayList<String>()
        val descriptions = ArrayList<String>()
        val startdates = ArrayList<String>()
        val enddates = ArrayList<String>()
        val users = ArrayList<String>()
        val booksid =  ArrayList<Int>()
        val ids = ArrayList<Int>()
        pivot.forEach{
            startdates.add(it.startdate)
            enddates.add(it.enddate)
            booksid.add(it.booksId)
            users.add(it.contactName)
        }
        books.forEach {
            ids.add(it.id)
            names.add(it.bookName)
            descriptions.add(it.description)
        }
        val myListAdapter = BooksAdapter(this,ids.toTypedArray(),names.toTypedArray(),startdates.toTypedArray(),enddates.toTypedArray(), users.toTypedArray(), booksid.toTypedArray(),descriptions.toTypedArray())

        binding.listview.adapter = myListAdapter

        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    startActivity(Intent(this, AddActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.floatingButton.setOnClickListener { openCamera() }
        bottomAppBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
        setContentView(view)


    }

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT)  // Tested on API 24 Android version 7.0(Samsung S6)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT) // Tested on API 27 Android version 8.0(Nexus 6P)
                cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            }
            else -> cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)  // Tested API 21 Android version 5.0.1(Samsung S4)
        }
        startActivityForResult(cameraIntent, 0)
    }

}
