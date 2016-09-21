package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 03-09-16.
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class Animal {
    //Esto se hizo para mostrar los ultimos 5 animales!
    public String title;
    public String description;
    public String imageUrl;
    public String instructionUrl;
    public String label;

    public static ArrayList<Animal> getRecipesFromFile(String filename, Context context){
        final ArrayList<Animal> AnimalList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("animal.json", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray animal = json.getJSONArray("animal");

            // Get Recipe objects from data
            for(int i = 0; i < animal.length(); i++){
                Animal recipe = new Animal();

                recipe.title = animal.getJSONObject(i).getString("title");
                recipe.description = animal.getJSONObject(i).getString("description");
                recipe.imageUrl = animal.getJSONObject(i).getString("image");
                recipe.instructionUrl = animal.getJSONObject(i).getString("url");
                recipe.label = animal.getJSONObject(i).getString("dietLabel");

                AnimalList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return AnimalList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}
