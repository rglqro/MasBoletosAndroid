package itstam.masboletos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class SeleccionZonaFR extends Fragment {
    View vista;
    public static final String TAG = SeleccionZonaFR.class.getSimpleName();
    String [] funciones, zonas,colores, precios, disponibilidad, subzonas,idevento_funcion,numerado,idsubzonas,comision,zona_precio;
    JSONArray Elementos=null;
    String idevento,_zona,id_seccionXevento, URLMapa;
    int indiceZona;
    Spinner spzona,spseccion;
    Button btContinuar;
    String seccion_compra,costo_compra,asiento_compra,tipomsj,msj,cantidadBoletos,fila;
    ImageView IMVMApa;
    Dialog customDialog = null;

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
        IMVMApa=(ImageView)vista.findViewById(R.id.IMVMapa);
        Recibir_Funcion_CBol();
        return vista;
    }

    public void Recibir_Funcion_CBol(){
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=(prefe.getString("idevento",""));
        cantidadBoletos=(prefe.getString("Cant_boletos",""));
        obtener_zonas();
    }

    void obtener_zonas(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
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
                                zona_precio= new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    zonas[i]=datos.getString("grupo");
                                    zona_precio[i]=datos.getString("grupo")+" $"+datos.getString("precio")+" c/u" +
                                            "\nDisponibles: "+datos.getString("disponibilidad");
                                    colores[i]=datos.getString("color");
                                    precios[i]=datos.getString("precio");
                                    disponibilidad[i]=datos.getString("disponibilidad");
                                    numerado[i]=datos.getString("numerado");
                                    comision[i]=datos.getString("comision");
                                    URLMapa="http://www.masboletos.mx/sica/imgEventos/"+datos.getString("EventoMapam");
                                }
                                spinner_zonas();
                                Mostrar_Mapa();
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

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void Mostrar_Mapa(){
        pintar_imagen(URLMapa,IMVMApa);
        IMVMApa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog = new Dialog(getActivity());
                //deshabilitamos el título por defecto
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //obligamos al usuario a pulsar los botones para cerrarlo
                //establecemos el contenido de nuestro dialog
                customDialog.setContentView(R.layout.dialog_custom_layout);
                PhotoView photoView = customDialog.findViewById(R.id.IMVMapaZoom);
                photoView.setAdjustViewBounds(true);
                photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                pintar_imagen(URLMapa,photoView);
                customDialog.show();
                Window window = customDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                Instruccion_Zoom();
            }
        });
    }

    void Instruccion_Zoom(){
        // cuadro de dialogo que se abre si no se envio ningun sms o no hubo respuesta del servidor para ingresar el numero de celular manualmente
        // con este tema personalizado evitamos los bordes por defecto
        customDialog = new Dialog(getActivity());
        //deshabilitamos el título por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        //establecemos el contenido de nuestro dialog
        customDialog.setContentView(R.layout.gifzoom);

        customDialog.show();
        Window window = customDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TimerTask task = new TimerTask(){
            public void run() {
                customDialog.dismiss();
            }
        };
        // cuenta regresiva para cerrar el activity
        Timer timer = new Timer();
        timer.schedule(task,2000);
    }

    void pintar_imagen(String urlMapa,ImageView imag){
        Picasso.get()
                .load(urlMapa)
                .error(R.drawable.ic_inicio)
                .into(imag);
    }


    public void spinner_zonas(){
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_2,zona_precio);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spzona.setAdapter(adapter);
        ((DetallesEventos)getActivity()).cerrar_cargando();
        spzona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indiceZona=position;
                String txtsel="";
                txtsel=(String) zonas[position];
                _zona=txtsel.replace(" ","%20");
                obtener_secciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void obtener_secciones(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
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
        ((DetallesEventos)getActivity()).cerrar_cargando();
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
        ((DetallesEventos)getActivity()).iniciar_cargando();
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
                                    fila=datos.getString("mensagesetNombrePuerta") ;
                                }
                                if(tipomsj.equals("1")) {
                                        set_DatosCompra("zona",zonas[indiceZona]);
                                        set_DatosCompra("precio",precios[indiceZona]);
                                        set_DatosCompra("comision",comision[indiceZona]);
                                        set_DatosCompra("fila",fila);
                                        set_DatosCompra("asientos",asiento_compra);
                                        ((DetallesEventos) getActivity()).replaceFragment(new FRMejDisp());
                                    }else {
                                    ((DetallesEventos)getActivity()).cerrar_cargando();
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

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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

}