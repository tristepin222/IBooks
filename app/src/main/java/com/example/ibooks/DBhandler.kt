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


class DBhandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
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

        var query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + ISBN_COL + " TEXT)")


        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_PIVOT + " ("
                + PIVOT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID_CONTACTS_COL + " INT,"
                + RENT_DATE_COL + " TEXT,"
                + END_DATE_COL + " TEXT,"
                + BOOKS_ID_COL + " INTEGER,"
                + CONTACT_NAME + " TEXT,"
                + "FOREIGN KEY(" + BOOKS_ID_COL + ") REFERENCES artist(" + ID_COL + "))")


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


        val db = this.writableDatabase

        val values = ContentValues()
        val valuesPivot = ContentValues()

        values.put(NAME_COL, bookName)
        values.put(DESCRIPTION_COL, description)
        values.put(ISBN_COL, isbn)


        var id: Long = db.insert(TABLE_NAME, null, values)
        valuesPivot.put(ID_CONTACTS_COL, idContact)
        valuesPivot.put(RENT_DATE_COL, startDate)
        valuesPivot.put(END_DATE_COL, endDate)
        valuesPivot.put(BOOKS_ID_COL, id)
        valuesPivot.put(CONTACT_NAME, contact_name)
        db.insert(TABLE_PIVOT, null, valuesPivot)

        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    @SuppressLint("Recycle", "Range")
    fun viewBooks(): List<BookClass> {
        val empList: ArrayList<BookClass> = ArrayList<BookClass>()
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
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
                val emp =
                    BookClass(id = Id, bookName = bookName, description = description, isbn = isbn)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    //method to update data
    fun updateBook(
        book: BookClass,
        idContact: Int?,
        contact_name: String?,
        startDate: String?,
        endDate: String?,
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        val valuesPivot = ContentValues()
        values.put(ID_COL, book.id)
        values.put(NAME_COL, book.bookName)
        values.put(DESCRIPTION_COL, book.description)
        values.put(ISBN_COL, book.isbn)
        valuesPivot.put(ID_CONTACTS_COL, idContact)
        valuesPivot.put(RENT_DATE_COL, startDate)
        valuesPivot.put(END_DATE_COL, endDate)
        valuesPivot.put(BOOKS_ID_COL, book.id)
        valuesPivot.put(CONTACT_NAME, contact_name)
        // Updating Row
        val success = db.update(TABLE_NAME, values, "id=" + book.id, null)
        db.update(TABLE_PIVOT, valuesPivot, "$BOOKS_ID_COL=" + book.id, null)
        db.close() // Closing database connection
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getPivot(): List<PivotClass> {
        val empList: ArrayList<PivotClass> = ArrayList<PivotClass>()
        val selectQuery = "SELECT  * FROM $TABLE_PIVOT"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
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
                val emp = PivotClass(
                    id = Id,
                    startdate = startdate,
                    enddate = endDate,
                    contactId = idContact,
                    booksId = bookId,
                    contactName = contactName
                )
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }

    @SuppressLint("Range", "Recycle")
    fun getABook(id: Int): BookClass? {
        var book: BookClass? = null
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE id = $id"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
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
                book =
                    BookClass(id = Id, bookName = bookName, description = description, isbn = isbn)
            } while (cursor.moveToNext())
        }
        return book
    }

    @SuppressLint("Range", "Recycle")
    fun getAPivotFromBook(id: Int): PivotClass? {
        var pivot: PivotClass? = null
        val selectQuery = "SELECT * FROM $TABLE_PIVOT WHERE $BOOKS_ID_COL = $id"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
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
                pivot = PivotClass(
                    id = Id,
                    startdate = startdate,
                    enddate = endDate,
                    contactId = idContact,
                    booksId = bookId,
                    contactName = contactName
                )
            } while (cursor.moveToNext())
        }
        return pivot
    }
    @SuppressLint("Range", "Recycle")
    fun deleteBook(name: String) {
        val db = this.writableDatabase
        var values = ContentValues()
        //french sentences like "j'adore les frite" has a single quote, which can cause problem with sqlite queries
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $NAME_COL =" + "\"$name\""
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var id: Int = 1
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    id = cursor.getInt(cursor.getColumnIndex("id"))
                } while (cursor.moveToNext())
            }
        }

        val stringid = id.toString()
        db.delete(TABLE_NAME,"$ID_COL=?", Array(1){stringid})
        db.delete(TABLE_PIVOT,"$BOOKS_ID_COL=?", Array(1){stringid})
    }
}