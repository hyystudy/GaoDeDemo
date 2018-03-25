package com.ldzc.gaodedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeSearch;

/**
 * Created by Administrator on 2018/3/21.
 */

public class MapUtil {


    /**
     * @param aMap 地图对象
     * @param latLng 经纬度对象
     * @param bitmap 要显示的图片
     * @return 返回Marker对象 可以对Marker对象继续设置属性
     */
    public static Marker addMarkerToMap(AMap aMap, LatLng latLng, Bitmap bitmap){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (bitmap != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }

        Marker marker = aMap.addMarker(markerOptions);
        return marker;
    }

    public static void getLatLonPointFromAddress(String address, GeocodeSearch geocoderSearch){
        GeocodeQuery geocodeQuery = new GeocodeQuery(address, "");
        geocoderSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    /**
     * @param aMap 地图对象
     * @param latLng 经纬度对象
     * @param bitmap 要显示的图片
     * @param infoWindowAdapter
     */
    public static void addInfoWindowToMap(AMap aMap, LatLng latLng, Bitmap bitmap, AMap.InfoWindowAdapter infoWindowAdapter){
        Marker marker = addMarkerToMap(aMap, latLng, bitmap);
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        marker.setInfoWindowEnable(true);
        marker.showInfoWindow();
    }
}
