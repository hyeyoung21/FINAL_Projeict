package com.example.ma01_20200942;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class WeatherForecastActivity extends AppCompatActivity {
    ListView lvList;

    String apiUrl;
    String apiKey;
    Intent intent;
    String nx;
    String ny;

    ArrayAdapter<ForecastItem> adapter;
    List<ForecastItem> resultList;
    ArrayList<String> arrayList;
    String resultToString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        lvList = findViewById(R.id.lvList);
        intent = getIntent();
        transLocation();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String base_date = dateFormat.format(new Date());

        int hour = 6;
        int minute = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = null;
            now = LocalDateTime.now();
            hour = now.getHour();
            minute = now.getMinute();
        }

        TextView textView = findViewById(R.id.textView);
        textView.setText(base_date +" "+ hour + "시 이후 날씨입니다");

        addMemo();

        apiKey = getResources().getString(R.string.weather_api_key);

        resultList = new ArrayList<ForecastItem>();
        adapter = new ArrayAdapter<ForecastItem>(this, android.R.layout.simple_list_item_1, resultList);

        apiUrl = getResources().getString(R.string.api_url) + "?servicekey=" + apiKey + "&numOfRows=100&pageNo=1&base_date=" + base_date + "&base_time=" + getBaseTime(hour, minute) + "&nx=" + nx + "&ny=" + ny;
        Log.e(TAG, apiUrl);
        new NetworkAsyncTask().execute(apiUrl);

        lvList.setAdapter(adapter);
    }

    public void onClick(View v) {
        v.setFocusable(false);
        switch (v.getId()) {
            case R.id.btnLoadMemo:
                Intent memoIntent = new Intent(WeatherForecastActivity.this, LoadMemoActivity.class);
                startActivity(memoIntent);
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Void, String> {

        final static String NETWORK_ERR_MSG = "Server Error!";
        public final static String TAG = "NetworkAsyncTask";
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(WeatherForecastActivity.this, "Wait", "Downloading...");     // 진행상황 다이얼로그 출력
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) {
                cancel(true);
                return NETWORK_ERR_MSG;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDlg.dismiss();

            ForecastXml parser = new ForecastXml();
            resultList = parser.parse(result);
            adapter.addAll(resultList);     // 리스트뷰에 연결되어 있는 어댑터에 parsing 결과 ArrayList 를 추가
        }

        @Override
        protected void onCancelled(String msg) {
            super.onCancelled();
            progressDlg.dismiss();
            Toast.makeText(WeatherForecastActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    /* 주소(apiAddress)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream) {
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    public void addMemo(){
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    view.setFocusable(false);
                    String memo = resultList.get(i).toString();
                    Intent memoIntent = new Intent(WeatherForecastActivity.this, AddMemoActivity.class);
                    memoIntent.putExtra("memo", memo);
                    startActivity(memoIntent);
                }
        });
    }


    private void transLocation() {
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);

        TransferLocalPoint transferLocalPoint = new TransferLocalPoint();
        TransferLocalPoint.LatXLngY LngLat = transferLocalPoint.convertGRID_GPS(0, lat, lng);
        nx = String.valueOf(LngLat.nx);
        ny = String.valueOf(LngLat.ny);
    }

    private String getBaseTime(int h, int m) {
        String result;

        if (m < 45) {
            // 0시면 2330
            if (h == 0)
                result = "2330";
            else {
                int resultH = h - 1;
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10)
                    result = "0" + resultH + "30";
                else
                    result = resultH + "30";
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else {
            if (h < 10)
                result = "0" + h + "30";
            else
                result = h + "30";
        }
        return result;
    }

}
