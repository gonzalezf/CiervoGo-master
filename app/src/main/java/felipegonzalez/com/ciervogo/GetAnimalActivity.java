package felipegonzalez.com.ciervogo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class GetAnimalActivity extends AsyncTask<String, Void, String> {

        public  GetAnimalActivity(){}

        private TextView  idAnimalTextView;
        private Context context;
        private int byGetOrPost = 0;

        //flag 0 means get and 1 means post.(By default it is get.)
        public GetAnimalActivity(Context context, TextView idAnimalTextView, int flag) {
            this.context = context;
            this.idAnimalTextView = idAnimalTextView;
            byGetOrPost = flag;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            if (byGetOrPost == 0) { //means by Get Method


                try {
                    String username = (String) arg0[0];

                    String link = "http://nachotp.asuscomm.com:8111/pokedex.php?username="+username;

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();

                    request.setURI(new URI(link));

                    HttpResponse response = client.execute(request);


                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    Log.e("puebla", "6");

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Link", link);
                    Log.e("sb = ", sb.toString());

                    return sb.toString();


                } catch (Exception e) {
                    return new String("Exception u.u: " + e.getMessage());
                }
            } else {
                try {
                    String username = (String) arg0[0];


                    String link = "http://nachotp.asuscomm.com:8111/getDeteccion.php";
                    String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            /* Hacer Algo
            this.statusField.setText("Login Successful");
            */
            Log.e("resultado", result);

            this.idAnimalTextView.setText(result);


        }
 }
