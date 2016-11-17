package fr.norips.ar.ARMuseum;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class Presentation extends Activity {
    final static String TAG = "Presentation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        Button bv = (Button) findViewById(R.id.btLaunch);
        bv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Click");
            }
        });
    }
}
