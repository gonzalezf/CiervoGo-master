package felipegonzalez.com.ciervogo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecomendadorActivity extends ActionBarActivity {

    private String GetFavoritiesAnimals = "http://nachotp.asuscomm.com:8111/selectFavoritiesAnimals.php";

    private ArrayList<AnimalFavoritoEncontrado> favoritosList = new ArrayList<AnimalFavoritoEncontrado>();
    private  String Distancia = "100"; //100 km
    private LocationProvider mLocationProvider;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendador2);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
        String username = profile.getId();

        new GetDeteccionesCercanas().execute(String.valueOf(0.0),String.valueOf(0.0),String.valueOf(10000),username); //LLamada a base de datos, se crea deteccion en bd
//        new GetDeteccionesCercanas(String.valueOf(0.0),String.valueOf(0.0),String.valueOf(10000),username).execute(); //LLamada a base de datos, se crea deteccion en bd


    }




    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private class GetDeteccionesCercanas extends AsyncTask<String, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecomendadorActivity.this);
            pDialog.setMessage("Obteniendo animales fav cercanos ..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            String  Latitud = arg0[0];
            String Longitud = arg0[1];
            String Distancia = arg0[2];
            String idFacebook = arg0[3];

            Log.e("R - PARAMETROS ",Latitud+Longitud+Distancia+idFacebook);
            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Latitud",Latitud));
            params.add(new BasicNameValuePair("Longitud",Longitud));
            params.add(new BasicNameValuePair("Distancia",Distancia));
            params.add(new BasicNameValuePair("idFacebook",idFacebook));

            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(GetFavoritiesAnimals,
                    ServiceHandler.POST, params);

            if (json != null) {
                Log.e("R - ",json);
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray deteccion = jsonObj
                                .getJSONArray("favorito");

                        for (int i = 0; i < deteccion.length(); i++) {
                            JSONObject catObj = (JSONObject) deteccion.get(i);
                            AnimalFavoritoEncontrado cat = new AnimalFavoritoEncontrado(catObj.getString("nombreAnimal"),
                                    catObj.getString("linkFotoAnimal"),catObj.getDouble("distance"),catObj.getDouble("Latitud"),catObj.getDouble("Longitud"));

                            Log.e("1",cat.getLinkFotoAnimal());
                            Log.e("2",cat.getNombreAnimal());
                            Log.e("3", String.valueOf(cat.getDistancia()));
                            Log.e("4", String.valueOf(cat.getLatitud()));
                            Log.e("5", String.valueOf(cat.getLongitud()));
                            favoritosList.add(cat); //cat es un objeto deteccion y deteccionList alberga todas las detecciones

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server! - recomendador");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }
}

