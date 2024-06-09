package com.cookandroid.myproject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusInfoDownloader extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView busInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

//        busInfoTextView = findViewById(R.id.bus_info_text);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("MY LOCATION", "위도 : " + location.getLatitude());
                Log.i("MY LOCATION", "경도 : " + location.getLongitude());
                new FetchBusInfoTask().execute(location.getLatitude(), location.getLongitude());
            }
        };

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        // 위치 권한 체크 및 요청 로직 추가
    }

    private class FetchBusInfoTask extends AsyncTask<Double, Void, String> {

        @Override
        protected String doInBackground(Double... params) {
            try {
                double latitude = params[0];
                double longitude = params[1];
                String apiUrl = "http://apis.data.go.kr/6260000/BusanBIMS/stopArrByBstopid?bstopid=505780000&serviceKey=hWgYHZ6wSKK1RN4xdueRnFz%2FVA405Tx%2BS0EvdwNZlyUviUzbvd5Ram9Z33045GZzCmFZd0uLqwKuMizAdKE2hQ%3D%3D"; // 실제 API URL로 대체
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (xpp.getName().equals("bstopnm")) {
                                String busStopName = xpp.nextText();
                                busInfoTextView.setText("Bus Stop Name: " + busStopName);
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("FetchBusInfoTask", "Error fetching data");
            }
        }
    }
}
