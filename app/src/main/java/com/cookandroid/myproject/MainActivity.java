package com.cookandroid.myproject;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.Manifest;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;


public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    LocationManager locationManager;
    LocationListener locationListener;

    Context context;
    public static final String TAG = MainActivity.class.getSimpleName();
    public EditText edit;
    public Button send;
    TextView status1;

    TextView textView;

    private double lat, lon;


    private final double markerLatitude = 35.188833519106;
    private final double markerLongitude = 129.084941554332;

    Call<data_model> call;


    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };



    String key = "hWgYHZ6wSKK1RN4xdueRnFz%2FVA405Tx%2BS0EvdwNZlyUviUzbvd5Ram9Z33045GZzCmFZd0uLqwKuMizAdKE2hQ%3D%3D";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();

        initView();

//        textView = findViewById(R.id.mainTextView);

        DBHelper helper;
        SQLiteDatabase db;
        helper = new DBHelper(MainActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);
        ContentValues values = new ContentValues();
        values.put("txt","HelloLlama");
        db.insert("mytable",null,values);

        call = retrofit_client.getApiService().test_api_get();
        call.enqueue(new Callback<data_model>() {
            @Override
            public void onResponse(Call<data_model> call, retrofit2.Response<data_model> response) {
                data_model result = response.body();
                String str;
                str= result.getid() +"\n"+
                        result.getArsno()+"\n"+
                        result.getBstopid()+"\n"+
                        result.getLineno()+"\n"+
                        result.getNodenm()+"\n"+
                        result.getGpsx()+"\n"+
                        result.getGpsy();
                textView.setText(str);
            }

            @Override
            public void onFailure(Call<data_model> call, Throwable t) {

            }
        });


        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3e8n3zzsba"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        //getMapAsync 호출해 비동기로 onMapReady 콜백 메서드 호출
        //onMapReady에서 NaverMap 객체를 받음.
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Location myLocation = location;
                // 위치가 변할 때마다 실행
                Log.i("MY LOCATION", location.getLatitude() + ", " + location.getLongitude());


            }
        };

        checkLocationPermission();

    }

    private void initView() {
//        status1 = (TextView) findViewById(R.id.result);
//        edit = (EditText) findViewById(R.id.message);
//        send = (Button) findViewById(R.id.send);

        //messsage send action
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!edit.getText().toString().isEmpty()){
//                    BusArriveTask(edit.getText().toString());
//                    edit.setText(" ");
//                }
//            }
//        });
    }

    private void BusArriveTask(String search){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String bstopid = null;
        try {
            bstopid = URLEncoder.encode(search,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?serviceKey="+key+"&bstopid="+bstopid+"";
        Log.d(TAG, "URL:"+url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                XMLtoJSONData(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        requestQueue.add(request);
    }

    private void XMLtoJSONData(String xml){
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        JSONObject jsonObject = xmlToJson.toJson();
        Log.d(TAG, "jsonObject:"+jsonObject);

        try {
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject msgHeader = response.getJSONObject("msgHeader");
            String resultCode = msgHeader.optString("resultCode");
            Log.d(TAG, "String resultCode :" + resultCode);

            if (resultCode.equals("0")){
                JSONObject msgBody = response.getJSONObject("msgBody");
                Log.d(TAG, "jsonObject mshBody :"+msgBody);

                JSONArray array = msgBody.getJSONArray("busArrivalList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String 	lineno = obj.optString("lineNo"); // 버스 번호
                    String 	gpsx = obj.optString("gpsx"); // x좌표
                    String gpsy = obj.optString("gpsy"); // y좌표
                    String min1 = obj.optString("min1"); // 남은 도착시간

                    Log.d(TAG, "Bus number (lineno): " + lineno);
                    Log.d(TAG, "X coordinate (gpsx): " + gpsx);
                    Log.d(TAG, "Y coordinate (gpsy): " + gpsy);
                    Log.d(TAG, "Remaining arrival time (min1): " + min1);
                }
            } else if (resultCode.equals("1")) {
                Toast.makeText(context, "시스템 에러가 발생하였습니다", Toast.LENGTH_SHORT).show();
            } else if (resultCode.equals("4")) {
                Toast.makeText(context, "결과가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            } else if (resultCode.equals("8")) {
                Toast.makeText(context, "요청 제한을 초과하였습니다", Toast.LENGTH_SHORT).show();
            } else if (resultCode.equals("23")) {
                Toast.makeText(context, "버스 도착 정보가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void checkLocationPermission() {
        // 위치 권한 허용 여부 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        } else {
            // 권한이 허용되었을 때, 위치 업데이트 요청
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    -1,
                    locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었을 때, 위치 업데이트 요청
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            3000,
                            -1,
                            locationListener);
                }
            }
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        Marker marker1 = new Marker();
        marker1.setPosition(new LatLng(markerLatitude, markerLongitude));
        marker1.setWidth(90);
        marker1.setHeight(90);
        marker1.setMap(naverMap);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                locationOverlay.setPosition(new LatLng(lat, lon));

                // 두 위치 사이 거리 계산
                double distance = DistanceByDegreeAndroid(lat, lon, markerLatitude, markerLongitude);

                // 특정 거리 이내(예: 50미터)에 있을 경우 True 출력
                if (distance < 50) {
                    Log.d(TAG, "True");
                    startActivity(new Intent(MainActivity.this, activity_sub.class));
//                    Toast.makeText(MainActivity.this, "True", Toast.LENGTH_SHORT).show();
                }

                // 거리 오차 출력
                Log.d(TAG, "Distance to marker: " + distance + " meters");
//                Toast.makeText(MainActivity.this, "Distance to marker: " + distance + " meters", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2) {
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        return startPos.distanceTo(endPos);
    }

}

//        NaverMapApilnterface naverMapApilnterface = NaverMapRequest.getClient().create(NaverMapApilnterface.class);
//        Call<NaverMapItem> call = naverMapApilnterface.getMapData();
//
//        call.enqueue(new Callback<NaverMapItem>() {
//            @Override
//            public void onResponse(Call<NaverMapItem> call, retrofit2.Response<NaverMapItem> response) {
//                naverMapList = response.body();
//                naverMapInfo = naverMapList.Bus;
//                Log.d("main",naverMapList.toString());
//
//                for (int i=0; i < naverMapInfo.size(); i++){
//                    Marker[] markers = new Marker[naverMapInfo.size()];
//
//                    markers[i] = new Marker();
//                    Mlat = naverMapInfo.get(i).getgpsx();
//                    Mlon = naverMapInfo.get(i).getgpsy();
//                    markers[i].setPosition(new LatLng(Mlat, Mlon));
//                    markers[i].setMap(naverMap);
//
//                    int finalI = i;
//                    markers[i].setOnClickListener(new Overlay.OnClickListener() {
//                        @Override
//                        public boolean onClick(@NonNull Overlay overlay)
//                        {
//                            Toast.makeText(getApplication(), "마커" + finalI + "클릭", Toast.LENGTH_SHORT).show();
//                            return false;
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NaverMapItem> call, Throwable t) {
//                Log.d("main",naverMapList.toString());
//            }
//        });

//         위치 권한이 허용된 경우에만 LocationTrackingMode.Follow 설정
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
//
//            naverMap.addOnLocationChangeListener(location ->
//                    Toast.makeText(this,
//                            location.getLatitude() + ", " + location.getLongitude(),
//                            Toast.LENGTH_SHORT).show());
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//        }