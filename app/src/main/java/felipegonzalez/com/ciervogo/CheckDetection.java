package felipegonzalez.com.ciervogo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckDetection extends ActionBarActivity {
    Button trueButton;
    Button falseButton;
    Button unknownButton;
    ImageView imageViewCheckAnimal;
    TextView textViewAnimal;
    public int count = 0; //cantidad de veces que puede checkear un animal
    private String URL_UPDATE_VALIDEZ = "http://nachotp.asuscomm.com:8111/updateValidezAnimal.php";
    private String URL_UPDATE_PUNTAJE = "http://nachotp.asuscomm.com:8111/updatePuntajeUsuario.php";
    private String URL_GET_SOME_DETECTION = "http://nachotp.asuscomm.com:8111/getSomeDetection.php";
    private String URL_GET_ANIMAL_BY_ID = "http://nachotp.asuscomm.com:8111/GetAnimalById.php";

    private ProgressDialog pDialog;
    String PointsPerCheck = "100";
    private RequestHandler requestHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detection);

        trueButton = (Button) findViewById(R.id.TrueAnimalButton);
        falseButton = (Button) findViewById(R.id.FalseAnimalButton);
        unknownButton = (Button) findViewById(R.id.UnknownAnimalButton);
        textViewAnimal = (TextView) findViewById(R.id.textviewAnimal);

        imageViewCheckAnimal = (ImageView) findViewById(R.id.imageViewCheckAnimal);
        requestHandler = new RequestHandler();

        FacebookSdk.sdkInitialize(getApplicationContext());
        final ArrayList<Deteccion> deteccionList = GetSomeDetections(); //LLamada a base de datos, se crea deteccion en bd


        if (deteccionList == null) {
            Log.e("Lista", "fail");
            Intent intent = new Intent(CheckDetection.this, MapsActivity.class);
            startActivity(intent);
        }


        if (isLoggedIn()) { //Esta logueado en facebook
            Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
            final String idFacebook = profile.getId();

            textViewAnimal.setText(GetAnimalName(String.valueOf(deteccionList.get(count).getIdAnimal())));
            Log.e("Lista",String.valueOf(deteccionList.get(0).getIdAnimal()));
            getImage(String.valueOf(deteccionList.get(count).getIdDeteccion()));

            trueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateValidez().execute(String.valueOf(deteccionList.get(count).getIdDeteccion()), "1");
                    new UpdatePuntajeUsuario().execute(idFacebook, PointsPerCheck);

                    count++;
                    if (count == 3) {
                        volverMenu(v);

                    }
                    if(count<3) {
                        textViewAnimal.setText(GetAnimalName(String.valueOf(deteccionList.get(count).getIdAnimal())));
                        getImage(String.valueOf(deteccionList.get(count).getIdDeteccion()));

                    }
                }
            });

            unknownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    if (count == 3) {
                        volverMenu(v);
                    }
                    if(count<3) {
                        textViewAnimal.setText(GetAnimalName(String.valueOf(deteccionList.get(count).getIdAnimal())));
                        getImage(String.valueOf(deteccionList.get(count).getIdDeteccion()));
                    }
                }
            });

            falseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateValidez().execute(String.valueOf(deteccionList.get(count).getIdDeteccion()), "-1");
                    new UpdatePuntajeUsuario().execute(idFacebook, PointsPerCheck);

                    count++;
                    if (count == 3) {
                        volverMenu(v);
                    }
                    if(count<3) {
                        textViewAnimal.setText(GetAnimalName(String.valueOf(deteccionList.get(count).getIdAnimal())));
                        getImage(String.valueOf(deteccionList.get(count).getIdDeteccion()));

                    }

                }
            });

        } else { //No esta logueado
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
        }
    }

    private ArrayList<Deteccion> GetSomeDetections() {
        {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_GET_SOME_DETECTION, ServiceHandler.GET);
            if (json != null) {
                ArrayList<Deteccion> deteccionLista = new ArrayList<>();
                try {
                    JSONObject jsonObj = new JSONObject(json);

                    if (jsonObj != null) {
                        Log.e("Lista json", json);
                        JSONArray deteccion = jsonObj
                                .getJSONArray("deteccion");

                        for (int i = 0; i < deteccion.length(); i++) {
                            JSONObject catObj = (JSONObject) deteccion.get(i);
                            Deteccion cat = new Deteccion(catObj.getInt("idDeteccion"),
                                    catObj.getInt("idFacebook"), catObj.getInt("idAnimal"), catObj.getLong("Latitud"), catObj.getLong("Longitud"));

                            if (cat != null) {
                                Log.e("Lista object", String.valueOf(cat.getIdAnimal())); //cat es un objeto deteccion y deteccionList alberga todas las detecciones
                                deteccionLista.add(cat);
                                Log.e("Lista deteccion ", "añadido");

                            } else {
                                Log.e("LIsta", "objeto nulo");
                            }

                        }
                    }
                    return deteccionLista;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");

            }
        }
        return null;
    }


    public boolean isLoggedIn() { //Esta logueado en facebook ?
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    public void volverMenu(View view) {
        Intent intent = new Intent(CheckDetection.this, MapsActivity.class);
        startActivity(intent);
    }


    private class UpdateValidez extends AsyncTask<String, Void, Void> {

        boolean isUpdateAnimal = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckDetection.this);
            pDialog.setMessage("Actualizando Valores");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            String idDeteccion = arg[0];
            String puntaje = arg[1];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idDeteccion", idDeteccion));
            params.add(new BasicNameValuePair("puntaje", puntaje));

            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(URL_UPDATE_VALIDEZ,
                    ServiceHandler.POST, params);

            Log.e("UpdateVALIDEZAnimal: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isUpdateAnimal = true;

                    } else {
                        Log.e("Update validez Error: ", "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "validezDidn't receive any data from server!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isUpdateAnimal) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all categories
                        //new GetAnimals().execute();
                    }
                });
            }
        }
    }

    private class UpdatePuntajeUsuario extends AsyncTask<String, Void, Void> {

        boolean isUpdateUser = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckDetection.this);
            pDialog.setMessage("Actualizando Puntaje Usuario");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            String idFacebook = arg[0];
            String puntaje = arg[1];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idFacebook", idFacebook));
            params.add(new BasicNameValuePair("puntaje", puntaje));

            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(URL_UPDATE_PUNTAJE,
                    ServiceHandler.POST, params);

            Log.e("UpdatePUNTAJEusuario: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isUpdateUser = true;

                    } else {
                        Log.e("Update Puntaje Error: ", "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Puntaje Didn't receive any data from server!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isUpdateUser) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all categories
                        //new GetAnimals().execute();
                    }
                });
            }
        }
    }

    private String GetAnimalName(String idAnimal) {

        // Preparing post params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idAnimal", idAnimal));
        ServiceHandler serviceClient = new ServiceHandler();
        String json = serviceClient.makeServiceCall(URL_GET_ANIMAL_BY_ID,
                ServiceHandler.POST, params);

        Log.e("GETANIMALID: ", "> " + json);
        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                if (jsonObj != null) {
                    JSONArray usuario = jsonObj
                            .getJSONArray("animales");

                    for (int i = 0; i < usuario.length(); i++) {
                        JSONObject catObj = (JSONObject) usuario.get(i);
                        String resultPuntaje = catObj.getString("nombreAnimal");
                        Log.e("Result Animal", resultPuntaje);
                        return  resultPuntaje;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "Didn't receive any data from server!");
        }

        return null;
    }

    private void getImage(String idDeteccion) {
        String id = idDeteccion;
        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(CheckDetection.this, "Descargando datos..", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                imageViewCheckAnimal.setImageBitmap(b);
                Log.e("IMAGE SET ","lo hizo");

            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://nachotp.asuscomm.com:8111/getImage.php?idDeteccion="+id;
                Bitmap image = null;
                try {
                    URL url = new URL(add);
                    Log.e("RETORNA", String.valueOf(url));
                    InputStream in = url.openConnection().getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(in,1024*8);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    int len=0;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1){
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    bis.close();

                    byte[] data = out.toByteArray();
                    image = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Log.e("RETORNA", String.valueOf(image));
                    return image;
                    }
                catch (Exception ex){
                    return null;
                }

           }
        }
        GetImage gi = new GetImage();
        gi.execute(id);
    }
}
