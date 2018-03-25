package com.ldzc.gaodedemo;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "MainActivity";
    private MapView mMapView;
    private AMap aMap;//地图对象
    private MyLocationStyle myLocationStyle;//我的位置标记
    private GeocodeSearch geocoderSearch;
    private boolean hasShowMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        initMyLocation();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(this);


        SearchView searchView = findViewById(R.id.search_view);
        //searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                MapUtil.getLatLonPointFromAddress(query, geocoderSearch);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        getMyLocationName();


    }

    private void getMyLocationName() {

    }

    private void initMyLocation() {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        //只定位一次；
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        //设置我的位置 现实的图标
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round)));
        //设置地图缩放界别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_marker) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMyLocationChange(Location location) {
        //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取（获取地址描述数据章节有介绍）
        if (!hasShowMarker) {
            hasShowMarker = true;
            Toast.makeText(this, ""+location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onRegeocodeSearched: FormatAddress-->" + ""+location.getLatitude() + "," + location.getLongitude());

            RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(location.getLatitude(), location.getLongitude()), 50, GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(regeocodeQuery);


        }

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取地址描述信息
        Log.d(TAG, "onRegeocodeSearched: AdCode" + regeocodeResult.getRegeocodeAddress().getAdCode());
        Log.d(TAG, "onRegeocodeSearched: City" + regeocodeResult.getRegeocodeAddress().getCity());
        Log.d(TAG, "onRegeocodeSearched: District" + regeocodeResult.getRegeocodeAddress().getDistrict());
        Log.d(TAG, "onRegeocodeSearched: FormatAddress" + regeocodeResult.getRegeocodeAddress().getFormatAddress());
        Log.d(TAG, "onRegeocodeSearched: " );

        LatLonPoint point = regeocodeResult.getRegeocodeQuery().getPoint();

        Toast.makeText(this, regeocodeResult.getRegeocodeAddress().getFormatAddress(), Toast.LENGTH_SHORT).show();

    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        //如果想修改自定义Infow中内容，请通过view找到它并修改

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        //解析result获取坐标信息
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if (geocodeAddressList != null && geocodeAddressList.size() > 0){
                GeocodeAddress geocodeAddress = geocodeAddressList.get(0);
                LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();
                Log.d(TAG, "onGeocodeSearched: AdCode-->" + geocodeAddress.getAdcode());
                Log.d(TAG, "onGeocodeSearched: Province-->" + geocodeAddress.getProvince());
                Log.d(TAG, "onGeocodeSearched: City-->" + geocodeAddress.getCity());
                Log.d(TAG, "onGeocodeSearched: District-->" + geocodeAddress.getDistrict());
                Log.d(TAG, "onGeocodeSearched: LatLonPoint-->" + latLonPoint.getLatitude() + "," + latLonPoint.getLongitude());
                LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                MapUtil.addMarkerToMap(aMap, latLng, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round));
                CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(latLng,15,0,0));
                aMap.animateCamera(mCameraUpdate, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
        }
    }
}
