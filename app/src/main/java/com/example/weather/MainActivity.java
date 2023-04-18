package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView currLoc,currTemp, curWeath, humid, wind, precip, visible, currStatus, textView, currentTime, currSunStatus,currSunSet;
    ImageView currIcon, imgNewWeath;
    Button btnWeath;
    EditText etCity;

    RelativeLayout relLay;
    SwipeRefreshLayout swipeLay;
    ScrollView scrollView;
    HourlyForeModel model;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    RequestQueue queue,queue1;
    StringRequest stringRequest, stringRequest1;
    RecyclerView recView;
    String lati;
    String longi;
    String imgUrl,temp;
    Location location1;
    ProgressBar pb;
    ImageView imgWeath;
    Calendar calendar;
    String url;
    List<HourlyForeModel> HourlyList;
    HourlyAdapter recAdapter;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currLoc=findViewById(R.id.currLoc);
        currTemp=findViewById(R.id.currentTemp);
        curWeath=findViewById(R.id.currentWeather);
        humid= findViewById(R.id.humidy);
        wind= findViewById(R.id.windy);
        precip= findViewById(R.id.precipitation);
        visible=findViewById(R.id.visible);
        currIcon=findViewById(R.id.currIcon);
        currStatus=findViewById(R.id.currStatus);
        pBar=findViewById(R.id.pb);
        textView = findViewById(R.id.tvResult);
        btnWeath=findViewById(R.id.btnWeath);
        etCity=findViewById(R.id.etCity);
        imgNewWeath=findViewById(R.id.imgNewWeath);
        currentTime=findViewById(R.id.currentTime);
        relLay=findViewById(R.id.relLay);
        swipeLay=findViewById(R.id.swipeLay);
        scrollView=findViewById(R.id.scrollView);
        currSunStatus=findViewById(R.id.currSunStatus);
        currSunSet=findViewById(R.id.currSunSet);




        recView=findViewById(R.id.recView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        HourlyList=new ArrayList<>();

        getLastLocation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        SwipeWork();

        swipeLay.setDistanceToTriggerSync(300);







    }

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if(location!=null){
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            String address = addresses.get(0).getAddressLine(0);
                            currLoc.setText("Location: "+address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }




                        longi = String.valueOf(location.getLongitude());
                        lati = String.valueOf(location.getLatitude());
                        String url = "https://api.weatherapi.com/v1/forecast.json?key=da12b3ebb9df4a55909121518232702&q="+lati+","+longi;
                        queue1= Volley.newRequestQueue(MainActivity.this);
                        stringRequest1=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject obj1 = new JSONObject(response);
                                    JSONObject obj2= obj1.getJSONObject("forecast");
                                    JSONObject current= obj1.getJSONObject("current");
                                    JSONArray jsonArray=obj2.getJSONArray("forecastday");
                                    String currentTemp= current.getString("temp_c");
                                    JSONObject condCurrent=current.getJSONObject("condition");
                                    String condStatus= condCurrent.getString("text");

                                    String windSpeed=current.getString("wind_kph")+" kmph";
                                    String humidily= current.getString("humidity")+" %";
                                    String precipit= current.getString("precip_mm")+" mm";
                                    String visib=current.getString("vis_km")+" km";
                                    String condUrl= "http:"+condCurrent.getString("icon");

                                    wind.setText(windSpeed);
                                    humid.setText(humidily);
                                    precip.setText(precipit);
                                    visible.setText(visib);
                                    Glide.with(MainActivity.this).load(condUrl).into(currIcon);
                                    currStatus.setText("Status: "+condStatus);

                                    if(response!=null){

                                        pBar.setVisibility(View.INVISIBLE);
                                    currLoc.setVisibility(View.VISIBLE);
                                    relLay.setVisibility(View.VISIBLE);}

                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject= jsonArray.getJSONObject(i);
                                        JSONArray hourArray = jsonObject.getJSONArray("hour");


                                        for (int j=0;j<hourArray.length();j++){
                                            JSONObject hourJsonObj= hourArray.getJSONObject(j);
                                            JSONObject condObj=hourJsonObj.getJSONObject("condition");



                                            String imgUrl=condObj.getString("icon");

                                            String newUrl= "http:"+imgUrl;

                                            model= new HourlyForeModel(newUrl,
                                                    hourJsonObj.getString("time").substring(10,16), hourJsonObj.getString("temp_c").substring(0,2));

                                            HourlyList.add(model);



                                        }



                                        JSONObject dayJSONObj=jsonObject.getJSONObject("day");
                                        JSONObject astroJSONObj=jsonObject.getJSONObject("astro");
                                        JSONObject condJSONObj=dayJSONObj.getJSONObject("condition");
                                        temp=dayJSONObj.getString("avgtemp_c");

                                        String minTemp=dayJSONObj.getString("mintemp_c");
                                        String maxTemp=dayJSONObj.getString("maxtemp_c");
                                        String speed=dayJSONObj.getString("maxwind_kph");
                                        String cond=condJSONObj.getString("text");
                                        imgUrl=condJSONObj.getString("icon");
                                        String sunRise= astroJSONObj.getString("sunrise");
                                        String sunSet= astroJSONObj.getString("sunset");

                                        currTemp.setText(currentTemp.substring(0,2));


                                        currSunStatus.setText(sunRise);
                                        currSunSet.setText(sunSet);



                                    }


                                    recAdapter=new HourlyAdapter(MainActivity.this,HourlyList);
                                    recView.setAdapter(recAdapter);




                                    recView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

                                    ScrollToPosition();

                                    ObjectAnimator animator=ObjectAnimator.ofInt(currIcon, "scrollY",50 );
                                    animator.setDuration(500);
                                    animator.start();



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        queue1.add(stringRequest1);


                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation();


            } else {
                Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

public void ScrollToPosition(){

//    Toast.makeText(this, ""+currSunStatus.getText(), Toast.LENGTH_SHORT).show();





    String currentDateTimeString = java.text.DateFormat.getTimeInstance().format(new Date());
    currentTime.setText(currentDateTimeString);
    recView.getLayoutManager().canScrollHorizontally();

    if (currSunStatus.getText().subSequence(1,1).equals(currentTime.getText().subSequence(0,0)) && currSunStatus.getText().subSequence(3,4).equals(currentTime.getText().subSequence(2,3))
    && currentTime.getText().toString().substring(0,10).contains("a")){

        swipeLay.setBackgroundResource(R.drawable.sunny);

    }

    if (currSunSet.getText().subSequence(1,1).equals(currentTime.getText().subSequence(0,0)) && currSunSet.getText().subSequence(3,4).equals(currentTime.getText().subSequence(2,3))
            && currentTime.getText().toString().substring(0,10).contains("p")){

        swipeLay.setBackgroundResource(R.drawable.moony);

    }

    if(currentTime.getText().subSequence(0,1).equals("1")){
        recView.getLayoutManager().scrollToPosition(1);
    }else if(currentTime.getText().subSequence(0,1).equals("2") && currentTime.getText().toString().substring(0,10).contains("a")){
        recView.getLayoutManager().scrollToPosition(2);
    }else if(currentTime.getText().subSequence(0,1).equals("3")){
        recView.getLayoutManager().scrollToPosition(3);
    }else if(currentTime.getText().subSequence(0,1).equals("4")){
        recView.getLayoutManager().scrollToPosition(4);
    }else if(currentTime.getText().subSequence(0,1).equals("5")){
        recView.getLayoutManager().scrollToPosition(5);
    }else if(currentTime.getText().subSequence(0,1).equals("6")){
        recView.getLayoutManager().scrollToPosition(6);
    }else if(currentTime.getText().subSequence(0,1).equals("7")){
        recView.getLayoutManager().scrollToPosition(7);
    }else if(currentTime.getText().subSequence(0,1).equals("8")){
        recView.getLayoutManager().scrollToPosition(8);
    }else if(currentTime.getText().subSequence(0,1).equals("9")){
        recView.getLayoutManager().scrollToPosition(9);
    }else if(currentTime.getText().subSequence(0,1).equals("10")){
        recView.getLayoutManager().scrollToPosition(10);
    }else if(currentTime.getText().subSequence(0,1).equals("11")){
        recView.getLayoutManager().scrollToPosition(11);
    }else if(currentTime.getText().subSequence(0,1).equals("12")){
        recView.getLayoutManager().scrollToPosition(0);
    }else if(currentTime.getText().subSequence(0,1).equals("1") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(13);
    }else if(currentTime.getText().subSequence(0,1).equals("2") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(14);
    }else if(currentTime.getText().subSequence(0,1).equals("3") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(15);
    }else if(currentTime.getText().subSequence(0,1).equals("4") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(16);
    }else if(currentTime.getText().subSequence(0,1).equals("5") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(17);
    }else if(currentTime.getText().subSequence(0,1).equals("6") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(18);
    }else if(currentTime.getText().subSequence(0,1).equals("7") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(19);
    }else if(currentTime.getText().subSequence(0,1).equals("8") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(20);
    }else if(currentTime.getText().subSequence(0,1).equals("9") && currentTime.getText().toString().substring(0,10).contains("p")){
        recView.getLayoutManager().scrollToPosition(21);
    }else if(currentTime.getText().subSequence(0,2).equals("10") && currentTime.getText().toString().substring(0,11).contains("p")){
        recView.getLayoutManager().scrollToPosition(22);
    }else if(currentTime.getText().subSequence(0,2).equals("11") && currentTime.getText().toString().substring(0,11).contains("p")){
        recView.getLayoutManager().scrollToPosition(23);
    }else if(currentTime.getText().subSequence(0,2).equals("12") && currentTime.getText().toString().substring(0,11).contains("p")){
        recView.getLayoutManager().scrollToPosition(12);
    }







}

    public void GetWeather(View view) {
        String city = etCity.getText().toString().trim();
        String urlCity= "https://api.weatherapi.com/v1/forecast.json?key=da12b3ebb9df4a55909121518232702&q="+city+"/forecast";
        queue = Volley.newRequestQueue(MainActivity.this);
        stringRequest = new StringRequest(Request.Method.GET, urlCity, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj1 = new JSONObject(response);
                    JSONObject obj2= obj1.getJSONObject("forecast");
                    JSONArray jsonArray=obj2.getJSONArray("forecastday");


                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject= jsonArray.getJSONObject(i);
//                        JSONObject jsonObjectHour=jsonArrayhour.getJSONObject(i);
                        JSONObject dayJSONObj=jsonObject.getJSONObject("day");
                        JSONObject astroJSONObj=jsonObject.getJSONObject("astro");
                        JSONObject condJSONObj=dayJSONObj.getJSONObject("condition");
//                        String tempHour=jsonObjectHour.getString("temp_c");
                        String temp=dayJSONObj.getString("avgtemp_c");
                        String minTemp=dayJSONObj.getString("mintemp_c");
                        String maxTemp=dayJSONObj.getString("maxtemp_c");
                        String speed=dayJSONObj.getString("maxwind_kph");
                        String cond=condJSONObj.getString("text");
                        String veryNewUrl = condJSONObj.getString("icon");

                        url="https:"+veryNewUrl;

                        textView.setText("Temp: "+temp+" C"+
                                "\nmin: "+minTemp+ " & max: "+maxTemp+
                                "\nWind: "+speed+" km/h"+
                                "\nStatus: "+cond);

                        Picasso.get().load(url).into(imgNewWeath);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void SwipeWork(){
        swipeLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currIcon.setVisibility(View.INVISIBLE);
                ObjectAnimator animator=ObjectAnimator.ofInt(currIcon, "scrollY",-50 );
                animator.setDuration(500);
                animator.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLay.setRefreshing(false);
                        getLastLocation();

                        currIcon.setVisibility(View.VISIBLE);
//                        Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();


                    }
                },2000);

            }
        });
    }
}