package itstam.masboletos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import itstam.masboletos.principal.MainActivity;

public class Splash_Principal extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    final int PERMISSION_ALL = 112;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configura que la pantalla esté en vertical siempre
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        // oculta la barra de titulo de la app
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash__principal);
        permisos();
    }

    void permisos(){
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else {
            iniciar_activity();
        }
    }

    void iniciar_activity(){
        TimerTask task = new TimerTask(){
            public void run() {

                // Inicia la siguiente pantalla
                Intent mainIntent = new Intent().setClass(
                        Splash_Principal.this, MainActivity.class);
                startActivity(mainIntent);

                // Cierra el activity para no abrirlo de nuevo

                finish();
            }


        };
        // cuenta regresiva para cerrar el activity
        Timer timer = new Timer();
        timer.schedule(task,SPLASH_SCREEN_DELAY);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    iniciar_activity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(getApplicationContext(), "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            }
        }

    }


}
