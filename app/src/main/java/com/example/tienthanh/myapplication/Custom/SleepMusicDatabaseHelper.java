package com.example.tienthanh.myapplication.Custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.tienthanh.myapplication.Model.Category;
import com.example.tienthanh.myapplication.Model.Song;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SleepMusicDatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "sleep_music_database";
    private static final int DB_VERSION = 1;
    private static Context context;

    public SleepMusicDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public static void insertCategory(SQLiteDatabase db, Category category) {
        ContentValues categoryValues = new ContentValues();
        categoryValues.put("NAME", category.getCategoryName());
        categoryValues.put("THUMBNAIL", category.getCategoryThumbnail());
        categoryValues.put("DESCRIPTION", category.getCountItem());
        categoryValues.put("CID", category.getCID());

        db.insert("CATEGORY", null, categoryValues);
    }

  /*  public static void insertAudio(SQLiteDatabase db, Song audio) {
        ContentValues audioValues = new ContentValues();
        audioValues.put("AID", audio.getAID());
        audioValues.put("NAME", audio.getAudioName());
        audioValues.put("THUMBNAIL", audio.getAudioThumbnail());
        audioValues.put("LINK", audio.getAudioLink());
        audioValues.put("AUTHOR", audio.getAudioAuthor());
        audioValues.put("CATEGORYID", audio.getCID());

        db.insert("AUDIO", null, audioValues);
    }*/





    public static Bitmap loadImageFromStorage(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();


        try {
            File f = new File(path);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String saveToInternalStorage(Bitmap bitmapImage, String name, int type) {
        name = name.toLowerCase();
        // path to /data/data/your app/app_data/imageDir
        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        File myPath = new File(directory, name.toLowerCase() + type + ".jpeg");
        if (myPath.exists()) {
            myPath.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.getAbsolutePath();

    }



    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {


            db.execSQL("CREATE TABLE CATEGORY (CID TEXT PRIMARY KEY, "
                    + "NAME TEXT NOT NULL, "
                    + "THUMBNAIL TEXT, "
                    + "ITEMCOUNT INT);");

            db.execSQL("CREATE TABLE AUDIO (AID TEXT PRIMARY KEY, "
                    + "NAME TEXT NOT NULL, "
                    + "THUMBNAIL TEXT, "
                    + "LINK TEXT, "
                    + "AUTHOR TEXT, "
                    + "CATEGORYID INTEGER, "
                    + "FOREIGN KEY(CATEGORYID) REFERENCES CATEGORY(CID));");


        }
        if (oldVersion < 2) {

        }
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

}
