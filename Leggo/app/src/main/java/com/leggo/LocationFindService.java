package com.leggo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
