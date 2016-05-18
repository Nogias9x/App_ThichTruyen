package com.example.tuntc2.s1212491_khotruyen.Activites;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.DBHelper;
import com.example.tuntc2.s1212491_khotruyen.Common.MyApplication;
import com.example.tuntc2.s1212491_khotruyen.Common.Utils;
import com.example.tuntc2.s1212491_khotruyen.R;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView coverIv;
    private TextView titleTv;
    private TextView authorTv;
    private TextView viewTv;
    private TextView chapterTv;
    private TextView introTv;

    private ImageView bookshelfIv;
    private ImageView readIv;
    private ImageView shareIv;
    private RatingBar ratingRb;

    private Book mBook;
    private DBHelper mDb;
    private boolean mIsMine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = new DBHelper(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_detail);

        coverIv = (ImageView) findViewById(R.id.cover_iv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        authorTv = (TextView) findViewById(R.id.author_tv);
        viewTv = (TextView) findViewById(R.id.view_tv);
        chapterTv = (TextView) findViewById(R.id.chapter_tv);
        bookshelfIv = (ImageView) findViewById(R.id.add_remove_my_bookshelf_iv);
        readIv = (ImageView) findViewById(R.id.read_iv);
        shareIv = (ImageView) findViewById(R.id.share_iv);
        ratingRb = (RatingBar) findViewById(R.id.rating_rb);
        introTv = (TextView) findViewById(R.id.intro_tv);

        ratingRb.setVisibility(View.VISIBLE);
        bookshelfIv.setEnabled(true);

        bookshelfIv.setOnClickListener(this);
        readIv.setOnClickListener(this);
        shareIv.setOnClickListener(this);


        Intent callerIntent = getIntent();
        mBook = (Book) callerIntent.getSerializableExtra("selectedBook");

        if (mBook != null) {
            coverIv.setImageResource(R.drawable.loading);
            coverIv.setTag(mBook.getCoverUrl());
            Utils.loadImageFromUrl(this, coverIv, mBook.getCoverUrl());
            titleTv.setText(mBook.getTitle());
            authorTv.setText(mBook.getAuthor());
            introTv.setText(mBook.getDescription());
            viewTv.setText(getString(R.string.book_view) + mBook.getView());
            chapterTv.setText(mBook.getChapter());
        }
////

        mIsMine = ((MyApplication) getApplication()).getmLocalDatabase().checkIfExistBook(mBook.getId());

        changeBookStatus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_iv:
                startShareActivity();
                break;
            case R.id.read_iv:
                Intent intent = new Intent(this, ViewerActivity.class);
                intent.putExtra("BookTitle", mBook.getTitle());
                intent.putExtra("BookID", mBook.getId());
                intent.putExtra("ChapterID", 0);
                intent.putExtra("Style", mBook.STYLE_ONLINE);
                startActivity(intent);
                break;
            case R.id.add_remove_my_bookshelf_iv:
                if (mIsMine) {
                    removeBookFromBookshelf();
                } else {
                    addToBookshelf();
                }
                break;
            default:
                super.onClick(view);
                return;
        }
    }

    private void startShareActivity() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mBook.getTitle() + getString(R.string.share_content));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }


    private void addToBookshelf() {
        DBHelper db = ((MyApplication) getApplication()).getmLocalDatabase();
        db.insertBook(mBook.getId(), mBook.getTitle(), mBook.getAuthor(), mBook.getCoverUrl());
        ((MyApplication) getApplication()).setmLocalDatabase(db);
        mIsMine = true;
        changeBookStatus();
        Toast.makeText(this, mBook.getTitle().toUpperCase() + " đã được thêm vào TRUYỆN CỦA TÔI...", Toast.LENGTH_SHORT).show();
    }

    private void removeBookFromBookshelf() {
        DBHelper db = ((MyApplication) getApplication()).getmLocalDatabase();
        db.deleteBook(mBook.getId());
        ((MyApplication) getApplication()).setmLocalDatabase(db);
        mIsMine = false;
        changeBookStatus();
        Toast.makeText(this, mBook.getTitle().toUpperCase() + " đã được xoá khỏi TRUYỆN CỦA TÔI...", Toast.LENGTH_SHORT).show();
    }

    public void changeBookStatus() {
        if (mIsMine) {
            bookshelfIv.setBackgroundResource(R.drawable.add_book_icon);
        } else {
            bookshelfIv.setBackgroundResource(R.drawable.remove_book_icon);
        }
    }
}
