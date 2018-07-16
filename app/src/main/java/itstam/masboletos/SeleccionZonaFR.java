package itstam.masboletos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SeleccionZonaFR extends Fragment {
    View vista;
    public static final String TAG = MyApplication.class.getSimpleName();
    String [] funciones, zonas,colores, precios, disponibilidad, subzonas,idevento_funcion,numerado,idsubzonas,comision;
    JSONArray Elementos=null;
    String idevento,_zona,id_seccionXevento;
    int indiceZona;
    DatosCompra datosCompra= new DatosCompra();
    Spinner spzona,spseccion;
    Button btContinuar;
    SelZonaList selZonaList;
    String seccion_compra,costo_compra,asiento_compra,tipomsj,msj,cantidadBoletos;

    public SeleccionZonaFR() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.seleccion_zonafr, container, false);
        spzona=(Spinner)vista.findViewById(R.id.spzona);
        spseccion=(Spinner)vista.findViewById(R.id.spseccion);
        btContinuar=(Button)vista.findViewById(R.id.btContinuar2);

        return vista;
    }

    public void Recibir_Funcion_CBol(){
        Toast.makeText(getActivity(),"Recarga de Fragment",Toast.LENGTH_SHORT).show();
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=(prefe.getString("idevento",""));
        cantidadBoletos=(prefe.getString("Cant_boletos",""));
        obtener_zonas();
    }

    void obtener_zonas(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getZonasxEvento.php?idevento="+idevento);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                zonas= new String[Elementos.length()];
                                colores= new String[Elementos.length()];
                                precios= new String[Elementos.length()];
                                disponibilidad= new String[Elementos.length()];
                                numerado= new String[Elementos.length()];
                                comision= new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    zonas[i]=datos.getString("grupo");
                                    colores[i]=datos.getString("color");
                                    precios[i]=datos.getString("precio");
                                    disponibilidad[i]=datos.getString("disponibilidad");
                                    numerado[i]=datos.getString("numerado");
                                    comision[i]=datos.getString("comision");
                                }
                                spinner_zonas();
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

    public void spinner_zonas(){
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_2,zonas);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spzona.setAdapter(adapter);
        spzona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indiceZona=position;
                String txtsel="";
                txtsel=(String) parent.getItemAtPosition(position);
                _zona=txtsel.replace(" ","%20");
                obtener_secciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void obtener_secciones(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getSubzonasxGrupo.php?idevento="+idevento+"&grupo="+_zona);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                subzonas= new String[Elementos.length()+1];
                                idsubzonas= new String[Elementos.length()+1];
                                subzonas[0]="Mejor disponible";
                                idsubzonas[0]="0";
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    subzonas[i+1]=datos.getString("nombre");
                                    idsubzonas[i+1]=datos.getString("idzona");
                                }
                                spinner_seccion();
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

    void spinner_seccion(){
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item_2,subzonas);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spseccion.setAdapter(adapter);
        spseccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_seccionXevento=idsubzonas[position];
                Log.e("id:seccion",id_seccionXevento);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mejor_Disponible();
            }
        });
    }

    void Mejor_Disponible(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getBoletos.php?idevento="+idevento+"&numerado="+numerado[indiceZona]+"&zona="+_zona+"&CantBoletos="+cantidadBoletos+"&idzonaxgrupo="+id_seccionXevento);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray("["+resultado+"]");
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    seccion_compra=datos.getString("mensagesetDescripcion");
                                    costo_compra=datos.getString("mensagesetImporteBoleto");
                                    asiento_compra=datos.getString("mensagesetAsientos");
                                    tipomsj=datos.getString("mensagesetTipo");
                                    msj=datos.getString("mensagesetMensage");
                                    }
                                if(tipomsj.equals("1")) {
                                        ((DetallesEventos) getActivity()).next_page();
                                        selZonaList.setSelZona(numerado[indiceZona],zonas[indiceZona],id_seccionXevento,precios[indiceZona],comision[indiceZona]);
                                    }else {
                                    Toast.makeText(getActivity(),msj+"\nSolicite una cantidad diferente o verifique la zona",Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                    }});  //permite trabajar con la interfaz grafica
               }};
        tr.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            selZonaList=(SelZonaList)context;
        }catch (Exception e){}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"OnDettach");
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG,"onDestroyView");
        super.onDestroyView();
    }

    public String inserta(String enlace){ // metodo que inserta los parametros en la BD
        URL url = null;
        Log.d("Enlace ",enlace);
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

    public interface SelZonaList{
        public void setSelZona(String numerado, String zona, String idzonaxgrupo, String precio, String comision);
    }
}
