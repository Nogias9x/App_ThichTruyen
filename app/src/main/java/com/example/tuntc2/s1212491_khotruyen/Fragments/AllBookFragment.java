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
import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.DBHelper;
import com.example.tuntc2.s1212491_khotruyen.Common.MyApplication;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_allbook, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setEmptyView(view.findViewById(R.id.empty));
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        getAllBooksTask();
        return view;
    }

    private void getAllBooksTask() {
        new AsyncTask<Void, Void, List<Book>>() {
            @Override
            protected void onPreExecute() {
                Dialog = new ProgressDialog(mContext);
                Dialog.setMessage(getString(R.string.progress_msg));
                Dialog.show();
            }

            @Override
            protected List<Book> doInBackground(Void... voids) {
                return getAllBooks("http://wsthichtruyen-1212491.rhcloud.com/?function=0");
            }

            @Override
            protected void onPostExecute(List<Book> list) {
                if (list != null) {
                    mBooks = list;
                    BookListAdapter adapter = new BookListAdapter(getActivity(), mBooks);
                    mListView.setAdapter(adapter);
                }
                //DialogManager.closeDialog(0);
                Dialog.dismiss();
            }
        }.execute();
    }

    private List<Book> getAllBooks(String path) {
        Context mContext;
        String Content = null;
        String Error = null;
        ProgressDialog Dialog;
        String data = "";
        List<Book> mBookList = null;

        BufferedReader reader = null;
        try {
            URL url = new URL(path);//"http://wsthichtruyen-1212491.rhcloud.com/?function=0");

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + " ");
            }

            // Append Server Response To Content String
            Content = sb.toString();
        } catch (Exception ex) {
            Error = ex.getMessage();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                Log.i("<<NOGIAS>>", ex.toString());
            }
        }

        if (Error != null) {
            Log.i("<<NOGIAS>>", Error);
        } else {
            /****************** Start Parse Response JSON Data *************/
            JSONArray jsonArray;
            try {
                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                Content = Html.fromHtml(Content).toString();
                jsonArray = new JSONArray(Content);//
//                /*********** Process each JSON Node ************///
                int lengthJsonArr = jsonArray.length();
                mBookList = new ArrayList<Book>(lengthJsonArr);
                for (int i = 0; i < lengthJsonArr; i++) {
//                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonArray.getJSONObject(i);
//
                    Book book = new Book();
                    book.setId(jsonChildNode.optInt(Book.KEY_ID));
                    book.setAuthor(jsonChildNode.optString(Book.KEY_AUTHOR).toString());
                    book.setCoverUrl(jsonChildNode.optString(Book.KEY_COVER).toString());
                    book.setChapter(jsonChildNode.optString(Book.KEY_CHAPTER).toString());
                    book.setDescription(jsonChildNode.optString(Book.KEY_DESCRIPTION).toString());
                    book.setTitle(jsonChildNode.optString(Book.KEY_TITLE).toString());
                    book.setView(jsonChildNode.optInt(Book.KEY_VIEW));
                    mBookList.add(book);
                }
            } catch (JSONException e) {
                Log.i("<<NOGIAS>>", e.toString());
            }
        }
        return mBookList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent detailIntent= new Intent(mContext, DetailActivity.class);
        detailIntent.putExtra("selectedBook", mBooks.get(position));
        mContext.startActivity(detailIntent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//        Toast.makeText(getActivity(), "onItemLongClick", Toast.LENGTH_SHORT).show();

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
                            ((MyApplication) mContext.getApplication()).setmLocalDatabase(db);
                            Toast.makeText(mContext,mBooks.get(position).getTitle().toUpperCase()+" đã được thêm vào TRUYỆN CỦA TÔI..." , Toast.LENGTH_SHORT).show();
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

