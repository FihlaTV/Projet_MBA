package fr.norips.ar.ARMuseum;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.artoolkit.ar.base.camera.CaptureCameraPreview;

import fr.norips.ar.ARMuseum.Config.JSONParser;


public class Presentation extends Activity {
    final static String TAG = "Presentation";
    final static String DOMAIN = "norips.me";
    private final static int REQUEST_WRITE = 1;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        url = myIntent.getStringExtra("url"); // will return "FirstKeyValue"
        setContentView(R.layout.activity_presentation);
        Button bv = (Button) findViewById(R.id.btLaunch);
        bv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Click");
                Intent myIntent = new Intent(Presentation.this, ARMuseumActivity.class);
                Presentation.this.startActivity(myIntent);
            }
        });
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    if (this.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        // Will drop in here if user denied permissions access camera before.
                        // Or no uses-permission CAMERA element is in the
                        // manifest file. Must explain to the end user why the app wants
                        // permissions to the camera devices.
                        Toast.makeText(this.getApplicationContext(),
                                "App requires access to write external storage to be granted",
                                Toast.LENGTH_SHORT).show();
                    }
                    // Request permission from the user to access the camera.
                    Log.i(TAG, "Presentation(): must ask user for write external storage access permission");
                    this.requestPermissions(new String[]
                                    {
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    },
                            REQUEST_WRITE);
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Presentation(): exception caught, " + ex.getMessage());
            return;
        }
        JSONParser json = new JSONParser(this.getApplicationContext(),bv);
        boolean result = json.createConfig(url, "http://"+ DOMAIN +"/format_mult.json");
        Log.d(TAG,"JSON create" + result);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult(): called");
        if (requestCode == REQUEST_WRITE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not run with folder access denied",
                        Toast.LENGTH_LONG).show();
            }
            else if (1 <= permissions.length) {
                Toast.makeText(getApplicationContext(),
                        String.format("Reading file access permission \"%s\" allowed", permissions[0]),
                        Toast.LENGTH_SHORT).show();
                JSONParser json = new JSONParser(this.getApplicationContext(),(Button) findViewById(R.id.btLaunch));
                boolean result = json.createConfig(url, "http://"+ DOMAIN +"/format.json");
                Log.d(TAG,"JSON request " + result);
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
