package felipegonzalez.com.ciervogo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class OptionsMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);
    }


    public void getProfile(View view)
    {
        Intent intent = new Intent(OptionsMenu.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void getExplorer(View view)
    {
        Intent intent = new Intent(OptionsMenu.this, ExplorerActivity.class);
        startActivity(intent);
    }

}
