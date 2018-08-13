/*
 * Copyright (c) 2018-2018 Bas van den Boom 'Z3r0byte'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.z3r0byteapps.shary.Util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.ShareRestriction;
import eu.z3r0byteapps.shary.SharyLibrary.ShareType;

public class ShareDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ShareDatabase";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "shares";
    private static final String TABLE_RECEIVED = "received";

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_EXPIRY = "expiry";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_RESTRICTIONS = "restrictions";

    private Context context;

    public ShareDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALENDAR_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_RECEIVED + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TYPE + " TEXT,"
                + KEY_SECRET + " TEXT,"
                + KEY_COMMENT + " TEXT,"
                + KEY_EXPIRY + " TEXT,"
                + KEY_RESTRICTIONS + " TEXT"
                + ")";
        db.execSQL(CREATE_CALENDAR_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: New Version!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: New Version!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED);
        onCreate(db);
    }

    public String addItem(Share share) {
        if (share == null) {
            return context.getString(R.string.err_saving_share);
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TYPE, share.getType().getID());
        contentValues.put(KEY_COMMENT, share.getComment());
        contentValues.put(KEY_EXPIRY, formatDate(share.getExpire()));
        contentValues.put(KEY_RESTRICTIONS, new Gson().toJson(share.getRestrictions()));
        contentValues.put(KEY_SECRET, share.getSecret());


        if (db.insert(TABLE_RECEIVED, null, contentValues) == -1) {
            return context.getString(R.string.err_share_exists);
        } else {
            return null;
        }

    }

    public void updateShare(Share share) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_EXPIRY, formatDate(share.getExpire()));
        contentValues.put(KEY_TYPE, share.getType().getID());
        contentValues.put(KEY_RESTRICTIONS, new Gson().toJson(share.getRestrictions()));
        contentValues.put(KEY_COMMENT, share.getComment());

        db.update(TABLE_RECEIVED, contentValues, KEY_ID + "=?", new String[]{Integer.toString(share.getId())});
        db.close();
    }

    public Share[] getShares() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] tableColumns = new String[]{
                "*"
        };
        String orderBy = KEY_ID + " DESC";
        Cursor cursor = db.query(TABLE_RECEIVED, tableColumns, null, null, null, null, orderBy);

        if (cursor != null) {
            Share[] results = new Share[cursor.getCount()];
            int i = 0;
            if (cursor.moveToFirst()) {
                do {
                    Share share = new Share();
                    share.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    share.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
                    share.setExpire(parseDate(cursor.getString(cursor.getColumnIndex(KEY_EXPIRY))));
                    share.setRestrictions(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(KEY_RESTRICTIONS)), ShareRestriction.class));
                    share.setType(ShareType.getTypeById(cursor.getInt(cursor.getColumnIndex(KEY_TYPE))));
                    share.setSecret(cursor.getString(cursor.getColumnIndex(KEY_SECRET)));

                    results[i] = share;
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            return results;
        }
        return null;
    }

    public void deleteShare(Share share) {
        deleteShare(share.getId());
    }

    public void deleteShare(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIVED, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECEIVED, null, null);
    }

    public void removeAll(SQLiteDatabase db) {
        db.delete(TABLE_RECEIVED, null, null);
    }
}