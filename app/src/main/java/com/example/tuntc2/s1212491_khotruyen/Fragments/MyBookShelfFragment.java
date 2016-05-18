package com.example.tuntc2.s1212491_khotruyen.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tuntc2.s1212491_khotruyen.Activites.MainActivity;
import com.example.tuntc2.s1212491_khotruyen.Adapters.BookGridAdapter;
import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.DBHelper;
import com.example.tuntc2.s1212491_khotruyen.Common.MyApplication;
import com.example.tuntc2.s1212491_khotruyen.R;

import java.util.List;

public class MyBookShelfFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{//, Animation.AnimationListener {
    private List<Book> mBooks;
    private DBHelper mLocalDatabase;
    private GridView mGridView;
    private int mPosition = 0;
    private MainActivity mContext;
    private ProgressDialog Dialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("--", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("--", "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("--", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("--", "onCreateView");
        mContext = (MainActivity) getActivity();
        mLocalDatabase =  ((MyApplication)mContext.getApplication()).getmLocalDatabase();


        View view = inflater.inflate(R.layout.fragment_myshelf, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setEmptyView(view.findViewById(R.id.empty));
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayMyBooks();
    }

    private void displayMyBooks() {
        new AsyncTask<Void, Void, List<Book>>() {

            @Override
            protected void onPreExecute() {
                Dialog = new ProgressDialog(mContext);
                Dialog.setMessage(getString(R.string.progress_msg));
                Dialog.show();
            }

            @Override
            protected List<Book> doInBackground(Void... voids) {
                Log.i("<<NOGIAS>>", "doInBackground getAllBooks");
                return mLocalDatabase.getAllBooks();
            }

            @Override
            protected void onPostExecute(List<Book> list) {
                mBooks = list;
                if (list != null) {
                    mGridView.setAdapter(new BookGridAdapter(getActivity(), list));
                }
                Dialog.dismiss();
            }
        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        AllChapterFragment fragment = null;
        fragment= new AllChapterFragment();
        fragment.setStoryID(mBooks.get(position).getId());
        fragment.setStoryTitle(mBooks.get(position).getTitle());
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("--", "Ondestroy");
        ((MyApplication)getActivity().getApplication()).setmLocalDatabase(mLocalDatabase);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("--", "OndestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("--", "onDetach");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBHelper db = ((MyApplication) mContext.getApplication()).getmLocalDatabase();
                        db.deleteBook(mBooks.get(position).getId());
                        ((MyApplication) mContext.getApplication()).setmLocalDatabase(db);
                        Toast.makeText(mContext, mBooks.get(position).getTitle().toUpperCase() + " đã được xoá khỏi TRUYỆN CỦA TÔI...", Toast.LENGTH_SHORT).show();
                        refresh();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getActivity(), "NO", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bạn có muốn xoá truyện " + mBooks.get(position).getTitle().toUpperCase() + " khỏi TRUYỆN CỦA TÔI không?")
                .setPositiveButton("Có", dialogClickListener)
                .setNegativeButton("Không", dialogClickListener).show();
        return true;
    }

    public void refresh() {
        Fragment fragment = new MyBookShelfFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
