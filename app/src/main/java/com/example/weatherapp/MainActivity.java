package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker;
    EditText editText;
    Button button;
    ImageView imageView;
    TextView temptv, time,  humidity, pressure, country, city_nam, max_temp, min_temp;
    double let_at,let_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        temptv = findViewById(R.id.textView3);
        time = findViewById(R.id.textView2);


        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        country = findViewById(R.id.country);
        city_nam = findViewById(R.id.city_nam);
        max_temp = findViewById(R.id.temp_max);
        min_temp = findViewById(R.id.min_temp);


        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)

            {

                FindWeather();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MainActivity.this);
            }
        }
        );

        }
        public void FindWeather()
        {
            final String city = editText.getText().toString();
            String url ="http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=462f445106adc1d21494341838c10019&units=metric";
            StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        //find temperature
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject object = jsonObject.getJSONObject("main");
                        double temp = object.getDouble("temp");
                        temptv.setText(temp+"°C");

                        //find country
                        JSONObject object8 = jsonObject.getJSONObject("sys");
                        String count = object8.getString("country");
                        country.setText(count+"  :");

                        //find city
                        String city = jsonObject.getString("name");
                        city_nam.setText(city);

                        //find icon
                        JSONArray jsonArray = jsonObject.getJSONArray("weather");
                        JSONObject obj = jsonArray.getJSONObject(0);
                        String icon = obj.getString("icon");
                        Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageView);

                        //find date & time
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
                        String date = std.format(calendar.getTime());
                        time.setText(date);



                        //find humidity
                        JSONObject object4 = jsonObject.getJSONObject("main");
                        int humidity_find = object4.getInt("humidity");
                        humidity.setText(humidity_find+"  %");

                        //find pressure
                        JSONObject object7 = jsonObject.getJSONObject("main");
                        String pressure_find = object7.getString("pressure");
                        pressure.setText(pressure_find+"  hPa");

                        //find min temperature
                        JSONObject object10 = jsonObject.getJSONObject("main");
                        double mintemp = object10.getDouble("temp_min");
                        min_temp.setText("Min Temp :    "+mintemp+" °C");

                        //find max temperature
                        JSONObject object12 = jsonObject.getJSONObject("main");
                        double maxtemp = object12.getDouble("temp_max");
                        max_temp.setText("Max Temp :    "+maxtemp+" °C");



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        final String city = editText.getText().toString();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=462f445106adc1d21494341838c10019&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Trouver la température
                            JSONObject jsonObject = new JSONObject(response);
                            // Trouver la latitude
                            JSONObject object2 = jsonObject.getJSONObject("coord");
                            double lat_find = object2.getDouble("lat");

                            // Trouver la longitude
                            JSONObject object3 = jsonObject.getJSONObject("coord");
                            double long_find = object3.getDouble("lon");
                            mMap = googleMap;
                            if (marker != null && marker.isVisible()) {
                                marker.remove();
                            }
                            // Ajouter un marqueur à la ville et déplacer la caméra
                            LatLng cityLatLng = new LatLng(lat_find, long_find);
                            marker = mMap.addMarker(new MarkerOptions().position(cityLatLng).title("Je suis à " + city));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cityLatLng));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}