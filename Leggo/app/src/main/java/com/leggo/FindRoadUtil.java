package com.leggo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import net.daum.mf.map.api.MapPoint;

/**
 * Created by user on 2015-01-31.
 */
public class FindRoadUtil {
    private static FindRoadUtil ourInstance = new FindRoadUtil();

    public static FindRoadUtil getInstance(){
        return ourInstance;
    }

    public void openFindRoad(Context context, MapPoint.GeoCoordinate startGeo, MapPoint.GeoCoordinate endGeo, double distance){
        String by = new String();
        String startPoint = new String();
        String endPoint = new String();
        String appMaps = new String();
        String mode = new String();
        StringBuilder sb = new StringBuilder();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String linkapp = sharedPreferences.getString(
                context.getString(R.string.pref_linkapp_key),
                context.getString(R.string.pref_linkapp_daum));
        if (linkapp.equals(context.getString(R.string.pref_linkapp_daum))){
            appMaps = "daummaps://route?";
            startPoint = "sp";
            endPoint = "ep";
            mode = "by";
            if(distance >= 400)
                by = "PUBLICTRANSIT";
            else
                by = "FOOT";
        } else if(linkapp.equals(context.getString(R.string.pref_linkapp_google))){
            // 2015.01.28 erorr exist: don;t working to google url scheme
            appMaps = "http://maps.google.com/maps?";
            startPoint = "saddr";
            endPoint = "daddr";
            mode = "dirflg";
            if(distance >= 400)
                by = "r";
            else
                by = "w";
        }

        sb.append(appMaps)
                .append(startPoint).append("=")
                .append(startGeo.latitude).append(",").append(startGeo.longitude)
                .append("&").append(endPoint).append("=")
                .append(endGeo.latitude).append(",").append(endGeo.longitude)
                .append("&").append(mode).append("=")
                .append(by);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        Log.i("FindRoadUtil", "url: "+sb.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
