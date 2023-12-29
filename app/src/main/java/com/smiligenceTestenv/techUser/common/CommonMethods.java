package com.smiligenceTestenv.techUser.common;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;

import javax.crypto.spec.SecretKeySpec;

public class CommonMethods {

    public static String getConnectivityStatusString(Context context) {

        String status = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = "Wifi enabled";
                return status;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = "Mobile data enabled";
                return status;
            }
        } else {
            status = "No internet is available";
            return status;
        }
        return status;
    }


    private Context mContext;

    public CommonMethods(Context context) {
        this.mContext = context;
    }

    public boolean checkGPSConnection() {
         LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public static DatabaseReference fetchFirebaseDatabaseReference(String FirebaseTableName) {
        DatabaseReference mDataRef = FirebaseDatabase.getInstance("https://restaurantsdemo-d89c9-default-rtdb.firebaseio.com/")
                .getReference(FirebaseTableName);
        return mDataRef;
     }

     public static StorageReference fetchFirebaseStorageReference(String FirebaseTableName) {

         StorageReference mStorageRef = FirebaseStorage.getInstance("gs://restaurantsdemo-d89c9.appspot.com")
                 .getReference(FirebaseTableName);
         return mStorageRef;
    }

    public static SecretKeySpec generatekey(String password) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        messageDigest.update(bytes, 0, bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }



}
