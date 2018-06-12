package itstam.masboletos;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class Splash_Principal extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configura que la pantalla esté en vertical siempre
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        // oculta la barra de titulo de la app
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash__principal);
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
}
