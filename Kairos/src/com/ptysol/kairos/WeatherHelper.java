package com.ptysolutions.kairos;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Ramon on 4/5/2016.
 */
public class WeatherHelper {


    public  WeatherResult[] GetWeather(String latitude, String longitude)
    {
        try {

            WeatherResult[] weatherResults = new WeatherResult[2];
            String url = String.format("http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=imperial&APPID=a6390fe3e02c7b4a959f43863a8187f6&cnt=5", latitude, longitude);
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.execute(url).get();

            for (int w = 0; w < weatherResults.length; w++) {
                weatherResults[w] = new WeatherResult();
                weatherResults[w].DayTemp = String.format("%d°F", (int) Double.parseDouble(json.getJSONArray("list").getJSONObject(w).getJSONObject("temp").getString("day")));
                weatherResults[w].MorningTemp = String.format("%d°F", (int) Double.parseDouble(json.getJSONArray("list").getJSONObject(w).getJSONObject("temp").getString("morn")));
                weatherResults[w].NightTemp = String.format("%d°F", (int) Double.parseDouble(json.getJSONArray("list").getJSONObject(w).getJSONObject("temp").getString("night")));
                weatherResults[w].MinTemp = String.format("%d°", (int) Double.parseDouble(json.getJSONArray("list").getJSONObject(w).getJSONObject("temp").getString("min")));
                weatherResults[w].MaxTemp = String.format("%d°", (int) Double.parseDouble(json.getJSONArray("list").getJSONObject(w).getJSONObject("temp").getString("max")));
                weatherResults[w].NormalDate =new Date( Long.parseLong(json.getJSONArray("list").getJSONObject(w).getString("dt"))*1000);


                weatherResults[w].WeatherSummary = json.getJSONArray("list").getJSONObject(w).getJSONArray("weather").getJSONObject(0).getString("description");
                weatherResults[w].WeatherIcon = json.getJSONArray("list").getJSONObject(w).getJSONArray("weather").getJSONObject(0).getString("icon");
                weatherResults[w].WeatherIconId = json.getJSONArray("list").getJSONObject(w).getJSONArray("weather").getJSONObject(0).getString("id");
               // weatherResults[w].Rain = json.getJSONArray("list").getJSONObject(w).getString("rain");
                weatherResults[w].WeatherImgUrl = String.format("http://openweathermap.org/img/w/%s.png", weatherResults[w].WeatherIcon);

                weatherResults[w].BgColor = GetBackgroundColor(weatherResults[w].WeatherSummary);
                weatherResults[w].WeatherNotificationIcon = GetNotificationIcon(weatherResults[w].WeatherSummary);
                weatherResults[w].Message = GetMessage(weatherResults[w].WeatherSummary);

            }

            return  weatherResults;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return  null;
        }
    }

    private  int GetBackgroundColor(String weatherDesc)
    {
        if(weatherDesc.contains("rain"))
            return 0xdd104a;
        else if (weatherDesc.contains("snow"))
            return  0x0f60cd;
        else
            return 0x1ea061;
    }

    private  int GetNotificationIcon(String weatherDesc)
    {
        if(weatherDesc.contains("rain"))
            return  R.drawable.ic_weather_rain;
        else if (weatherDesc.contains("snow"))
            return  R.drawable.ic_weather_snow;
        else if(weatherDesc.contains("cloud"))
            return  R.drawable.ic_weather_partly_cloudy;
        else return  R.drawable.ic_weather_sunny;
    }

    private  String GetMessage(String weatherDesc) {
        if(weatherDesc.contains("rain"))
            return  "You might want to grab an umbrella today!";
        else if (weatherDesc.contains("snow"))
            return  "We have snow for today, get your snow boots!";
        else if (weatherDesc.contains("clear sky"))
            return  "We have a clear and sunny day today, enjoy it!";
        else
            return  "";
    }

}
