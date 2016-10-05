package felipegonzalez.com.ciervogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class ShowAnimalActivity extends Activity {


    private String link = "http://nachotp.asuscomm.com:8111/showAnimal.php";

    private TextView txtNombreAnimalView;
    private TextView txtExtincionAnimalView;
    private TextView txtDescripcionAnimalView;
    private TextView txtDetallesAnimalView;
    private Button btnEditAnimal;
    private ImageView imageView;
    private Animal animal;
    private Deteccion deteccion;


    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_animal);

        txtNombreAnimalView = (TextView) findViewById(R.id.txtNombreAnimal);
        txtExtincionAnimalView = (TextView) findViewById(R.id.txtExtincionAnimal);
        txtDescripcionAnimalView = (TextView) findViewById(R.id.txtDescripcionAnimal);
        txtDetallesAnimalView = (TextView) findViewById(R.id.txtDetallesAnimal);

        btnEditAnimal = (Button) findViewById(R.id.btnEditAnimal);
        imageView = (ImageView) findViewById(R.id.imageView);


        // Add new category click event
        btnEditAnimal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String Latitud = String.valueOf(extras.getDouble("Latitud"));
                    String Longitud = String.valueOf(extras.getDouble("Longitud"));
                    String idFacebook = extras.getString("idFacebook");
                    Intent myIntent = new Intent(v.getContext(), EditAnimalActivity.class);
                    myIntent.putExtra("Latitud", Latitud);
                    myIntent.putExtra("Longitud", Longitud);
                    myIntent.putExtra("idFacebook", idFacebook); //Ojo! hacer control, que pasa si no recibe nada, app se cae

                    startActivity(myIntent); //Creo que hay que pasar los datos de las variable
                }
            }

        });

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String nombreAnimal = extras.getString("nombreAnimal");
            String Latitud = String.valueOf(extras.getDouble("Latitud"));
            String Longitud = String.valueOf(extras.getDouble("Longitud"));
            Log.e("Mandar llegado ",Latitud);
            new GetInfoAnimal().execute(nombreAnimal,Latitud,Longitud);

        }


    }




    private class GetInfoAnimal extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowAnimalActivity.this);
            pDialog.setMessage("Cargando Informacion....");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            animal = new Animal();
            deteccion = new Deteccion();

            String nombreAnimal = arg[0];
            String Latitud = arg[1];
            String Longitud = arg[2];

            Log.e("VALORES PARAMS = ",String.valueOf(Latitud)+"-"+String.valueOf(Longitud));


            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombreAnimal", nombreAnimal));
            params.add(new BasicNameValuePair("Latitud", Latitud));
            params.add(new BasicNameValuePair("Longitud", Longitud));


            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(link,
                    ServiceHandler.POST, params);

            Log.e("Json Animal Show : ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray animals = jsonObj
                                .getJSONArray("animales");


                        JSONObject catObj = (JSONObject) animals.get(0);

                                String txtNombreAnimal = catObj.getString("nombreAnimal");
                                String txtExtincionAnimal = catObj.getString("extincionAnimal");
                                String txtDescripcionAnimal = catObj.getString("descripcionAnimal");

                                String txtDetallesAnimal = catObj.getString("detallesAnimal");
                                String image = catObj.getString("image");

                                animal.setNombreAnimal(txtNombreAnimal);
                                animal.setDescripcionAnimal(txtDescripcionAnimal);
                                animal.setExtincionAnimal(txtExtincionAnimal);
                                deteccion.setDetalleAnimal(txtDetallesAnimal);
                                deteccion.setImage(image);



                        }
                    } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(animal!=null){
                txtNombreAnimalView.setText(animal.getNombreAnimal());
                txtDescripcionAnimalView.setText(animal.getDescripcionAnimal());
                txtExtincionAnimalView.setText(animal.getExtincionAnimal());
            }
            if(deteccion !=null){
                txtDetallesAnimalView.setText(deteccion.getDetalleAnimal());
                //imageView.setImageBitmap(createImage(300,300,000000,  deteccion.getImage()));

                byte[] decodedString = Base64.decode(deteccion.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);


            }



            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }
    public Bitmap createImage(int width, int height, int color, String name) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint2 = new Paint();
        paint2.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint2);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);
        paint.setTextScaleX(1);
        canvas.drawText(name, 75 - 25, 75 + 20, paint);
        return bitmap;
    }

}
