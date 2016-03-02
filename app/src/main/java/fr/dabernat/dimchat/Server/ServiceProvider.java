package fr.dabernat.dimchat.server;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider extends AsyncTask<String, String, String> {

    private static final String LOG_TAG = "ServiceProvider";

    private String method;
    private String urlString;
    private HashMap<String, String> params;

    private OnServiceListener listener;

    public ServiceProvider(String method, String url, HashMap<String, String> params) {
        this.method = method;
        this.urlString = url;
        this.params = params;
    }

    private static String convertinputStreamToString(InputStream ists)
            throws IOException {
        if (ists != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader r1 = new BufferedReader(new InputStreamReader(ists, "UTF-8"));
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                ists.close();
            }


            return sb.toString();
        } else {
            return "{\"data\":\"invalid_connection\",\"status\":\"error\"}";
        }
    }

    public void setOnWebServiceListener(OnServiceListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... urls) {

        String response = "";

        try {

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);

            if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
                httpConn.setRequestMethod(method);
                httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream outputStream = new DataOutputStream(httpConn.getOutputStream());
                outputStream.writeBytes(getPostDataString(params));
                outputStream.flush();
                outputStream.close();
            }

            InputStream is = httpConn.getInputStream();
            response = convertinputStreamToString(is);
            is.close();
            httpConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (response.isEmpty() || response.length() == 0) {
            response = "{\"data\":\"no_response (internal error)\",\"status\":\"error\"}";
        }

        //Log.w("Serveur", "\n" + method + " -> " + urlString + "\n" + "\nReponse : " + response + "\n\n");

        listener.onResult(response);
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}