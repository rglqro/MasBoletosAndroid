package itstam.masboletos;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ComprarBoletoFr extends Fragment implements View.OnClickListener {


    // TODO: Rename and change types of parameters
    TextView txvCantidad;
    ImageButton BtMas,BtMenos;
    int cantidadBoletos=0;
    String idevento,eventogrupo;
    TextView TXVSFuncion,TXVEFuncion,TXVSFuncion2;
    Spinner spfuncion;    View vista; JSONArray Elementos=null;
    String [] funciones,idevento_funcion;
    Button btmDisponible;
    SharedPreferences prefe;


    public ComprarBoletoFr() {
        // Required empty public constructor
    }


    private static final String TAG = ComprarBoletoFr.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_comprar_boleto, container, false);
        txvCantidad=(TextView)vista.findViewById(R.id.txvCantidad);
        BtMas=(ImageButton)vista.findViewById(R.id.BtMas);
        BtMenos=(ImageButton)vista.findViewById(R.id.BtMenos);
        TXVSFuncion=(TextView)vista.findViewById(R.id.TXVSFuncion);
        TXVSFuncion2=(TextView)vista.findViewById(R.id.TXVSFuncion2);
        TXVEFuncion=(TextView)vista.findViewById(R.id.TXVEFuncion);
        spfuncion=(Spinner) vista.findViewById(R.id.spFuncion);
        btmDisponible=(Button)vista.findViewById(R.id.btmdisponible);
        btmDisponible.setBackgroundResource(R.color.grisclaro);

        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=(prefe.getString("idevento",""));
        eventogrupo=(prefe.getString("eventogrupo",""));
        cantidadBoletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        txvCantidad.setText(String.valueOf(cantidadBoletos));
        inicio_interfaz();

        return vista;
    }

    void inicio_interfaz(){
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=(prefe.getString("idevento",""));
        eventogrupo=prefe.getString("eventogrupo","");
        BtMas.setOnClickListener(this);
        BtMenos.setOnClickListener(this);
        btmDisponible.setOnClickListener(this);
        if(cantidadBoletos==0){ BtMenos.setClickable(false);} else{ BtMenos.setClickable(true);btmDisponible.setBackgroundResource(R.color.verdemb);}
        if(eventogrupo.equals("0")){
            TXVSFuncion2.setVisibility(View.GONE);
            TXVEFuncion.setVisibility(View.GONE);
            spfuncion.setVisibility(View.GONE);
        }else{
            BtMas.setClickable(false);
            BtMenos.setClickable(false);
            Consulta_Funciones_Evento();
        }
    }

    void Consulta_Funciones_Evento(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="http://www.masboletos.mx/appMasboletos/getFuncionesxEvento.php?eventogrupo="+eventogrupo;
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            funciones= new String[Elementos.length()+1];
                            idevento_funcion= new String[Elementos.length()+1];
                            funciones[0]="..Selecciona.."; idevento_funcion[0]="000";
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                funciones[i+1]=datos.getString("FechaLarga")+" "+datos.getString("hora");
                                idevento_funcion[i+1]=datos.getString("idevento_funcion");
                            }
                            spinner_funcion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void spinner_funcion(){
        ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), R.layout.spinner_item_2,funciones);
        adapter2.setDropDownViewResource(R.layout.spinner_lista2);
        spfuncion.setAdapter(adapter2);
        spfuncion.setSelection(Integer.parseInt(prefe.getString("posEve","0")));
        ((DetallesEventos)getActivity()).cerrar_cargando();
        spfuncion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemIdAtPosition(position)!=0){
                    idevento=idevento_funcion[position];
                    set_DatosCompra("posEve",String.valueOf(position));
                    BtMas.setClickable(true);
                    BtMenos.setClickable(true);
                }else{
                    btmDisponible.setBackgroundResource(R.color.grisclaro);
                    txvCantidad.setText("0");
                    BtMas.setClickable(false);
                    BtMenos.setClickable(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

        }catch (Exception e){}
    }

    @Override
    public void onDetach() {
        Log.d("detachCB","detach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d("onDestroyViewCB","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroyCB","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v==BtMas){
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            cantidadBoletos++;
            txvCantidad.setText(String.valueOf(cantidadBoletos));
            if(cantidadBoletos>0){
                BtMenos.setClickable(true);
                btmDisponible.setBackgroundResource(R.color.verdemb);
            }
        }
        if(v==BtMenos){
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            cantidadBoletos--;
            txvCantidad.setText(String.valueOf(cantidadBoletos));
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            if (cantidadBoletos==0){
                BtMenos.setClickable(false);
                btmDisponible.setBackgroundResource(R.color.grisclaro);
            } else{
                BtMenos.setClickable(true);
                btmDisponible.setBackgroundResource(R.color.verdemb);
                txvCantidad.setText(String.valueOf(cantidadBoletos));
            }
        }
        if(v==btmDisponible){
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            if(cantidadBoletos>0) {
                Log.e("BTMD", "pulsado");
                ((DetallesEventos) getActivity()).replaceFragment(new SeleccionZonaFR());
                set_DatosCompra("idevento", idevento);
                set_DatosCompra("Cant_boletos", txvCantidad.getText().toString());
            }else if(spfuncion.getSelectedItemPosition()==0 && !eventogrupo.equals("0")){
                ((DetallesEventos)getActivity()).AlertaBoton("Evento","Debe elegir un Evento").show();
            }else{
                ((DetallesEventos)getActivity()).AlertaBoton("Cantidad Boletos","Debe elegir al menos un boleto").show();
            }
        }
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
