package com.example.tuntc2.s1212491_khotruyen.Common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.tuntc2.s1212491_khotruyen.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class Utils {
    public static void downloadImage(ImageView imageView, String url) {
        new DownloadImageSimpleTask(imageView, url).execute();
    }

    private static class DownloadImageSimpleTask extends AsyncTask<Void, Void, Bitmap> {
        private ImageView mImageView;
        private String mUrl;

        public DownloadImageSimpleTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
            mImageView.setTag(url);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImageView.setImageResource(R.drawable.loading);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            try {
                InputStream in = new URL(mUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (!mImageView.getTag().toString().equals(mUrl)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                return;
            }
            if (result != null && mImageView != null) {
                mImageView.setImageBitmap(result);
            } else {
                //
            }
        }
    }

    public static void loadImageFromUrl(Context context, ImageView imageView, String url) {
        if (url.isEmpty()) {
            imageView.setImageResource(R.drawable.noimage);
            return;
        }
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.loading)
                .error(R.drawable.noimage)
                .into(imageView);
    }

    public static String loadFileFromAsset(Activity activity, String fileName) {
        String json = null;
        try {
            InputStream inputStream = activity.getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static long getCurrentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

}
