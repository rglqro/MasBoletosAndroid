package itstam.masboletos.acciones_perfil;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import itstam.masboletos.R;

public class buzonsuger extends AppCompatActivity {

    Spinner sptipomsj,spciudad,spservicio;
    String [] Estados,IDEdo;
    JSONArray Elementos;
    ProgressDialog dialogcarg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzonsuger);
        sptipomsj=findViewById(R.id.sptipomsj);
        spciudad=findViewById(R.id.spciudad);
        spservicio=findViewById(R.id.spservicio);
        set_spinner();
        Consulta_Estados();
    }

    void set_spinner(){
        String[] tipomsj = {"Tipo de Mensaje...","Sugerencia","Queja"};
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item_2,tipomsj);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        sptipomsj.setAdapter(adapter);

        String[] servicio = {"Servicio...","Aplicación","Pagina Web"};
        adapter = new ArrayAdapter(this, R.layout.spinner_item_2,servicio);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spservicio.setAdapter(adapter);
    }

    void Consulta_Estados(){
        // Initialize a new RequestQueue instance
        iniciar_cargando();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos/getEstados.php";
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            Estados= new String[Elementos.length()+1];
                            IDEdo= new String[Elementos.length()+1];
                            Estados[0]="Ciudad...";IDEdo[0]="0";
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                IDEdo[i+1]=datos.getString("idEstado");  Estados[i+1]=datos.getString("estado");
                            }
                            llenar_spinner_Estados();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error en la consulta: \n"+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void llenar_spinner_Estados(){
        cerrar_cargando();
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item_2,Estados);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spciudad.setAdapter(adapter);
        spciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void regresar(View view){
        finish();
    }

    public void iniciar_cargando(){
        dialogcarg= new ProgressDialog(this);
        dialogcarg.setTitle("Cargando información");
        dialogcarg.setMessage("  Espere...");
        dialogcarg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogcarg.setCancelable(false);
        dialogcarg.show();
    }

    public void cerrar_cargando(){
        dialogcarg.dismiss();
    }
}
