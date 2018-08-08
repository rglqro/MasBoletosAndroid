package itstam.masboletos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UsuarioFR extends Fragment {

    SharedPreferences prefe;
    View vista;
    public static final String PAYPAL_CLIENT_ID="AYlSJbea6ruWz6FAn1X0ZXRKYTcY19Y0t_niLDKQRdBRn3gF5znxBzMaYa2km9CBrd-6qC0Zq6IRjFIx";
    String fpago,totalpago,nombreevento,idevento,fechappp,idpp,statuspp;
    Button entrar;
    JSONArray Elementos;
    EditText edtusuario,edtcontra;
    boolean resp=false;
    String msj,usuario,id_cliente;
    int bloqueo_boton=0;
    private static final int PAYPAL_REQUEST_CODE=7171;
    private static PayPalConfiguration configPP = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
    .clientId(PAYPAL_CLIENT_ID);

    public UsuarioFR() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_usuario_fr, container, false);
        entrar=(Button)vista.findViewById(R.id.btentrar);
        entrar.setBackgroundResource(R.color.grisclaro);
        edtcontra=(EditText) vista.findViewById(R.id.edtcontrasena);
        edtcontra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>1){
                    entrar.setBackgroundResource(R.color.verdemb);
                    bloqueo_boton=1;
                }else{
                    bloqueo_boton=0;
                    entrar.setBackgroundResource(R.color.grisclaro);
                }
            }
        });
        edtusuario=(EditText)vista.findViewById(R.id.edtcorreo);
        edtusuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtusuario.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") && s.length() > 0 && edtcontra.getText().length()>0) {
                    entrar.setBackgroundResource(R.color.verdemb);
                    bloqueo_boton=1;
                }
                else {
                    bloqueo_boton=0;
                    entrar.setBackgroundResource(R.color.grisclaro);
                }
            }
        });
        Intent intent= new Intent(getActivity(),PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configPP);
        getActivity().startService(intent);
        prefe=getActivity().getSharedPreferences("DatosCompra",Context.MODE_PRIVATE);
        recibir_datos();
        return vista;
    }

    void recibir_datos(){
        fpago=prefe.getString("idformapago","");
        totalpago=prefe.getString("total","0.00");
        nombreevento=prefe.getString("NombreEvento","");
        idevento=prefe.getString("idevento","");
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bloqueo_boton!=0) {
                    iniciar_sesion();
                }else{
                    ((DetallesEventos)getActivity()).AlertaBoton("Datos usuario","Ingresa los datos correspondientes").show();
                }
            }
        });
    }

    void iniciar_sesion(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="http://www.masboletos.mx/appMasboletos/validalogin.php?correo="+edtusuario.getText().toString()+"&contrasenia="+edtcontra.getText().toString();
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                resp=datos.getBoolean("respuesta");
                                msj=datos.getString("mensaje");
                                id_cliente=datos.getString("id_cliente");
                                usuario=datos.getString("usuario");
                            }
                            ((DetallesEventos)getActivity()).cerrar_cargando();
                            if(resp){
                                Toast.makeText(getActivity(),"Bienvenido: "+usuario,Toast.LENGTH_LONG).show();
                                checar_tipo_pago();
                            }else {
                                Toast.makeText(getActivity(),msj,Toast.LENGTH_SHORT).show();
                            }
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
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void checar_tipo_pago(){
        if(fpago.equals("5")){
            procesar_pagoPP();
        }
    }

    private void procesar_pagoPP(){
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(totalpago),"MXN","Pago por boletos de :"+nombreevento,PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(),PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configPP);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PAYPAL_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!=null){
                    try {
                        String detallespago= confirmation.toJSONObject().toString(4);
                        JSONObject obj1= new JSONObject(detallespago);// Lee el objeto de Json
                        JSONObject json3 = obj1.getJSONObject("response"); //Obtiene un conjunto de elementos nombrado
                        fechappp=json3.getString("create_time");
                        idpp=json3.getString("id");
                        statuspp=json3.getString("state");
                        Toast.makeText(getActivity(),"Fecha: "+fechappp+"\nid: "+idpp,Toast.LENGTH_LONG).show();
                        Log.e("Datos Pago Paypal","Fecha: "+fechappp+"\nid: "+idpp);
                    }catch (Exception e){}
                }
            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getActivity(),"Pago Cancelado",Toast.LENGTH_LONG).show();
            }
        }else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID){

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(),PayPalService.class));
        super.onDestroy();
    }
}
