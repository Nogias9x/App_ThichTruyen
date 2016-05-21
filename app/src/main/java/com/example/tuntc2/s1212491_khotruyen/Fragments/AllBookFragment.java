package com.example.tuntc2.s1212491_khotruyen.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tuntc2.s1212491_khotruyen.Activites.DetailActivity;
import com.example.tuntc2.s1212491_khotruyen.Activites.MainActivity;
import com.example.tuntc2.s1212491_khotruyen.Adapters.BookListAdapter;
import com.example.tuntc2.s1212491_khotruyen.Adapters.ChapterListAdapter;
import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.Chapter;
import com.example.tuntc2.s1212491_khotruyen.Common.DBHelper;
import com.example.tuntc2.s1212491_khotruyen.Common.MyApplication;
import com.example.tuntc2.s1212491_khotruyen.Progress.LongOperation;
import com.example.tuntc2.s1212491_khotruyen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class AllBookFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private List<Book> mBooks = null;
    private ListView mListView;
    private MainActivity mContext;
    private ProgressDialog Dialog;
    private List<Chapter> mChapters = null;
    LongOperation mLongOperation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_allbook, container, false);


        mLongOperation= new LongOperation(mContext);

        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setEmptyView(view.findViewById(R.id.empty));

        if(!mContext.isOnline()){
            Toast.makeText(mContext, R.string.msg_no_internet, Toast.LENGTH_SHORT).show();
            return view;
        }

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mLongOperation.getAllBooksTask(this, mListView);

        return view;
    }

    public void setmBooks(List<Book> mBooks) {
        this.mBooks = mBooks;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent detailIntent= new Intent(mContext, DetailActivity.class);
        detailIntent.putExtra("selectedBook", mBooks.get(position));
        mContext.startActivity(detailIntent);
        Toast.makeText(mContext, String.valueOf(mBooks.get(position).getRating()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DBHelper db= ((MyApplication) mContext.getApplication()).getmLocalDatabase();

                        if(db.checkIfExistBook( mBooks.get(position).getId())==true){
                            Toast.makeText(mContext,mBooks.get(position).getTitle().toUpperCase()+" đã tồn tại" , Toast.LENGTH_SHORT).show();
                        }else {
                            db.insertBook(mBooks.get(position).getId(), mBooks.get(position).getTitle(), mBooks.get(position).getAuthor(), mBooks.get(position).getCoverUrl());

                            //tải và thêm các chapter của book xuống local database
                            mLongOperation.getAllChaptersTask(mBooks.get(position).getId());

                            ((MyApplication) mContext.getApplication()).setmLocalDatabase(db);

                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        //////////
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có muốn thêm truyện " + mBooks.get(position).getTitle().toUpperCase()+ " vào TRUYỆN CỦA TÔI không?")
                .setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
        return true;
    }

}

