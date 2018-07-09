package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComprarBoletoFr.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComprarBoletoFr#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComprarBoletoFr extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView txvCantidad,txvprecio;
    ImageButton BtMas,BtMenos;
    int cantidadBoletos=0, indiceZona=0;
    String idevento,eventogrupo,_zona;
    TextView TXVSFuncion,TXVEFuncion;
    ImageView IMVMapa; Spinner spfuncion,spseccion; LinearLayout LLYZonas;
    RadioButton[] RBZonas; RadioGroup RGZonas=null;
    View vista; JSONArray Elementos=null;
    String [] funciones, zonas,colores, precios, disponibilidad, secciones;
    Button btCaptcha;

    private OnFragmentInteractionListener mListener;

    public ComprarBoletoFr() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComprarBoletoFr.
     */
    // TODO: Rename and change types and number of parameters
    public static ComprarBoletoFr newInstance(String param1, String param2) {
        ComprarBoletoFr fragment = new ComprarBoletoFr();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final String TAG = ComprarBoletoFr.class.getSimpleName();
    // TODO - replace the SITE KEY with yours
    private static final String SAFETY_NET_API_SITE_KEY = "6LentGIUAAAAAKuqtOecg0H0TRdZikpyUj_39F0f";
    // TODO - replace the SERVER URL with yours
    private static final String URL_VERIFY_ON_SERVER = "http://www.masboletos.mx/appMasboletos/captchaVerificated.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_comprar_boleto, container, false);
        btCaptcha=(Button)vista.findViewById(R.id.btCaptcha);
        btCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCaptcha();
            }
        });
        ButterKnife.bind(getActivity());
        txvCantidad=(TextView)vista.findViewById(R.id.txvCantidad);
        txvprecio=(TextView)vista.findViewById(R.id.txvPrecio);
        BtMas=(ImageButton)vista.findViewById(R.id.BtMas);
        BtMenos=(ImageButton)vista.findViewById(R.id.BtMenos);
        IMVMapa =(ImageView)vista.findViewById(R.id.IMVMapa);
        LLYZonas=(LinearLayout)vista.findViewById(R.id.LLYZonas);
        TXVSFuncion=(TextView)vista.findViewById(R.id.TXVSFuncion);
        TXVEFuncion=(TextView)vista.findViewById(R.id.TXVEFuncion);
        spfuncion=(Spinner) vista.findViewById(R.id.spFuncion);
        spseccion=(Spinner) vista.findViewById(R.id.spseccion);

        idevento=getArguments().getString("idevento");
        eventogrupo=getArguments().getString("eventogrupo");
        inicio_interfaz();


        return vista;
    }

    void inicio_interfaz(){
        BtMas.setOnClickListener(this);
        BtMenos.setOnClickListener(this);
        Picasso.with(getActivity())
                .load("http://www.masboletos.mx/sica/imgEventos/MAPA.NEW-CLR.jpg")
                .error(R.drawable.mblogo)
                .into(IMVMapa);
        if(cantidadBoletos==0){ BtMenos.setClickable(false);}
        if(eventogrupo.equals("0")){
            obtener_zonas();
            TXVSFuncion.setVisibility(View.GONE); TXVEFuncion.setVisibility(View.GONE);
            spfuncion.setVisibility(View.GONE);
        }else{
            spseccion.setVisibility(View.INVISIBLE);
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
                                funciones[0]="..Seleccione una función..";
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    funciones[i+1]=datos.getString("FechaLarga")+" "+datos.getString("hora");
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
                    obtener_zonas();
                    BtMas.setVisibility(View.VISIBLE);
                    BtMenos.setVisibility(View.VISIBLE);
                }else{
                    if (RGZonas!=null) {
                        RGZonas.removeAllViews();
                    }
                    txvCantidad.setText("0");
                    spseccion.setVisibility(View.INVISIBLE);
                    BtMas.setVisibility(View.INVISIBLE);
                    BtMenos.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    zonas[i]=datos.getString("grupo");
                                    colores[i]=datos.getString("color");
                                    precios[i]=datos.getString("precio");
                                    disponibilidad[i]=datos.getString("disponibilidad");
                                }
                                generar_zonas();
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

    @SuppressLint("ResourceAsColor")
    void generar_zonas(){
        if (RGZonas!=null) {
            RGZonas.removeAllViews();
        }
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RBZonas = new RadioButton[zonas.length];
        RGZonas = new RadioGroup(getActivity());
        RGZonas.setLayoutParams(lp);
        RGZonas.setPadding(10,0,10,15);
        for (int i=0;i<RBZonas.length;i++){
            RBZonas[i] = new RadioButton(getActivity());
            RBZonas[i].setText(zonas[i]+" \nDisponbles: "+disponibilidad[i]);
            RBZonas[i].setTextSize(20);
            RBZonas[i].setTextColor(Color.BLACK);
            RBZonas[i].setId(i);
            RBZonas[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cantidadBoletos=0;
                    txvCantidad.setText(String.valueOf(0));
                    txvprecio.setText(" $ 0.0");
                    BtMenos.setClickable(false);
                    indiceZona=RGZonas.getCheckedRadioButtonId();
                    _zona=zonas[indiceZona].replace(" ","%20");
                    obtener_secciones();
                }
            });
            RGZonas.addView(RBZonas[i]);
        }
        LLYZonas.addView(RGZonas);
        _zona=zonas[indiceZona].replace(" ","%20");
        RBZonas[0].setChecked(true);
        obtener_secciones();
    }

    void obtener_secciones(){
        for (int i=0;i<RBZonas.length;i++){
            if (i==indiceZona){
                RBZonas[i].setClickable(false);
            }else{
                RBZonas[i].setClickable(true);
            }
        }
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
                                secciones= new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    secciones[i]=datos.getString("nombre");
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
        spseccion.setVisibility(View.INVISIBLE);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item_2,secciones);
        adapter.setDropDownViewResource(R.layout.spinner_lista2);
        spseccion.setAdapter(adapter);
        spseccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void validateCaptcha() {
        // Showing reCAPTCHA dialog
        SafetyNet.getClient(getActivity()).verifyWithRecaptcha(SAFETY_NET_API_SITE_KEY)
                .addOnSuccessListener(getActivity(),
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                String userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    verifyTokenOnServer(response.getTokenResult());
                                }
                            }
                        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
    }

    public void verifyTokenOnServer(final String token) {
        Log.d(TAG, "Captcha Token " + token);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_VERIFY_ON_SERVER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");

                    if (success) {
                        // Congrats! captcha verified successfully on server
                        // TODO - submit the feedback to your server

                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("recaptcha-response", token);
                return params;
            }
        };

        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        if (RGZonas!=null) {
            RGZonas.removeAllViews();
        }
        Log.d("detach","detach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        if (RGZonas!=null) {
            RGZonas.removeAllViews();
        }
        Log.d("onDestroyView","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (RGZonas!=null) {
            RGZonas.removeAllViews();
        }
        Log.d("onDestroy","onDestroy");
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
            txvprecio.setText(" $ "+String.valueOf(Double.parseDouble(precios[indiceZona])*cantidadBoletos));
            if(cantidadBoletos>0){
                BtMenos.setClickable(true);
                spseccion.setVisibility(View.VISIBLE);
            }
        }
        if(v==BtMenos){
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            cantidadBoletos--;
            txvCantidad.setText(String.valueOf(cantidadBoletos));
            cantidadBoletos=Integer.parseInt((String) txvCantidad.getText());
            txvprecio.setText(" $ "+String.valueOf(Double.parseDouble(precios[indiceZona])*cantidadBoletos));
            if (cantidadBoletos==0){
                BtMenos.setClickable(false);
                spseccion.setVisibility(View.INVISIBLE);
            } else{
                spseccion.setVisibility(View.VISIBLE);
                BtMenos.setClickable(true);
                txvCantidad.setText(String.valueOf(cantidadBoletos));
            }
        }
    }

}
