package itstam.masboletos.acciones_perfil;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import itstam.masboletos.R;

public class acercade extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acercade);
    }

    public void regresar(View view){
        finish();
    }
}
