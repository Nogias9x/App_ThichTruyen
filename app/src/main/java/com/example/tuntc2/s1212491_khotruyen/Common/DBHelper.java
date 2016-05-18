package com.example.tuntc2.s1212491_khotruyen.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "KhoTruyen.db";

    public static final String BOOK_TABLE_NAME = "Book";
    public static final String KEY_B_ID = "StoryID";
    public static final String KEY_B_COVER = "Cover";
    public static final String KEY_B_AUTHOR = "Author";
    public static final String KEY_B_VIEW = "View";//
    public static final String KEY_B_DESCRIPTION = "Description";//
    public static final String KEY_B_CHAPTER = "Status";//
    public static final String KEY_B_TITLE = "Title";


    public static final String CHAPTER_TABLE_NAME = "Chapter";
    public static final String KEY_C_STORYID = "StoryID";
    public static final String KEY_C_CHAPTERID = "ChapterID";
    public static final String KEY_C_TITLE = "ChapterTitle";
    public static final String KEY_C_CONTENT = "ChapterContent";


//    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS Book (StoryID integer primary key, Title text,Author text, Cover text)");
        db.execSQL("create table IF NOT EXISTS Chapter (StoryID integer,ChapterID, ChapterTitle text, ChapterContent text, PRIMARY KEY ( StoryID, ChapterTitle))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Book");
        db.execSQL("DROP TABLE IF EXISTS Chapter");
        onCreate(db);
    }

    public boolean insertBook  (int StoryID, String Title, String Author, String Cover)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("StoryID", StoryID);
        contentValues.put("Title", Title);
        contentValues.put("Author", Author);
        contentValues.put("Cover", Cover);
        db.insertWithOnConflict("Book", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public boolean insertChapter (int StoryID, int ChapterID, String ChapterTitle, String ChapterContent)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("StoryID", StoryID);
        contentValues.put("ChapterID", ChapterID);
        contentValues.put("ChapterTitle", ChapterTitle);
        contentValues.put("ChapterContent", ChapterContent);
        db.insertWithOnConflict("Chapter", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

//    public boolean insertBook  (int StoryID, String Title, String Author, String Cover)
//    {
//
//        return true;
//    }

    public boolean checkIfExistBook(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from Book where StoryID = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int numberOfBook(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BOOK_TABLE_NAME);
        return numRows;
    }

    public Integer deleteBook (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean b1= checkIfExistBook(id);
        Integer i= db.delete("Book", "StoryID = " + id.toString(), null);
        boolean b2= checkIfExistBook(id);
        return i;
    }

    public ArrayList<Book> getAllBooks(){
        ArrayList<Book> array_list = new ArrayList<Book>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Book ORDER BY `Title` ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Book tempBook= new Book();
            tempBook.setId(res.getInt(res.getColumnIndex(KEY_B_ID)));
            tempBook.setTitle(res.getString(res.getColumnIndex(KEY_B_TITLE)));
            tempBook.setAuthor(res.getString(res.getColumnIndex(KEY_B_AUTHOR)));
            tempBook.setCoverUrl(res.getString(res.getColumnIndex(KEY_B_COVER)));

            array_list.add(tempBook);
            res.moveToNext();
        }
        return array_list;
    }

}