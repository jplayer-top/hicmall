package top.jplayer.locationlibrary;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;

import androidx.annotation.Nullable;

/**
 * Created by Obl on 2019/3/24.
 * com.wmtc.wmtb.util
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class OtherLocationMapUtil implements
        LocationSource,
        AMap.OnCameraChangeListener {

    public MapView mAMapView;
    private AMap mAMap;
    public TextView mLocationTip;
    private Handler mHandler;
    private OnLocationChangedListener mLocationChangedListener;
    private double mMyLat;
    private double mMyLng;
    private String mMyPoi;
    private double mLatResult;
    private double mLngResult;
    private String mPoiResult;
    private Activity activity;
    private View mClickView;
    private LatLng mLatLng;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationListener mLocationListener = locationInfo -> {
        if (this.mLocationChangedListener != null) {
            this.mHandler.post(() -> {
                if (locationInfo != null) {
                    mMyLat = mLatResult = locationInfo.getLatitude();
                    mMyLng = mLngResult = locationInfo.getLongitude();
                    mMyPoi = mPoiResult = locationInfo.getStreet() + locationInfo.getPoiName();
                    Location location = new Location("AMap");
                    location.setLatitude(locationInfo.getLatitude());
                    location.setLongitude(locationInfo.getLongitude());
                    location.setTime(locationInfo.getTime());
                    location.setAccuracy(locationInfo.getAccuracy());
                    mLocationChangedListener.onLocationChanged(location);
                    LatLng latLng = new LatLng(mLatResult, mLngResult);
                    addLocatedMarker(latLng, mPoiResult);
                } else {
                    Toast.makeText(activity, "定位失败", Toast.LENGTH_SHORT)
                            .show();
                }

            });
        }
    };


    public OtherLocationMapUtil(Activity activity, View rootView) {
        this.activity = activity;
        this.mAMapView = rootView.findViewById(R.id.rc_ext_amap);
        mClickView = rootView.findViewById(R.id.rc_ext_my_location);
        mLocationTip = rootView.findViewById(R.id.rc_ext_location_marker);
        rootView.findViewById(R.id.cardView).setVisibility(View.GONE);
    }

    public void initData() {
        this.mHandler = new Handler();
        mClickView.setOnClickListener(v -> {
            if (this.mMyPoi != null) {
                this.mAMap.setOnCameraChangeListener(null);
                CameraUpdate update = CameraUpdateFactory.changeLatLng(new LatLng(this.mMyLat, this.mMyLng));
                this.mAMap.animateCamera(update, new AMap.CancelableCallback() {
                    public void onFinish() {
                        mAMap.setOnCameraChangeListener(OtherLocationMapUtil.this);
                    }

                    public void onCancel() {
                    }
                });
                this.mLocationTip.setText(this.mMyPoi);
                this.mLatResult = this.mMyLat;
                this.mLngResult = this.mMyLng;
                this.mPoiResult = this.mMyPoi;

            }

        });
        initLocation();
        initMap();

    }

    private void initLocation() {
        mLocationOption = new AMapLocationClientOption();
        mLocationClient = new AMapLocationClient(activity);
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mLocationOption.setInterval(60000);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
            mLocationClient.setLocationListener(mLocationListener);
        }
    }

    public void setLatLng(LatLng latLng) {
        this.mLatLng = latLng;
    }

    private void initMap() {
        this.mAMap = this.mAMapView.getMap();
        this.mAMap.setLocationSource(this);
        this.mAMap.setMyLocationEnabled(true);
        this.mAMap.getUiSettings().setZoomControlsEnabled(false);
        this.mAMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.mAMap.setMapType(1);
        mAMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.rc_ext_location_marker_other)).position(mLatLng));
        CameraPosition build = new CameraPosition.Builder()
                .target(mLatLng).zoom(17).build();
        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(build));
    }


    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mLocationChangedListener = onLocationChangedListener;
    }

    public void deactivate() {
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    public void onCameraChangeFinish(CameraPosition cameraPosition) {


    }

    private void addLocatedMarker(LatLng latLng, String poi) {
        mAMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.rc_ext_location_marker_own)).position(latLng));
        Log.e("TAG", "addLocatedMarker" + poi);
    }


    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (mAMapView != null) {
            this.mAMapView.onCreate(savedInstanceState);
        }
    }

    public void onDestroy() {
        if (mAMapView != null) {
            this.mAMapView.onDestroy();
        }
    }
}
