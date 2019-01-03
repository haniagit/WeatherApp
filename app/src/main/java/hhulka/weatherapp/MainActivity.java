package hhulka.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button warsawButton;
    private Button parisButton;
    private Button saigonButton;
    private EditText editText1;
    private TextView textview1;
    private TextView textview2;
    private JSONObject jsonObject;
    private String city;
    private String currentWeather = "";
    private String currentWind = "";
    private String currentTemp = "";
    private String addInfo = "";
    private int currentMonth = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
    private boolean cityExists;

    private static final String TAG = "DATAx";


    public class WeatherApi extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + urls[0] + "&APPID=faed2744e1f91ed5c82948a24cbbd5ba");
                Log.i(TAG, "doInBackground: " + urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                cityExists = true;
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                cityExists = false;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (cityExists) {
                try {
                    jsonObject = new JSONObject(s);
                    String weatherInfo = jsonObject.getString("weather");

                    JSONArray arr = new JSONArray(weatherInfo);
                    JSONObject jsonPart = arr.getJSONObject(0);
                    currentWeather = jsonPart.getString("main");
                    addInfo = jsonPart.getString("description");
                    Log.i(TAG, "onPostExecute: current" + currentWeather);

                    JSONObject temp = jsonObject.getJSONObject("main");
                    currentTemp = temp.getString("temp");

                    JSONObject wind = jsonObject.getJSONObject("wind");
                    currentWind = wind.getString("speed");
                    Log.i(TAG, "onPostExecute: windSpeed" + currentWind);

                    Log.i(TAG, "onPostExecute: CurrentWeather: " + currentTemp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setBackground();
            }
            setInfoMessage();
            textview2.setVisibility(View.VISIBLE);
        }
    }

    public void getWeather(View view) {

        new WeatherApi().execute(city);

        textview1.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        editText1.setVisibility(View.INVISIBLE);
        warsawButton.setVisibility(View.INVISIBLE);
        parisButton.setVisibility(View.INVISIBLE);
        saigonButton.setVisibility(View.INVISIBLE);
    }

    private String getCelcius(String kelvin) {
        double celc = Double.parseDouble(kelvin) - 273.15;
        Log.i(TAG, "getCelcius: celc: " + celc);
        Log.i(TAG, "getCelcius: celcFormat: " + String.format("%.02f", celc));
        return String.format("%.02f", celc);
    }

    private String getKmperHWind(String meterPerSec) {
        double wind = Double.parseDouble(meterPerSec);
        double result = wind * 3.6;
        return String.format("%.02f", result);
    }


    private void setInfoMessage() {
        String completeWeatherInfo = "";
        Log.i(TAG, "setInfoMessage: " + currentWeather);
        if (cityExists) {
            completeWeatherInfo = "Current weather in " + city + ":\ntemperature: " + getCelcius(currentTemp) + " C\nconditions: " + currentWeather
                    + "\nwind speed: " + getKmperHWind(currentWind) + " km/s";
        } else {
            completeWeatherInfo = "Couldn't find given city (" + city + ").";
        }
        textview2.setText(completeWeatherInfo);
        button2.setVisibility(View.VISIBLE);
    }

    private void setBackground() {
        boolean isHeavyWind = (Double.parseDouble(currentWind) > 10.83);

        if (addInfo.contains("drizzle") && !isHeavyWind) {
            imageView.setImageResource(R.drawable.drizzle);
        } else if (addInfo.contains("fog") || addInfo.contains("mist") && !isHeavyWind) {
            imageView.setImageResource(R.drawable.fog);
        } else if (addInfo.contains("clouds") && !addInfo.contains("few")) {
            imageView.setImageResource(R.drawable.heavy_clouds);
        } else if (addInfo.contains("few clouds") && !isHeavyWind) {
            imageView.setImageResource(R.drawable.light_clouds);
        } else if (addInfo.contains("rain")) {
            imageView.setImageResource(R.drawable.rain);
        } else if (addInfo.contains("snow") || currentWeather.contains("Snow")) {
            imageView.setImageResource(R.drawable.snow);
        } else if (addInfo.contains("thunderstorm") || currentWeather.contains("Thunderstorm")) {
            imageView.setImageResource(R.drawable.thunder);
        } else if (isHeavyWind) {
            imageView.setImageResource(R.drawable.wind);
        } else {
            imageView.setImageResource(R.drawable.light_clouds);
        }
    }

    public void goToMain(View view) {
        editText1.setText("");
        textview2.setVisibility(View.INVISIBLE);
        textview1.setVisibility(View.VISIBLE);
        editText1.setVisibility(View.VISIBLE);
        button1.setVisibility(View.VISIBLE);
        setSeasonBackground();
        button2.setVisibility(View.INVISIBLE);

        warsawButton.setVisibility(View.VISIBLE);
        parisButton.setVisibility(View.VISIBLE);
        saigonButton.setVisibility(View.VISIBLE);
    }

    private void setSeasonBackground() {
        if (currentMonth <= 3) {
            imageView.setImageResource(R.drawable.winter);
        } else if (currentMonth <= 6) {
            imageView.setImageResource(R.drawable.spring);
        } else if (currentMonth <= 9) {
            imageView.setImageResource(R.drawable.summer);
        } else {
            imageView.setImageResource(R.drawable.autumn);
        }
    }

    public void favCity(View view){
        String tag = view.getTag().toString();
        if(tag.equals("warsaw")){
            city = "Warsaw";
        } else if (tag.equals("paris")){
            city = "Paris";
        } else if (tag.equals("saigon")){
            city = "Saigon";
        }
        getWeather(findViewById(R.id.saigonButton));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        editText1 = findViewById(R.id.editText1);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        warsawButton = findViewById(R.id.warsawButton);
        parisButton = findViewById(R.id.parisButton);
        saigonButton = findViewById(R.id.saigonButton);
        textview1 = findViewById(R.id.textView1);
        textview2 = findViewById(R.id.textView2);
        city = editText1.getText().toString();

        Log.i(TAG, "onCreate: currentMonth: " + currentMonth);

        setSeasonBackground();

    }
}
