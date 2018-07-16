package itstam.masboletos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import butterknife.ButterKnife;

public class ComprarBoletoFr extends Fragment implements View.OnClickListener {


    // TODO: Rename and change types of parameters
    TextView txvCantidad,txvprecio;
    ImageButton BtMas,BtMenos;
    int cantidadBoletos=0, indiceZona=0;
    String idevento,eventogrupo,NombreEvento;
    TextView TXVSFuncion,TXVEFuncion;
    Spinner spfuncion; LinearLayout LLYZonas;
    View vista; JSONArray Elementos=null;
    String [] funciones,idevento_funcion;
    Button btmDisponible;
    Funcion_NumBolListener funcion_numBolListener;


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
        TXVEFuncion=(TextView)vista.findViewById(R.id.TXVEFuncion);
        spfuncion=(Spinner) vista.findViewById(R.id.spFuncion);
        btmDisponible=(Button)vista.findViewById(R.id.btmdisponible);
        btmDisponible.setClickable(false);

        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=(prefe.getString("idevento",""));
        eventogrupo=(prefe.getString("eventogrupo",""));
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
        if(cantidadBoletos==0){ BtMenos.setClickable(false);}
        if(eventogrupo.equals("0")){
            TXVEFuncion.setVisibility(View.GONE);
            spfuncion.setVisibility(View.GONE);
        }else{
            BtMas.setVisibility(View.INVISIBLE);
            BtMenos.setVisibility(View.INVISIBLE);
            Consulta_Funciones_Evento();
            BtMenos.setClickable(false);
        }
    }

    void Consulta_Funciones_Evento(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getFuncionesxEvento.php?eventogrupo="+eventogrupo);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                funciones= new String[Elementos.length()+1];
                                idevento_funcion= new String[Elementos.length()+1];
                                funciones[0]="..Seleccione una función.."; idevento_funcion[0]="000";
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
                    }
                });  //permite trabajar con la interfaz grafica
            }
        };
        tr.start();
    }

    void spinner_funcion(){
        ArrayAdapter adapter2 = new ArrayAdapter(getActivity(), R.layout.spinner_item_2,funciones);
        adapter2.setDropDownViewResource(R.layout.spinner_lista2);
        spfuncion.setAdapter(adapter2);
        spfuncion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemIdAtPosition(position)!=0){
                    idevento=idevento_funcion[position];
                    BtMas.setVisibility(View.VISIBLE);
                    BtMenos.setVisibility(View.VISIBLE);
                }else{
                    btmDisponible.setClickable(false);
                    txvCantidad.setText("0");
                    BtMas.setVisibility(View.INVISIBLE);
                    BtMenos.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            funcion_numBolListener=(Funcion_NumBolListener)context;
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
                btmDisponible.setClickable(true);
            }
        }
        if(v==BtMenos){
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            cantidadBoletos--;
            txvCantidad.setText(String.valueOf(cantidadBoletos));
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            if (cantidadBoletos==0){
                BtMenos.setClickable(false);
                btmDisponible.setClickable(false);
            } else{
                BtMenos.setClickable(true);
                btmDisponible.setClickable(true);
                txvCantidad.setText(String.valueOf(cantidadBoletos));
            }
        }
        if(v==btmDisponible){
            Log.e("BTMD","pulsado");
            funcion_numBolListener.setFuncion_NumBol(idevento,txvCantidad.getText().toString());
            ((DetallesEventos) getActivity()).next_page();
        }
    }


    public interface Funcion_NumBolListener{
        public void setFuncion_NumBol(String funcion,String Cant_Boletos);
    }

}
