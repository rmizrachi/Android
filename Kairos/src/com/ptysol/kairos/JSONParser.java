/**
 * Created by Ramon on 3/28/2016.
 */
package com.ptysol.kairos;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;


import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser extends AsyncTask<String, Object, JSONObject> {

    static JSONObject JSONobject = null;
    static String json = "";


    public AsyncResponse delegate = null;//Call back interface

    public JSONParser(){

    }
   /* public JSONParser(AsyncResponse asyncResponse) {
        delegate = asyncResponse;//Assigning call back interface through constructor
    }*/


    @Override
    protected JSONObject doInBackground(String... params) {

        String url = params[0].replace(" ","%20");

        try {
            URLConnection urlConnection = new URL(url).openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            json = readJSONStream(in);
            try {
                JSONobject = new JSONObject(json);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( Exception ex){
            ex.printStackTrace();
        }
        return JSONobject;

    }

   /* @Override
    protected void onPostExecute(JSONObject result) {
        delegate.processFinish(result);
    }*/


     private  String readJSONStream(InputStream in) {
         try  {
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
             StringBuilder sb = new StringBuilder();
             String line;

             while ((line = reader.readLine()) != null) {
                 sb.append(line + "\n");

             }
             in.close();
             return sb.toString();

         }
         catch (UnsupportedEncodingException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return "";
         }
         catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return "";
         }
     }


}