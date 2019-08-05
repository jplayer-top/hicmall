package top.jplayer.locationlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 * Created by Obl on 2019/3/24.
 * com.wmtc.wmtb.util
 * call me : jplayer_top@163.com
 * github : https://github.com/oblivion0001
 */
public class SendLocationMapUtil implements
        LocationSource,
        AMap.OnCameraChangeListener,
        GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapScreenShotListener {

    private BitmapDescriptor mBitmapDescriptor;
    public MapView mAMapView;
    private AMap mAMap;
    public TextView mLocationTip;
    private Handler mHandler;
    private Marker mMarker;
    private GeocodeSearch mGeocodeSearch;
    private OnLocationChangedListener mLocationChangedListener;
    private double mMyLat;
    private double mMyLng;
    private String mMyPoi;
    private double mLatResult;
    private double mLngResult;
    private String mPoiResult;
    private ValueAnimator animator;
    private Activity activity;
    private View mClickView;
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
                    CameraPosition update = new CameraPosition.Builder()
                            .target(latLng).zoom(17).build();
                    mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(update), new AMap.CancelableCallback() {
                        public void onFinish() {
                            mAMap.setOnCameraChangeListener(SendLocationMapUtil.this);
                        }

                        public void onCancel() {

                        }
                    });
                    curLnglat = mLngResult + "," + mLatResult;
                    curAddr = mPoiResult;
                } else {
                    Toast.makeText(activity, "定位失败", Toast.LENGTH_SHORT)
                            .show();
                }

            });
        }
    };
    private String curLnglat = "";
    private String curAddr = "";
    private CardView mCardView;
    private ViewGroup mRootView;

    public SendLocationMapUtil(Activity activity, ViewGroup rootView) {
        this(activity, rootView, null);
    }

    public SendLocationMapUtil(Activity activity, ViewGroup rootView, ILocation listenter) {
        this.activity = activity;
        mRootView = rootView;
        this.mAMapView = mRootView.findViewById(R.id.rc_ext_amap);
        mClickView = rootView.findViewById(R.id.rc_ext_my_location);
        mLocationTip = rootView.findViewById(R.id.rc_ext_location_marker);
        mCardView = rootView.findViewById(R.id.cardView);
        this.listener = listenter;
    }

    public void setTipShow(int i) {
        mCardView.setVisibility(i);
    }

    public void initData() {
        this.mHandler = new Handler();
        mClickView.setOnClickListener(v -> {
            if (this.mMyPoi != null) {
                this.mAMap.setOnCameraChangeListener(null);
                CameraUpdate update = CameraUpdateFactory.changeLatLng(new LatLng(this.mMyLat, this.mMyLng));
                this.mAMap.animateCamera(update, new AMap.CancelableCallback() {
                    public void onFinish() {
                        mAMap.setOnCameraChangeListener(SendLocationMapUtil.this);
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

    private void initMap() {
        this.mAMap = this.mAMapView.getMap();
        this.mAMap.setLocationSource(this);
        this.mAMap.setMyLocationEnabled(true);
        this.mAMap.getUiSettings().setZoomControlsEnabled(false);
        this.mAMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.mAMap.setMapType(1);

//
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.rc_ext_my_locator));
//        myLocationStyle.strokeWidth(0.0F);
//        myLocationStyle.strokeColor(R.color.design_default_color_primary);
//        myLocationStyle.radiusFillColor(0);
//        this.mAMap.setMyLocationStyle(myLocationStyle);
        this.mGeocodeSearch = new GeocodeSearch(activity);
        this.mGeocodeSearch.setOnGeocodeSearchListener(this);
    }


    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult != null) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            this.mLatResult = regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
            this.mLngResult = regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();
            String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            this.mPoiResult = formatAddress.replace(regeocodeAddress.getProvince(), "").replace(regeocodeAddress.getCity(), "").replace(regeocodeAddress.getDistrict(), "");
            this.mLocationTip.setText(this.mPoiResult);
            curLnglat = mLngResult + "," + mLatResult;
            curAddr = mPoiResult;
            LatLng latLng = new LatLng(this.mLatResult, this.mLngResult);
            if (this.mMarker != null) {
                this.mMarker.setPosition(latLng);
            }
            if (listener != null) {
                listener.locationListener(latLng, regeocodeResult, mPoiResult);
            }
        } else {
            Toast.makeText(activity, "定位失败", Toast.LENGTH_SHORT).show();
        }

    }


    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
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
        LatLonPoint point = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 50.0F, "autonavi");
        this.mGeocodeSearch.getFromLocationAsyn(query);
        if (this.mMarker != null) {
            this.animMarker();
        }

    }

    private void addLocatedMarker(LatLng latLng, String poi) {
        this.mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.rc_ext_location_marker);
        MarkerOptions markerOptions = (new MarkerOptions()).position(latLng).icon(this.mBitmapDescriptor);
        this.mMarker = this.mAMap.addMarker(markerOptions);
        this.mMarker.setPositionByPixels(this.mAMapView.getWidth() / 2, this.mAMapView.getHeight() / 2);
        this.mLocationTip.setText(String.format("%s", poi));
        Log.e("TAG", "addLocatedMarker" + poi);
        if (listener != null) {
            listener.locationListener(latLng, null, poi);
        }
    }

    @TargetApi(11)
    private void animMarker() {
        if (this.animator != null) {
            this.animator.start();
            return;
        }
        this.animator = ValueAnimator.ofFloat((float) (this.mAMapView.getHeight() / 2), (float) (this.mAMapView.getHeight() / 2 - 30));
        this.animator.setInterpolator(new DecelerateInterpolator());
        this.animator.setDuration(150L);
        this.animator.setRepeatCount(1);
        this.animator.setRepeatMode(ValueAnimator.REVERSE);
        this.animator.addUpdateListener(animation -> {
            Float value = (Float) animation.getAnimatedValue();
            mMarker.setPositionByPixels(mAMapView.getWidth() / 2, Math.round(value));
        });
        this.animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                mMarker.setIcon(mBitmapDescriptor);
            }
        });
        this.animator.start();
    }


    private ILocation listener;
    private IMapShot mapShotListener;

    public void setLocationListener(ILocation listener) {
        this.listener = listener;
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        saveScreenShot(bitmap, mRootView, mAMapView);
    }

    public void mapShot(IMapShot mapShotListener) {
        this.mapShotListener = mapShotListener;
        mAMap.getMapScreenShot(this);
    }

    public interface ILocation {
        void locationListener(LatLng latLng, RegeocodeResult regeocodeAddress, String poiResult);
    }

    public interface IMapShot {
        void mapShotListener(File file);
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

    public void saveScreenShot(final Bitmap bitmap, final ViewGroup viewContainer, final MapView mapView, final View... views) {
        new Thread() {
            public void run() {

                Bitmap screenShotBitmap = getMapAndViewScreenShot(bitmap, viewContainer, mapView, views);
                if (Environment.getExternalStorageState().
                        equals(Environment.MEDIA_MOUNTED)) {

                    File file = new File(Environment.getExternalStorageDirectory(), "Pictures/" + System.currentTimeMillis() + ".jpg");
                    /**
                     * 如果该文件夹已经存在，则删除它，否则创建一个
                     */
                    try {
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.e("TAG", file.getAbsolutePath());
                        FileOutputStream outputStream = new FileOutputStream(file);
                        screenShotBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        Log.e("TAG", file.length() + "---");
                        //根据自己需求，如果外边对bitmp还有别的需求就不要recycle的
                        screenShotBitmap.recycle();
                        bitmap.recycle();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                    if (mapShotListener != null) {
                        mapShotListener.mapShotListener(file);
                    }
                }
            }

        }.start();

    }

    /**
     * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
     *
     * @param bitmap        地图截图回调返回的结果
     * @param viewContainer MapView和其他要截图的View所在的父容器ViewGroup
     * @param mapView       MapView控件
     * @param views         其他想要在截图中显示的控件
     */
    public static Bitmap getMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer, MapView mapView, View... views) {
        int width = viewContainer.getWidth();
        int height = mapView.getBottom() - mapView.getTop();
        final Bitmap screenBitmap = Bitmap.createBitmap(bitmap, 0, height / 3, width, height / 3);
        Canvas canvas = new Canvas(screenBitmap);
        canvas.drawBitmap(screenBitmap, 0, 0, null);
        for (View view : views) {
            view.setDrawingCacheEnabled(true);
            canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
        }

        return screenBitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("TAG", "zoomBitmap---" + "width:" + width + "---" + "height:" + height);
        Bitmap newbmp;
        try {
            newbmp = Bitmap.createBitmap(bitmap, 0, height / 3, width, 200);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return newbmp;
    }
}
