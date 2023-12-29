package com.smiligenceTestenv.techUser.common;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        try {
            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance("https://ceyondev-eb124-default-rtdb.asia-southeast1.firebasedatabase.app/");
                mDatabase.setPersistenceEnabled(true);
            }
        }catch (Exception e){
            Log.w(TAG,"SetPresistenceEnabled:Fail"+ FirebaseDatabase.getInstance().toString());
            e.printStackTrace();
        }
        return mDatabase;
    }
}
