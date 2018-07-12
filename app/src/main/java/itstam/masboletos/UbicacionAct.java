package itstam.masboletos;

import android.app.Fragment;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

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
    String [] Estados,IDEdo,latLngs;
    MAPSFR mapsfr= new MAPSFR();
    String Edo_Sel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        FRLYMapa=(FrameLayout)findViewById(R.id.FRLYMapa);
        spin_edos=(Spinner) findViewById(R.id.spEstados);

        Consulta_Estados();
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

    void Consulta_Estados(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getEstados.php");  //para que la variable sea reconocida en todos los metodos
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
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
                    }
                });  //permite trabajar con la interfaz grafica
            }
        };
        tr.start();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void Consulta_TiendaXedo(final String id_edo){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getPuntosVentaxEstado.php?idestado="+id_edo);  //para que la variable sea reconocida en todos los metodos
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                latLngs= new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    latLngs[i]= datos.getString("lat")+","+datos.getString("lon");
                                }
                                if(latLngs.length>0) {
                                    llenardatosmapa();
                                }else {
                                    getSupportFragmentManager().beginTransaction().detach(mapsfr).commit();
                                    Toast.makeText(getApplicationContext(),"No hay tiendas disponibles en "+Edo_Sel+" por el momento",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });  //permite trabajar con la interfaz grafica
            }
        };
        tr.start();
    }

    public void regresar(View view) {
        finish();
    }

    public String inserta(String enlace){ // metodo que inserta los parametros en la BD

        URL url = null;
        int respuesta = 0;
        String linea = "",valor="";
        StringBuilder resul = null;
        try {
            url = new URL(enlace);
            HttpURLConnection conection;
            conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((linea = reader.readLine()) != null) {
                    resul.append(linea);
                }
            }
            if(resul!=null) {
                valor = resul.toString();
            }
        } catch (Exception e) {
            //resul.append("Error ----");
        }
        Log.d("Resultado pagina",valor);
        return valor;
    }

    public int validadatos(String response){
        int respuesta = 0;
        if (response.length()>0){
            respuesta=1;
        }
        return respuesta;
    }
}
