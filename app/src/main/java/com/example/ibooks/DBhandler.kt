package com.example.ibooks

import BookClass
import PivotClass
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


class DBhandler(context: Context): SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {
    companion object {
        private val DB_NAME = "Ibooks"

        private val DB_VERSION = 1

        private val TABLE_NAME = "books"

        private val ID_COL = "id"

        private val NAME_COL = "name"

        private val DESCRIPTION_COL = "description"

        private val ISBN_COL = "isbn"

        private val TABLE_PIVOT = "contacts_rent_books"

        private val PIVOT_ID_COL = "idcontacts_rent_books"

        private val ID_CONTACTS_COL = "id_contacts"

        private val CONTACT_NAME = "contact_name"

        private val RENT_DATE_COL = "rent_date"

        private val END_DATE_COL = "end_date"

        private val BOOKS_ID_COL = "books_idbooks"
    }

    // below method is for creating a database by running a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        var query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + ISBN_COL + " TEXT)")

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_PIVOT + " ("
                + PIVOT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID_CONTACTS_COL + " INT,"
                + RENT_DATE_COL + " TEXT,"
                + END_DATE_COL + " TEXT,"
                + BOOKS_ID_COL + " INTEGER,"
                + CONTACT_NAME + " TEXT,"
                + "FOREIGN KEY(" + BOOKS_ID_COL + ") REFERENCES artist(" + ID_COL + "))")

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query)
    }

    @SuppressLint("Recycle", "Range")
    fun addNewBook(
        bookName: String?,
        description: String?,
        isbn: String?,
        idContact: Int?,
        contact_name: String?,
        startDate: String?,
        endDate: String?,
    ) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        val db = this.writableDatabase

        // on below line we are creating a
        // variable for content values.
        val values = ContentValues()
        val valuesPivot = ContentValues()
        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, bookName)
        values.put(DESCRIPTION_COL, description)
        values.put(ISBN_COL, isbn)
        // after adding all values we are passing
        // content values to our table.

        var id: Long = db.insert(TABLE_NAME, null, values)
        valuesPivot.put(ID_CONTACTS_COL, idContact)
        valuesPivot.put(RENT_DATE_COL, startDate)
        valuesPivot.put(END_DATE_COL, endDate)
        valuesPivot.put(BOOKS_ID_COL,id)
        valuesPivot.put(CONTACT_NAME, contact_name)
        db.insert(TABLE_PIVOT, null, valuesPivot)
        // at last we are closing our
        // database after adding database.
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    @SuppressLint("Recycle", "Range")
    fun viewBooks():List<BookClass>{
        val empList:ArrayList<BookClass> = ArrayList<BookClass>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var Id: Int
        var bookName: String
        var description: String
        var isbn: String
        if (cursor.moveToFirst()) {
            do {

                Id = cursor.getInt(cursor.getColumnIndex("id"))
                bookName = cursor.getString(cursor.getColumnIndex("name"))
                description = cursor.getString(cursor.getColumnIndex("description"))
                isbn = cursor.getString(cursor.getColumnIndex("isbn"))
                val emp= BookClass(id = Id, bookName = bookName, description = description, isbn = isbn)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }
    //method to update data
    fun updateBook(emp: BookClass):Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(ID_COL, emp.id)
        values.put(NAME_COL, emp.bookName)
        values.put(DESCRIPTION_COL, emp.description)
        values.put(ISBN_COL, emp.isbn)

        // Updating Row
        val success = db.update(TABLE_NAME, values,"id="+emp.id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    @SuppressLint("Range", "Recycle")
    fun getPivot():List<PivotClass>{
        val empList:ArrayList<PivotClass> = ArrayList<PivotClass>()
        val selectQuery = "SELECT  * FROM $TABLE_PIVOT"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var Id: Int
        var startdate: String
        var endDate: String
        var contactName: String
        var idContact: Int
        var bookId: Int
        if (cursor.moveToFirst()) {
            do {

                Id = cursor.getInt(cursor.getColumnIndex("idcontacts_rent_books"))
                startdate = cursor.getString(cursor.getColumnIndex(RENT_DATE_COL))
                endDate = cursor.getString(cursor.getColumnIndex(END_DATE_COL))
                idContact = cursor.getInt(cursor.getColumnIndex(ID_CONTACTS_COL))
                bookId = cursor.getInt(cursor.getColumnIndex(BOOKS_ID_COL))
                contactName = cursor.getString(cursor.getColumnIndex(CONTACT_NAME))
                val emp= PivotClass(id = Id, startdate = startdate, enddate = endDate, contactId = idContact, booksId = bookId, contactName = contactName)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

}