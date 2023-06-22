package com.example.ibooks.activities

import BookClass
import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.camera2.CameraCharacteristics
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ibooks.MainActivity
import com.example.ibooks.db.DBhandler
import com.example.ibooks.R
import com.example.ibooks.databinding.ActivityAddBinding
import java.util.*


/*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */

/*
 * Defines an array that contains resource ids for the layout views
 * that get the Cursor column contents. The id is pre-defined in
 * the Android framework, so it is prefaced with "android.R.id"
 */


class AddActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddBinding
    @RequiresApi(Build.VERSION_CODES.N)
    var cal = Calendar.getInstance()
    var updateMode = false
    var bookid = 0
    //contact permission code
    private val CONTACT_PERMISSION_CODE = 1

    //contact pick code
    private val CONTACT_PICK_CODE = 2
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        binding = ActivityAddBinding.inflate(layoutInflater)
        val view = binding.root
        val bottomAppBar = binding.bottomAppBar
        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    startActivity(Intent(this, AddActivity::class.java))
                    true
                }

                else -> false
            }
        }

        val extras = intent.extras
        if (extras != null) {
            updateMode = extras.getBoolean("update")
            bookid = extras.getInt("bookid")
        }
        val dbHandler = DBhandler(this@AddActivity)
        if(updateMode){
            val book = dbHandler.getABook(bookid)
            val pivot = dbHandler.getAPivotFromBook(bookid)
            if(book != null) {
                binding.bookName.setText(book.bookName)
                binding.description.setText(book.description)
                binding.isbn.setText(book.isbn)
            }
            if(pivot != null){
                binding.datepickerstart.setText(pivot.startdate)
                binding.datepickerend.setText(pivot.enddate)
            }
        }

        binding.floatingButton.setOnClickListener { openCamera() }
        bottomAppBar.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        // below line is to add on click listener for our add course button.

        // below line is to add on click listener for our add course button.
        binding.addButton.setOnClickListener(View.OnClickListener { // below line is to get data from all edit text fields.
            var bookName: String = binding.bookName.text.toString()
            var description: String = binding.description.text.toString()
            var isbn: String = binding.isbn.text.toString()
            // validating if the text fields are empty or not.
            if (bookName.isEmpty() && description.isEmpty() && isbn.isEmpty()) {
                Toast.makeText(this@AddActivity, "Please enter all the data..", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            // on below line we are calling a method to add new
            // course to sqlite data and pass all our values to it.
            // after adding the data we are displaying a toast message.
            Toast.makeText(this@AddActivity, "Book has been added.", Toast.LENGTH_SHORT).show()
            bookName = ""
            description = ""
            isbn = ""

            if (checkContactPermission()){
                //allowed
                pickContact()
            }
            else{
                //not allowed, request
                requestContactPermission()
            }
        })
        val dateSetListenerStart =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        val dateSetListenerEnd =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInViewEnd()
            }
        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        binding.datepickerstart.setOnClickListener {
            DatePickerDialog(
                this@AddActivity,
                dateSetListenerStart,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.datepickerend.setOnClickListener {
            DatePickerDialog(
                this@AddActivity,
                dateSetListenerEnd,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        binding.takeButton.setOnClickListener {
            if(updateMode) {
                val db = DBhandler(this@AddActivity)
                db.deleteBook(binding.bookName.text.toString())
            }
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
        setContentView(view)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.JAPAN)
        binding.datepickerstart.setText(sdf.format(cal.time))
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInViewEnd() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf =SimpleDateFormat (myFormat, Locale.JAPAN)
        binding.datepickerend.setText(sdf.format(cal.time))
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

    private fun checkContactPermission(): Boolean{
        //check if permission was granted/allowed or not, returns true if granted/allowed, false if not
        return  ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermission(){
        //request the READ_CONTACTS permission
        val permission = arrayOf(Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permission, CONTACT_PERMISSION_CODE)
    }

    private fun pickContact(){
        //intent ti pick contact
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //handle permission request results || calls when user from Permission request dialog presses Allow or Deny
        if (requestCode == CONTACT_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted, can pick contact
                pickContact()
            }
            else{
                //permission denied, cann't pick contact, just show message
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //handle intent results || calls when user from Intent (Contact Pick) picks or cancels pick contact
        if (resultCode == RESULT_OK){
            //calls when user click a contact from contacts (intent) list
            if (requestCode == CONTACT_PICK_CODE){
                var text = ""

                val cursor1: Cursor
                val cursor2: Cursor?

                //get data from intent
                val uri = data!!.data
                cursor1 = contentResolver.query(uri!!, null, null, null, null)!!
                if (cursor1.moveToFirst()){
                    val dbHandler = DBhandler(this@AddActivity)
                    val bookName: String = binding.bookName.text.toString()
                    val description: String = binding.description.text.toString()
                    val isbn: String = binding.isbn.text.toString()
                    val startDate: String = binding.datepickerstart.text.toString()
                    val endDate: String = binding.datepickerend.text.toString()
                    //get contact details
                    val contactId = cursor1.getInt(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    val contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    if(updateMode){

                        val book = BookClass(bookid,bookName, description, isbn)
                        dbHandler.updateBook(book,
                            contact_name = contactName,
                            idContact = contactId,
                            startDate = startDate,
                            endDate = endDate)
                    }else {
                        dbHandler.addNewBook(
                            bookName,
                            description,
                            isbn,
                            contact_name = contactName,
                            idContact = contactId,
                            startDate = startDate,
                            endDate = endDate
                        )

                    }
                    cursor1.close()
                }
            }
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
        else{
            //cancelled picking contact
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}
