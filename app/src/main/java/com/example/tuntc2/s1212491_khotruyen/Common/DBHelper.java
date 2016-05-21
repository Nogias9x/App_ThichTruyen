package com.example.tuntc2.s1212491_khotruyen.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
    public static final String KEY_B_READING_CHAPTER = "ReadingChapter";
    public static final String KEY_B_READING_Y = "ReadingY";


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
        db.execSQL("create table IF NOT EXISTS Book (StoryID integer primary key, Title text,Author text, Cover text, ReadingChapter integer, ReadingY integer)");
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
        contentValues.put("ReadingChapter", 0);
        contentValues.put("ReadingY", 0);
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

//    public boolean insertAllChapters(List<Chapter> ChapterList)
//    {
//        //insert book's chapters
//        for(int i= 0; i<ChapterList.size(); i++){
//            Chapter c= ChapterList.get(i);
//            insertChapter(c.getmStoryId(), c.getmChapterId(), c.getmTitle(), c.getmContent());
//        }
//        return true;
//    }

    public List<Chapter> getAllChapters(int StoryID){
        List<Chapter> list= new ArrayList<Chapter>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Chapter where StoryID = "+ StoryID+ " ORDER BY `ChapterID` ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Chapter chap= new Chapter();
            chap.setmStoryId(res.getInt(res.getColumnIndex(KEY_C_STORYID)));
            chap.setmChapterId(res.getInt(res.getColumnIndex(KEY_C_CHAPTERID)));
            chap.setmTitle(res.getString(res.getColumnIndex(KEY_C_TITLE)));
            chap.setmContent(res.getString(res.getColumnIndex(KEY_C_CONTENT)));
            list.add(chap);
            res.moveToNext();
        }

        return list;
    }

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

    public Integer deleteBookAndItsChapter(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer a= db.delete("Book", "StoryID = " + id.toString(), null);
        Integer b= db.delete("Chapter", "StoryID = " + id.toString(), null);
        return b;
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
//            tempBook.setmReadingChapter(res.getInt(res.getColumnIndex(KEY_B_READING_CHAPTER)));
//            tempBook.setmReadingY(res.getInt(res.getColumnIndex(KEY_B_READING_Y)));
            array_list.add(tempBook);
            res.moveToNext();
        }
        return array_list;
    }


    public int getReadingChapter(Integer storyID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Book where StoryID = "+ storyID, null );
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex(KEY_B_READING_CHAPTER));
        }
        return 0;
    }
    public int getReadingY(Integer storyID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Book where StoryID = "+ storyID, null );
        res.moveToFirst();

        if(res.isAfterLast() == false){
            return res.getInt(res.getColumnIndex(KEY_B_READING_Y));
        }
        return 0;
    }

    public void setReadingPositon(Integer storyID, Integer readingChapter, Integer readingY){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_B_READING_CHAPTER, readingChapter);
        cv.put(KEY_B_READING_Y,readingY);

        db.update(BOOK_TABLE_NAME, cv, KEY_B_ID+ "= "+storyID, null);
    }
}