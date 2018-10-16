package itstam.masboletos;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UbicacionAct extends AppCompatActivity {

    Spinner spin_edos;
    FrameLayout FRLYMapa;
    JSONArray Elementos=null;
    int ancho,alto;
    String [] Estados,IDEdo,latLngs,idpuntoventa;
    MAPSFR mapsfr= new MAPSFR();
    String Edo_Sel; ProgressDialog dialogcarg;
    RelativeLayout rlinfopunto;
    TextView txvcerrarinfo,txvinfomar,txvnpunto;
    ImageView imvpunto;
    TextView txvcomollegar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        FRLYMapa=(FrameLayout)findViewById(R.id.FRLYMapa);
        spin_edos=(Spinner) findViewById(R.id.spEstados);
        rlinfopunto=(RelativeLayout)findViewById(R.id.rlinfopunto);
        txvcerrarinfo=(TextView)findViewById(R.id.txvcerrarinfo);
        txvinfomar=(TextView)findViewById(R.id.txvinfomar);
        txvnpunto=(TextView)findViewById(R.id.txvnpunto);
        imvpunto=(ImageView)findViewById(R.id.imvpunto);
        txvcomollegar=(TextView)findViewById(R.id.txvcomollegar);
        rlinfopunto.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;

        imvpunto.getLayoutParams().width=ancho/4;
        status_gps();
    }

    public void status_gps() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Ubicación no activa, ¿Desea activarla?");
            dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    finish();
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
            dialog.show();
        }else {
            Consulta_Estados();
        }
    }


    void Consulta_Estados(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos.fueralinea/getEstados.php";
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            Estados= new String[Elementos.length()];
                            IDEdo= new String[Elementos.length()];
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                IDEdo[i]=datos.getString("idEstado");  Estados[i]=datos.getString("estado");
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
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item_2,Estados);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spin_edos.setAdapter(adapter);
        spin_edos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Consulta_TiendaXedo(IDEdo[position]); //Obtiene el ID del estado de acuerdo a la opcion seleccionada
                Edo_Sel=Estados[position];
                if(rlinfopunto.getVisibility()==View.VISIBLE){
                    rlinfopunto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void Consulta_TiendaXedo(final String id_edo){
        iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos.fueralinea/getPuntosVentaxEstado.php?idestado="+id_edo;
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            latLngs= new String[Elementos.length()];
                            idpuntoventa= new String[Elementos.length()];
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                latLngs[i]= datos.getString("lat")+","+datos.getString("lon");
                                idpuntoventa[i]=datos.getString("idpuntoventa");
                            }
                            if(latLngs.length>0) {
                                llenardatosmapa();
                                msj_alerta("Deberá seleccionar un marcador para ver los detalles del punto de venta");
                            }else {
                                if(FRLYMapa!=null){
                                    FRLYMapa.removeAllViews();
                                }
                                getSupportFragmentManager().beginTransaction().detach(mapsfr).commit();
                                Toast.makeText(getApplicationContext(),"No hay tiendas disponibles en "+Edo_Sel+" por el momento",Toast.LENGTH_SHORT).show();
                            }
                            cerrar_cargando();
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
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void llenardatosmapa(){
        Bundle bundle=new Bundle();
        bundle.putStringArray("latLngs",latLngs);
        mapsfr= new MAPSFR();
        mapsfr.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.FRLYMapa,mapsfr).commit();
        getSupportFragmentManager().beginTransaction().detach(mapsfr).commit();
        getSupportFragmentManager().beginTransaction().attach(mapsfr).commit();
    }

    void msj_alerta(String txt){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(txt)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void regresar(View view) {
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
