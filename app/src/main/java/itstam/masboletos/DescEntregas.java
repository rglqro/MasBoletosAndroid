package itstam.masboletos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DescEntregas extends AppCompatActivity {

    TextView txvdescentre,txvtitentr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc__entregas);
        txvdescentre=(TextView)findViewById(R.id.txvdescentre);
        txvtitentr=(TextView)findViewById(R.id.txvtitentr);
        Bundle bundle = getIntent().getExtras();
        String dato=bundle.getString("descripcionentrega","");
        txvdescentre.setText(dato);
        txvtitentr.setText(bundle.getString("tituloentrega",""));
    }


    public void cerrar(View v){
        finish();
    }
}
