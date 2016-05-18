package com.example.tuntc2.s1212491_khotruyen.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuntc2.s1212491_khotruyen.Common.Book;
import com.example.tuntc2.s1212491_khotruyen.Common.Utils;
import com.example.tuntc2.s1212491_khotruyen.R;

import java.util.List;

/**
 * Created by TuNTC2 on 5/9/2016.
 */
public class BookGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<Book> mList;

    public BookGridAdapter(Context context, List<Book> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_grid_book, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Book book = mList.get(position);
        if (book != null) {
            //Utils.downloadImage(viewHolder.coverIv, book.getCoverUrl());
            Utils.loadImageFromUrl(mContext, viewHolder.coverIv, book.getCoverUrl());
            viewHolder.titleTv.setText(book.getTitle());
        }

        return convertView;
    }

    static class ViewHolder {
        private ImageView coverIv;
        private TextView titleTv;

        public ViewHolder(View view) {
            coverIv = (ImageView) view.findViewById(R.id.cover_iv);
            titleTv = (TextView) view.findViewById(R.id.title_tv);
        }
    }

}
