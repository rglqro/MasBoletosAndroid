package itstam.masboletos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UbicacionAct extends AppCompatActivity {

    Spinner spin_edos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        spin_edos=(Spinner) findViewById(R.id.spEstados);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.EstadosRep, R.layout.spinner_item_2);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spin_edos.setAdapter(adapter);
    }


    public void regresar(View view) {
        finish();
    }
}
