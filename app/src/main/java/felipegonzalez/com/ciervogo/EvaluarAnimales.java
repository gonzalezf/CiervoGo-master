package felipegonzalez.com.ciervogo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class EvaluarAnimales extends ActionBarActivity {


    private String URL_GET_SOME_ANIMALS = "http://nachotp.asuscomm.com:8111/getSomeAnimalForEvaluate.php";
    private ProgressDialog pDialog;
    TextView textViewRankingAnimal;
    ImageView imageViewRankingAnimal;
    RatingBar ratingBarRankingAnimal;
    public int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluar_animales);
        textViewRankingAnimal = (TextView) findViewById(R.id.textviewRankignAnimal);
        imageViewRankingAnimal = (ImageView) findViewById(R.id.imageViewRankingAnimal);
        ratingBarRankingAnimal = (RatingBar) findViewById(R.id.ratingbarAnimal);
        final ArrayList<Animal> animalList = GetSomeAnimals(); //LLamada a base de datos, se crea deteccion en bd
        if (animalList == null) {
            Log.e("Lista Animal", "No se cargan animales para rankear");
            Intent intent = new Intent(EvaluarAnimales.this, MapsActivity.class);
            startActivity(intent);
        }

       textViewRankingAnimal.setText(animalList.get(count).getNombreAnimal());

        Picasso.with(this)
                .load(animalList.get(count).getLinkFotoAnimal())
                .into(imageViewRankingAnimal);

        if(isLoggedIn()) { //Esta logueado en facebook
            Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
            final String username = profile.getId();


            ratingBarRankingAnimal.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    //subir a la bd los datos actuales, luego actualizar
                    new InsertGusto().execute(username, animalList.get(count).getIdAnimal().toString(), String.valueOf(rating));
                    count++;
                    if (count == 5) {
                        Intent intent = new Intent(EvaluarAnimales.this, RecomendadorActivity.class);
                        startActivity(intent); //volver al menu
                        return;
                    }

                    textViewRankingAnimal.setText(animalList.get(count).getNombreAnimal());
                    URL newurl = null;
                    try {
                        newurl = new URL(animalList.get(count).getLinkFotoAnimal());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap mIcon_val = null;
                    try {
                        mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageViewRankingAnimal.setImageBitmap(mIcon_val);

                }

            });
        }


    }

    public boolean isLoggedIn() { //Esta logueado en facebook ?
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    private ArrayList<Animal> GetSomeAnimals(){

        ServiceHandler jsonParser = new ServiceHandler();
        String json = jsonParser.makeServiceCall(URL_GET_SOME_ANIMALS, ServiceHandler.GET);
        if (json != null) {
            ArrayList<Animal> animalLista = new ArrayList<>();
            try {
                JSONObject jsonObj = new JSONObject(json);

                if (jsonObj != null) {
                    Log.e("Lista json animal", json);
                    JSONArray animal = jsonObj
                            .getJSONArray("animales");

                    for (int i = 0; i < animal.length(); i++) {
                        JSONObject catObj = (JSONObject) animal.get(i);
                        Animal cat = new Animal(catObj.getInt("idAnimal"),
                                catObj.getString("nombreAnimal"), catObj.getString("linkFotoAnimal"));


                        if (cat != null) {
                            Log.e("Lista object Animal", cat.getNombreAnimal()); //cat es un objeto deteccion y deteccionList alberga todas las detecciones
                            animalLista.add(cat);
                            Log.e("Lista animal ", "añadido");

                        } else {
                            Log.e("LIsta animal", "objeto nulo");
                        }

                    }
                }
                return animalLista;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "Didn't receive any data from server!");

        }
        return null;
    }


    public class InsertGusto extends AsyncTask<String, Void, String> {

        public InsertGusto(){}

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {

                try {

                    String username = (String) arg0[0];
                    String idAnimal = (String) arg0[1];
                    String puntaje = (String) arg0[2];


                    String link = "http://nachotp.asuscomm.com:8111/insertGusto.php?username="+username+"&idAnimal="+idAnimal+"&puntaje="+puntaje;

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();

                    request.setURI(new URI(link));

                    HttpResponse response = client.execute(request);


                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    Log.e("Link insert gusto", link);
                    Log.e("sb insert = ", sb.toString());

                    return sb.toString();


                } catch (Exception e) {
                    return new String("Exception Insert Gusto " + e.getMessage());
                }

        }

        @Override
        protected void onPostExecute(String result) {

            Log.e("resultado Gusto Insert", result);


        }


    }


}
