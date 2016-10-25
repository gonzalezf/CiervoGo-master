package felipegonzalez.com.ciervogo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class ExplorerActivity extends ActionBarActivity {

    Button b1;
    EditText ed1;
    private WebView wv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        b1=(Button)findViewById(R.id.button);
        ed1=(EditText)findViewById(R.id.edit_text);

        wv1=(WebView)findViewById(R.id.webview);
        wv1.setWebViewClient(new MyBrowser()); // no se si esto funciona o q hace

        final String urlInicio = "http://dbpedia.org/snorql/?query=%20SELECT%20%3Fname%2C%20%3Fkingdom%2C%20%3Fphylum%2C%20%3Fclass%2C%20%3Forder%2C%20%3Ffamily%2C%20%3Fgenus%2C%20%3Fspecies%2C%20%3Fsubspecies%2C%20%3Fimg%2C%20%3Fabstract%0D%0A%20WHERE%20%7B%0D%0A%20%20%3Fs%20dbpedia2%3Aregnum%20%3FhasValue%3B%0D%0A%20%20%20%20rdfs%3Alabel%20%3Fname%0D%0A%20%20FILTER%20regex(%20%3Fname%2C%20%22";
        final String urlFinal="%22%2C%20%22i%22%20)%0D%0A%20%20FILTER%20(%20langMatches(%20lang(%20%3Fname%20)%2C%20%22EN%22%20))%0D%0A%20%20%3Fanimal%20dbpedia2%3Aname%20%3Fname%3B%0D%0A%20%20%20%20foaf%3Adepiction%20%3Fimg%3B%0D%0A%20%20%20%20dbpedia2%3Aregnum%20%3Fkingdom%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Aordo%20%3Forder%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Aphylum%20%3Fphylum%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Aclassis%20%3Fclass%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Afamilia%20%3Ffamily%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Agenus%20%3Fgenus%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Aspecies%20%3Fspecies%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%20%3Fanimal%20dbpedia2%3Asubspecies%20%3Fsubspecies%20.%20%7D%0D%0A%20%20OPTIONAL%20%7B%0D%0A%20%20%20FILTER%20(%20langMatches(%20lang(%20%3Fabstract%20)%2C%20%22EN%22%20))%0D%0A%20%20%7D%0D%0A%20%7D%0D%0A%20GROUP%20BY%20%3Fname%0D%0A%20LIMIT%2050%0D%0A";

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String animal = ed1.getText().toString();

                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                WebView myWebView = (WebView) findViewById(R.id.webview);
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.loadUrl(urlInicio+animal+urlFinal);

            }
        });

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // cambiar url!!!
            return true;
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

}
