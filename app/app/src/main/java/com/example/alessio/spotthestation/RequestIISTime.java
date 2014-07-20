package com.example.alessio.spotthestation;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Samuel on 19/07/2014.
 */
public class RequestIISTime extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
//        System.out.println(result);
        long nextISSTime = 0;
        long nextISSDuration = 0;
        try {
            JSONObject jObject = new JSONObject(result);
            nextISSTime = jObject.getJSONArray("response").getJSONObject(0).getLong("risetime");
            nextISSDuration = jObject.getJSONArray("response").getJSONObject(0).getLong("duration");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("IIS time: " + nextISSTime);

        // Update times if last time no longer visible (i.e. station has 'set' (disappeared)):
        if ((MainActivity.getISSNextDuration() == 0) || MainActivity.getISSNextTime()+MainActivity.getISSNextDuration() < (System.currentTimeMillis() / 1000L))
        {
            MainActivity.setISSNextTime(nextISSTime);
            MainActivity.setISSNextDuration(nextISSDuration);
        }
    }
}