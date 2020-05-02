package com.example.test1.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.test1.R;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.customview.WeatherEffectView;
import com.example.test1.dataconstruct.TomorrowWeatherInfo;
import com.example.test1.dataconstruct.UserLocation;
import com.example.test1.dataconstruct.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherAndLocationFragmment extends Fragment {
    TextView tvCity;
    TextView tvTemperature;
    TextView tvWeather;
    ImageView ivWeather;
    WeatherEffectView weatherEffectView;
    Button btMap;
    Paint paint;
    Canvas canvas;
    ExecutorService executorService;
    UserLocation userLocation;
    WeatherInfo weatherInfo;
    int requestCount=0;
    boolean isVisible=false,isInitFinished=false,isFirstLoad=true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible=true;
            if(isVisible&&isInitFinished&&isFirstLoad){
//                GetUserLocation();
                isFirstLoad=false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weather_and_location,container,false);
        tvCity=view.findViewById(R.id.tv_city);
        tvTemperature=view.findViewById(R.id.tv_temperature);
        tvWeather=view.findViewById(R.id.tv_weather);
        weatherEffectView=view.findViewById(R.id.ef_weather);
        btMap=view.findViewById(R.id.bt_map);
        executorService= Executors.newCachedThreadPool();
        weatherEffectView.Init();
        weatherEffectView.SetWeather(WeatherEffectView.WEATHER_TYPE_RAIN);

        isInitFinished=true;
        if(isVisible&&isInitFinished&&isFirstLoad){
//            GetUserLocation();
            isFirstLoad=false;
        }
        return view;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    userLocation=(UserLocation)msg.obj;
                    executorService.execute(new ProcessNetRequest(""));
//                    executorService.execute(new ProcessNetRequest(userLocation.getCity()));
                    break;
                case 1:
                    LoadTemperature();
                    break;
                case 2:
                    LoadTemperatureImage((Bitmap)msg.obj);
                    break;
                case 3:
                    if(requestCount<3){
                        requestCount++;
                        GetUserLocation();
                    }
                    break;
            }
        }
    };

    private void LoadTemperatureImage(Bitmap bitmap) {
        ivWeather.setImageBitmap(bitmap);
    }

    private void LoadTemperature() {
        tvCity.setText(weatherInfo.getCity());
        tvTemperature.setText(weatherInfo.getTemp());
        tvWeather.setText(weatherInfo.getWeather());
        GetNetImage("https:"+weatherInfo.getWeatherimg());
        paint=new Paint();
        paint.setAlpha(180);
        paint.setColor(getResources().getColor(R.color.grayblue));
        paint.setStrokeWidth(1.0f);
        canvas=new Canvas();
        for(int i=15;i!=0;i--){
            canvas.drawLines(ConstantAssemble.linePTS,0,1,paint);
        }
    }

    public void GetUserLocation(){
        try {
            LocationClient locationClient=new LocationClient(getActivity());
            LocationClientOption option=new LocationClientOption();
            option.setScanSpan(0);
            option.setIsNeedAddress(true);
            locationClient.setLocOption(option);
            locationClient.registerLocationListener(new SimpleLocationListener());
            locationClient.start();
        }
        catch (Exception e){
            Log.d("TAG","Baidu Location Error: "+e.getMessage());
        }
    }

    private class SimpleLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation.getCity()==null){
                mHandler.sendEmptyMessage(0);
//                mHandler.sendEmptyMessage(3);
                return;
            }
            String locationType;
            if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                locationType="NetWorkLocation";
            }
            else if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                locationType="GPSLocation";
            }
            else{
                locationType="CachedLocation";
            }
            Log.d("TAG","locationType: "+locationType);
            UserLocation userLocation=new UserLocation();
            userLocation.setAddr(bdLocation.getAddrStr());
            userLocation.setCity(bdLocation.getCity());
            userLocation.setCountry(bdLocation.getCountry());
            userLocation.setDistrict(bdLocation.getDistrict());
            userLocation.setProvince(bdLocation.getProvince());
            userLocation.setStreet(bdLocation.getStreet());
            Message msg=mHandler.obtainMessage();
            msg.what=0;
            msg.obj=userLocation;
            Log.d("TAG","Thread id: "+ Process.myTid());
            Log.d("TAG","Thread id: "+ Thread.currentThread().getId());
            mHandler.sendMessage(msg);
        }
    }

    public class ProcessNetRequest implements Runnable{
        private String cityName;

        public ProcessNetRequest(String cityName){
            this.cityName=cityName;
        }

        @Override
        public void run() {
            if(cityName==null||cityName.isEmpty()){
                cityName="苏州";
//                return;
            }
            String requestUrl="";
            try{
                cityName=URLEncoder.encode(cityName,"UTF-8");
                requestUrl= "https://api.help.bj.cn/apis/weather2d?id="+cityName;
                URL url=new URL(requestUrl);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                InputStream inputStream=connection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                String recieveMessage,result="";
                while((recieveMessage=reader.readLine())!=null){
                    result+=recieveMessage;
                }
                inputStream.close();
                ParseJsonResult(result);
            }
            catch (Exception e){
                Log.d("TAG","Net request error: "+requestUrl+" trace log:---  ");
                e.printStackTrace();
            }
        }
    }

    public void GetNetImage(final String netPath){
        new Thread(){
            @Override
            public void run() {
                try{
                    URL url=new URL(netPath);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream inputStream=connection.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    Message msg=Message.obtain();
                    msg.what=1;
                    msg.obj=bitmap;
                    mHandler.sendMessage(msg);
                    inputStream.close();
                }
                catch (Exception e){
                    Log.d("TAG","Image error: "+e.getMessage());
                }
            }
        }.start();
    }

    private void ParseJsonResult(String result) {
        try{
            JSONObject obj=new JSONObject(result);
            weatherInfo=new WeatherInfo();
            weatherInfo.setStatus(obj.getString("status"));
            weatherInfo.setCity(obj.getString("city"));
            weatherInfo.setAqi(obj.getString("aqi"));
            weatherInfo.setPm25(obj.getString("pm25"));
            weatherInfo.setTemp(obj.getString("temp"));
            weatherInfo.setWeather(obj.getString("weather"));
            weatherInfo.setWind(obj.getString("wind"));
            weatherInfo.setWeatherimg(obj.getString("weatherimg"));
            JSONObject tomorrowweatherObj=obj.getJSONObject("tomorrow");
            TomorrowWeatherInfo tomorrowWeatherInfo=new TomorrowWeatherInfo();
            tomorrowWeatherInfo.setTemp(tomorrowweatherObj.getString("temp"));
            tomorrowWeatherInfo.setWeather(tomorrowweatherObj.getString("weather"));
            tomorrowWeatherInfo.setWind(tomorrowweatherObj.getString("wind"));
            tomorrowWeatherInfo.setWeatherimg(tomorrowweatherObj.getString("weatherimg"));
            mHandler.sendEmptyMessage(1);
        }
        catch (Exception e){
            Log.d("TAG","Parse result error: "+e.getMessage());
        }
    }
}
