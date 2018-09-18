package itstam.masboletos.acciones_perfil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import itstam.masboletos.R;
import itstam.masboletos.carruselcompra.DetallesEventos;
import itstam.masboletos.carruselcompra.FRFinalizarCompra;

public class buzonsuger extends AppCompatActivity {

    Spinner sptipomsj,spciudad,spservicio;
    String [] Estados,IDEdo;
    JSONArray Elementos;
    ProgressDialog dialogcarg;
    Button btenviar;
    EditText edtnombre,edtcorreo,edttelefono,edtmsj;
    String tipomsjst,ciudadst,serviciost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzonsuger);
        sptipomsj=findViewById(R.id.sptipomsj);
        spciudad=findViewById(R.id.spciudad);
        spservicio=findViewById(R.id.spservicio);
        btenviar=findViewById(R.id.btenviarbuzon);
        edtnombre=findViewById(R.id.edtnombrebuzon);
        edtcorreo=findViewById(R.id.edtcorreobuzon);
        edttelefono=findViewById(R.id.edttelbuzon);
        edtmsj=findViewById(R.id.edtmsjbuzon);
        set_spinner();
        Consulta_Estados();

        btenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sptipomsj.getSelectedItemPosition()==0){
                    Toast.makeText(getApplicationContext(),"Debe seleccionar un tipo de mensaje",Toast.LENGTH_SHORT).show();
                }else if(spciudad.getSelectedItemPosition()==0){
                    Toast.makeText(getApplicationContext(),"Debe seleccionar una ciudad",Toast.LENGTH_SHORT).show();
                }else if(spservicio.getSelectedItemPosition()==0){
                    Toast.makeText(getApplicationContext(),"Debe seleccionar un tipo de servicio",Toast.LENGTH_SHORT).show();
                }else if(edtnombre.getText().length()<5){
                    Toast.makeText(getApplicationContext(),"Debe ingresar un nombre válido",Toast.LENGTH_SHORT).show();
                }else if (!edtcorreo.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") && edtcorreo.getText().length()==0) {
                        Toast.makeText(getApplicationContext(),"Verifique la estructura de su correo",Toast.LENGTH_SHORT).show();
                }else if(edttelefono.getText().length()!=10){
                    Toast.makeText(getApplicationContext(),"Debe ingresar un télefono válido",Toast.LENGTH_SHORT).show();
                }else if(edtmsj.getText().length()<10){
                    Toast.makeText(getApplicationContext(),"Debe escribir un mensaje válido",Toast.LENGTH_SHORT).show();
                }else {
                    mandar_mensaje("https://www.masboletos.mx/mailAPP.php");
                }
            }
        });
    }

    void set_spinner(){
        final String[] tipomsj = {"Tipo de Mensaje...","Sugerencia","Queja"};
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item_2,tipomsj);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        sptipomsj.setAdapter(adapter);
        sptipomsj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                tipomsjst=tipomsj[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final String[] servicio = {"Servicio...","Aplicación","Página Web"};
        adapter = new ArrayAdapter(this, R.layout.spinner_item_2,servicio);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spservicio.setAdapter(adapter);
        spservicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                serviciost=servicio[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                ciudadst=Estados[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void mandar_mensaje(String url){
        iniciar_cargando();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        cerrar_cargando();
                        Log.e("respenvia",response);
                        if(response.equals("1")){
                            cerrar_teclado();
                            Toast.makeText(getApplicationContext(),"Su mensaje ha sido enviado, gracias por sus comentarios",Toast.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"Ha ocurrido un error en el envio, intente más tarde",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        cerrar_cargando();
                        finish();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tipomensaje", tipomsjst);
                params.put("ciudad", ciudadst);
                params.put("servicio", serviciost);
                params.put("nombre", edtnombre.getText().toString());
                params.put("correo", edtcorreo.getText().toString());
                params.put("telefono", edttelefono.getText().toString());
                params.put("mensaje", edtmsj.getText().toString());
                return params;
            }
        };
        queue.add(strRequest);
    }

    void cerrar_teclado(){
        View vista = this.getCurrentFocus();
        if(vista != null){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(vista.getWindowToken(),0);
        }
    }

    public void regresar(View view){
        finish();
    }

    public void iniciar_cargando(){
        dialogcarg= new ProgressDialog(this,R.style.ProgressDialogStyle);
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
