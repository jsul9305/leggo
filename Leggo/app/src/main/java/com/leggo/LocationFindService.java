package com.leggo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationFindService extends Service {
    private WindowManager windowManager;
    private View searchView;
    private EditText editText;
    private WindowManager.LayoutParams params;
    private final String LOG_TAG = "LocationFindService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean autoFind = sharedPrefs.getBoolean(getString(R.string.pref_savepoint_key), true);
        String autoTimes = sharedPrefs.getString(getString(R.string.pref_savetime_key), "");
        long autoTime = Long.parseLong(autoTimes);
        if (autoFind){
            try{
                FileInputStream fis = openFileInput("auto.fnd");
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                String readedStr = new String(buffer);
                // Curent Time
                String curTime = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
                // Previous Time
                String prevTime = readedStr.substring(3, readedStr.indexOf("&"));
                readedStr = cutString(readedStr, 3+prevTime.length());
                // Start Point
                String stLatitude = readedStr.substring(4, readedStr.indexOf(","));
                readedStr = cutString(readedStr, 6+stLatitude.length());
                String stLongitude = readedStr.substring(0, readedStr.indexOf("&"));
                readedStr = cutString(readedStr, 1+stLongitude.length());
                // End Point
                String edLatitude = readedStr.substring(3, readedStr.indexOf(","));
                readedStr = cutString(readedStr, 5+edLatitude.length());
                String edLongitude = readedStr.substring(0, readedStr.indexOf("&"));
                readedStr = cutString(readedStr, 1+edLongitude.length());
                // Distance
                double distance = Double.parseDouble(readedStr.substring(3, readedStr.length()));
                long duration = durationTimes(prevTime, curTime);
                if(duration >= 0 && duration <= autoTime){
                    double latitude = Double.valueOf(stLatitude);
                    double longitude = Double.valueOf(stLongitude);
                    MapPoint.GeoCoordinate startGeo = new MapPoint.GeoCoordinate(latitude, longitude);
                    latitude = Double.valueOf(edLatitude);
                    longitude = Double.valueOf(edLongitude);
                    MapPoint.GeoCoordinate endGeo = new MapPoint.GeoCoordinate(latitude, longitude);
                    stopSelf();
                    FindRoadUtil.getInstance().openFindRoad(getApplicationContext(), startGeo, endGeo, distance);
                }
            } catch(IOException e){
            }
        }

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchView = layoutInflater.inflate(R.layout.search_window_view, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        editText = (EditText)searchView.findViewById(R.id.findString);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
        ImageButton imageButton = (ImageButton)searchView.findViewById(R.id.find);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString();
                if (query == null || query.length() == 0) {
                    Toast.makeText(LocationFindService.this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideSoftKeyboard();
                Intent intent = new Intent(LocationFindService.this, SearchMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("searchData", ""+query);
                startActivity(intent);
                stopSelf();
            }
        });
        windowManager.addView(searchView, params);
    }

    private String cutString(String temp, int startLength){
        return temp.substring(startLength, temp.length());
    }

    private long durationTimes(String fileTime, String curTime){
        long fileHour2sec = transTimes(fileTime, 0) * 60 * 60;
        long fileMin2sec = transTimes(fileTime, 3) * 60;
        long fileSecond = transTimes(fileTime, 6);
        long fileSec = fileHour2sec + fileMin2sec + fileSecond;
        long curHour2sec = transTimes(curTime, 0) * 60 * 60;
        long curMin2sec = transTimes(curTime, 3) * 60;
        long curSecond = transTimes(curTime, 6);
        long curSec = curHour2sec + curMin2sec + curSecond;
        long minusMin = (curSec - fileSec) / (60);
        return minusMin;
    }
    private long transTimes(String str, int start){
        String temp = str.substring(start, start+2);
        return Long.parseLong(temp);
    }
    
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(windowManager != null){
            windowManager.removeView(searchView);
            searchView = null;
            windowManager = null;
        }
    }
}
