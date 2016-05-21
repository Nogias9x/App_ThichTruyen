package com.example.tuntc2.s1212491_khotruyen.Activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.Chapter;
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

public class ViewerActivity extends BaseActivity implements View.OnClickListener{

    private ScrollView mScrollView;
    private RelativeLayout mLayout;
    private TextView mChapterTitle;
    private TextView mChapterContent;
    private Button mPrevChapter;
    private Button mNextChapter;
    private ArrayList<String> mTitleArray= new ArrayList<String>();
    private ArrayList<String> mContentArray= new ArrayList<String>();
    private int mPosition;
    private String mBookTitle;
    private int mReadStyle;
    private Book mBook;
    private Chapter mChapter;
    private ProgressDialog mDialog;
    private int mBookID;
    private int chapterID;
    private int mChapterID;
    private int mReadingY;
    private DBHelper mLocalDatabase;

    Handler mHandler= new Handler();
    AutoScrollOperation mScrollOperation= new AutoScrollOperation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        mLocalDatabase= ((MyApplication)getApplication()).getmLocalDatabase();

        getActionBar().setDisplayShowTitleEnabled(true);
        mScrollView= (ScrollView) findViewById(R.id.viewer_scrollView);
        mLayout= (RelativeLayout) findViewById(R.id.viewer_layout);
        mChapterTitle= (TextView) findViewById(R.id.viewer_chapterTitle_tv);
        mChapterContent= (TextView) findViewById(R.id.viewer_chapterContent_tv);
        mPrevChapter= (Button) findViewById(R.id.viewer_prev_btn);
        mNextChapter= (Button) findViewById(R.id.viewer_next_btn);

        mChapterTitle.setTextColor(getResources().getColor(R.color.viewer_chapter_title));
        mPrevChapter.setVisibility(View.INVISIBLE);
        mNextChapter.setVisibility(View.INVISIBLE);
        mPrevChapter.setOnClickListener(this);
        mNextChapter.setOnClickListener(this);
        mChapterContent.setOnClickListener(this);
        mScrollView.scrollTo(0,0);

        loadPreferences();

        setContentStyle();

        mChapter= new Chapter();
        Intent callerIntent= getIntent();
        mBookTitle= callerIntent.getStringExtra("BookTitle");
        mReadStyle= callerIntent.getIntExtra("Style", mBook.STYLE_ONLINE);

        if(mReadStyle==mBook.STYLE_OFFLINE){
            mPosition= callerIntent.getIntExtra("position", -1);
            mTitleArray= callerIntent.getStringArrayListExtra("titleArray");
            mContentArray= callerIntent.getStringArrayListExtra("contentArray");
            mChapterID= callerIntent.getIntExtra("ChapterID", 0);//
            mReadingY= callerIntent.getIntExtra("ReadingY", 0);//
            Toast.makeText(this, "mReadingY:"+mReadingY, Toast.LENGTH_SHORT).show();

            changeChapter(mPosition, mReadingY);
            Toast.makeText(this, "HERE", Toast.LENGTH_SHORT).show();
//            mScrollView.scrollTo(0, mReadingY);

        } else if(mReadStyle==mBook.STYLE_ONLINE){
            Intent callerIntent1= getIntent();
            mBookID = callerIntent1.getIntExtra("BookID", -1);
            chapterID= callerIntent1.getIntExtra("ChapterID", -1);
            if(mBookID ==-1 || chapterID==-1) {
                Toast.makeText(this, "Nội dung này không thể đọc!", Toast.LENGTH_SHORT).show();
                return;
            }
            getChapterTask(mBookID, chapterID);
        }
    }

    public void changeChapter(int chapter, final int y){
        this.mPosition= chapter;

        if(mPosition==-1){
            Toast.makeText(this, "Chương được chọn không tồn tại!!!", Toast.LENGTH_LONG).show();
            return;
        }

        mPrevChapter.setVisibility(View.VISIBLE);
        mNextChapter.setVisibility(View.VISIBLE);
        if (mPosition == 0) mPrevChapter.setVisibility(View.INVISIBLE);
        if (mPosition >= mTitleArray.size()-1) mNextChapter.setVisibility(View.INVISIBLE);

        getActionBar().setTitle(mBookTitle+" - "+ mTitleArray.get(mPosition));

        mChapterTitle.setText(mTitleArray.get(mPosition));
        mChapterContent.setText(mContentArray.get(mPosition));

        //
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, y);
            }
        });
//        mScrollView.scrollTo(0,y);
    }

    @Override
    public void onClick(View v) {
        int position=-1;
        if(mReadStyle== mBook.STYLE_ONLINE) position= mPosition;
        switch (v.getId()){
            case R.id.viewer_prev_btn:
                position= mPosition-1;
                break;
            case R.id.viewer_next_btn:
                position= mPosition+1;
                break;
            case R.id.viewer_chapterContent_tv:
                Log.i("<<","viewer_chapterContent_tv > onclick");
                if(mScrollOperation.isScrolling()){
                    Log.i("<<","viewer_chapterContent_tv > onclick while scroll");
                    mScrollOperation.setmIsScrolling(false);
                }else {
                    Log.i("<<","viewer_chapterContent_tv > onclick while stop");
                    mScrollOperation = new AutoScrollOperation();
                    mScrollOperation.setmIsScrolling(true);
                    mScrollOperation.execute(((MyApplication)getApplication()).getmCurrentReadMode());
                }
                return;
            default:
                super.onClick(v);
                setContentStyle();
                return;
        }

        if(mReadStyle== mBook.STYLE_OFFLINE) {
            if (position < 0 || position >= mTitleArray.size()) {
                Toast.makeText(this, "Không thể chuyển chương", Toast.LENGTH_SHORT).show();
                return;
            }
            changeChapter(position, 0);
            return;
        }else if(mReadStyle== mBook.STYLE_ONLINE){
            if (position < 0) {
                Toast.makeText(this, "Không thể chuyển chương", Toast.LENGTH_SHORT).show();
                return;
            }
            chapterID= position;
            mChapter= null;
            getChapterTask(mBookID, position);
        }
    }

    private class AutoScrollOperation extends AsyncTask<Integer, Integer, Void> {
        private int mScrollSpeed;
        private boolean mIsScrolling;

        public void setmIsScrolling(boolean mIsScrolling) {
            this.mIsScrolling = mIsScrolling;
        }

        public boolean isScrolling() {
            return mIsScrolling;
        }

        public void setmScrollSpeed(int mScrollSpeed) {
            this.mScrollSpeed = mScrollSpeed;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            Log.i("<<NOGIAS>>","AutoScrollOperation > doInBackground");
            mScrollSpeed= params[0];

            Runnable scrollTask = new Runnable() {
                @Override
                public void run() {
                    mScrollView.smoothScrollTo(0, mScrollView.getScrollY()+ mScrollSpeed);
                }
            };

            while(mIsScrolling) {
                try {
                    mHandler.post(scrollTask);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mIsScrolling= false;
        }

        @Override
        protected void onPreExecute() {
            Log.i("<<NOGIAS>>","AutoScrollOperation > onPreExecute");
            mIsScrolling= true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("<<NOGIAS>>","AutoScrollOperation > onProgressUpdate");
        }
    }
    //////////////////////////////
    private ViewerActivity mContext= (ViewerActivity)this;//REMOVE
    private void getChapterTask(int storyID, int chapterID) {
        new AsyncTask<Integer, Void, Chapter>() {
            @Override
            protected void onPreExecute() {
                mDialog = new ProgressDialog(mContext);
                mDialog.setMessage(getString(R.string.progress_msg));
                mDialog.show();
            }

            @Override
            protected Chapter doInBackground (Integer...params) {
                int storyID, chapterID;
                storyID= params[0];
                chapterID= params[1];

                return getBookChapter(storyID,chapterID);
            }

            @Override
            protected void onPostExecute(Chapter chapter) {
                if (chapter != null) {
                    mChapter = chapter;
                }
                showOnlineChapter(mChapter);
                mDialog.dismiss();
            }
        }.execute(storyID, chapterID);
    }

    private Chapter getBookChapter(int storyID, int chapterID) {
        Context mContext;
        String Content = null;
        String Error = null;
        ProgressDialog Dialog;
        String data = "";
        Chapter chapter = null;
        String path="";

        BufferedReader reader = null;
        try {
            path= "http://wsthichtruyen-1212491.rhcloud.com/?function=2&StoryID="+storyID+"&ChapterID="+chapterID;
            Log.i("<<NOGIAS>>", path);
            URL url = new URL(path);

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
                Log.i("<<NOGIAS>>", "Log1");
                /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                Content = Html.fromHtml(Content).toString();
                jsonArray = new JSONArray(Content);//
//                /*********** Process each JSON Node ************///
                int lengthJsonArr = jsonArray.length();

                for (int i = 0; i < lengthJsonArr; i++) {
//                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonArray.getJSONObject(i);
                    chapter = new Chapter();
                    chapter.setmStoryId(jsonChildNode.optInt(Chapter.KEY_STORYID));
                    chapter.setmChapterId(jsonChildNode.optInt(Chapter.KEY_CHAPTERID));
                    chapter.setmTitle(jsonChildNode.optString(Chapter.KEY_TITLE).toString());
                    chapter.setmContent(jsonChildNode.optString(Chapter.KEY_CONTENT).toString());
                    Log.i("<<NOGIAS>>", "Log2");
                    return chapter;
                }
            } catch (JSONException e) {
                Log.i("<<NOGIAS>>", e.toString());
            }
        }
        return chapter;
    }

    public void showOnlineChapter(Chapter c){
        if(mChapter==null){
            Toast.makeText(this, "Chương được chọn không tồn tại!!!", Toast.LENGTH_LONG).show();
            return;
        }

        mPrevChapter.setVisibility(View.VISIBLE);
        mNextChapter.setVisibility(View.VISIBLE);
        if (mChapter.getmChapterId() == 0) mPrevChapter.setVisibility(View.INVISIBLE);

        getActionBar().setTitle(mBookTitle+" - "+ mChapter.getmTitle());

        mChapterTitle.setText(mChapter.getmTitle());
        mChapterContent.setText(mChapter.getmContent());
        mScrollView.scrollTo(0,0);

        mPosition= c.getmChapterId();
    }

    public void setContentStyle(){
        mChapterContent.setTextColor(Color.parseColor(((MyApplication)getApplication()).getCurrentTextColor()));
        mLayout.setBackgroundColor(Color.parseColor(((MyApplication)getApplication()).getCurrentBackgroundColor()));
        mChapterContent.setLineSpacing(((MyApplication)getApplication()).getCurrentLineSpace(), 1);
        mChapterContent.setTextSize(((MyApplication)getApplication()).getCurrentTextSize());
        mChapterTitle.setTextSize(((MyApplication)getApplication()).getCurrentTextSize()+8);
        if(mScrollOperation.isScrolling()) mScrollOperation.setmScrollSpeed(((MyApplication)getApplication()).getmCurrentReadMode());
    }

    @Override
    protected void onPause() {
        Log.i("<<NOGIAS>>", "onPause");
        mScrollOperation.setmIsScrolling(false);
        super.onPause();
    }

    private void loadPreferences()
    {
        Toast.makeText(this, "LOAD data SharedPreferences", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        MyApplication mApplication= (MyApplication) getApplication();
        mApplication.setmCurrentTextColor(sharedPreferences.getInt("CurrentTextColor", 0));
        mApplication.setmCurrentBackgroundColor(sharedPreferences.getInt("CurrentBackgroundColor", 1));
        mApplication.setmCurrentTextSize(sharedPreferences.getInt("CurrentTextSize", 11));
        mApplication.setmCurrentReadMode(sharedPreferences.getInt("CurrentReadMode", 0));
        mApplication.setmCurrentLineSpace(sharedPreferences.getInt("CurrentLineSpace", 10));
    }

    @Override
    protected void onStop() {
        mLocalDatabase= ((MyApplication)getApplication()).getmLocalDatabase();
        mLocalDatabase.setReadingPositon(mBookID, mPosition, (int)mScrollView.getScrollY());
        Toast.makeText(this, "onStop:"+(int)mScrollView.getScrollY(), Toast.LENGTH_SHORT).show();
        super.onStop();
    }
}
