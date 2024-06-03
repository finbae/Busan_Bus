package com.cookandroid.myproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

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

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    LocationManager locationManager;
    LocationListener locationListener;

    Context context;
    public static final String TAG = MainActivity.class.getSimpleName();

    String key = "hWgYHZ6wSKK1RN4xdueRnFz%2FVA405Tx%2BS0EvdwNZlyUviUzbvd5Ram9Z33045GZzCmFZd0uLqwKuMizAdKE2hQ%3D%3D";

    private ArrayList<BusStop> busStops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getBaseContext();

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("3e8n3zzsba"));

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("MY LOCATION", "위도 : " + location.getLatitude());
                Log.i("MY LOCATION", "경도 : " + location.getLongitude());

                checkProximityAndLog(location);
            }
        };

        checkLocationPermission();
        // 초기 버스 데이터 불러오기
        fetchBusStopData();
    }

    private void fetchBusStopData() {
        // 샘플 버스 정류장 ID로 초기 데이터를 불러옵니다.
        BusArriveTask("YOUR_BUS_STOP_ID");
    }

    private void BusArriveTask(String search) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String bstopid = null;
        try {
            bstopid = URLEncoder.encode(search, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?serviceKey=" + key + "&bstopid=" + bstopid + "";
        Log.d(TAG, "URL:" + url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                XMLtoJSONData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Error fetching data: " + volleyError.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void XMLtoJSONData(String xml) {
        XmlToJson xmlToJson = new XmlToJson.Builder(xml).build();
        JSONObject jsonObject = xmlToJson.toJson();
        Log.d(TAG, "jsonObject:" + jsonObject);

        try {
            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject msgHeader = response.getJSONObject("msgHeader");
            String resultCode = msgHeader.optString("resultCode");
            Log.d(TAG, "String resultCode :" + resultCode);

            if (resultCode.equals("0")) {
                JSONObject msgBody = response.getJSONObject("msgBody");
                Log.d(TAG, "jsonObject msgBody :" + msgBody);

                JSONArray array = msgBody.getJSONArray("busArrivalList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String lineno = obj.optString("lineNo"); // 버스 번호
                    double gpsx = obj.optDouble("gpsx"); // x좌표
                    double gpsy = obj.optDouble("gpsy"); // y좌표
                    String min1 = obj.optString("min1"); // 남은 도착시간

                    Log.d(TAG, "Bus number (lineno): " + lineno);
                    Log.d(TAG, "X coordinate (gpsx): " + gpsx);
                    Log.d(TAG, "Y coordinate (gpsy): " + gpsy);
                    Log.d(TAG, "Remaining arrival time (min1): " + min1);

                    BusStop busStop = new BusStop(lineno, gpsx, gpsy, min1);
                    busStops.add(busStop);
                }
            } else {
                handleAPIError(resultCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleAPIError(String resultCode) {
        int messageResId;
        switch (resultCode) {
            case "1":
                messageResId = R.string.system_error;
                break;
            case "4":
                messageResId = R.string.no_result;
                break;
            case "8":
                messageResId = R.string.request_limit_exceeded;
                break;
            case "23":
                messageResId = R.string.no_bus_arrival_info;
                break;
            default:
                messageResId = R.string.unknown_error;
                break;
        }
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void checkProximityAndLog(Location location) {
        for (BusStop busStop : busStops) {
            float[] results = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), busStop.gpsY, busStop.gpsX, results);
            if (results[0] < 100) { // 100 meters 이내일 경우
                Log.i("Proximity Alert", "버스 번호: " + busStop.lineNo + ", 남은 도착시간: " + busStop.min1);
            }
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,
                    1,
                    locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

            naverMap.addOnLocationChangeListener(location ->
                    Toast.makeText(this,
                            location.getLatitude() + ", " + location.getLongitude(),
                            Toast.LENGTH_SHORT).show());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    static class BusStop {
        String lineNo;
        double gpsX;
        double gpsY;
        String min1;

        BusStop(String lineNo, double gpsX, double gpsY, String min1) {
            this.lineNo = lineNo;
            this.gpsX = gpsX;
            this.gpsY = gpsY;
            this.min1 = min1;
        }
    }
}
