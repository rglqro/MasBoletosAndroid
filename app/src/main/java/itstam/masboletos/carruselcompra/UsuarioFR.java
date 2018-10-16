package itstam.masboletos.carruselcompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import itstam.masboletos.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class UsuarioFR extends Fragment {

    SharedPreferences prefe,prefe_sesion;
    View vista;
    public static final String PAYPAL_CLIENT_ID_sandbox="Ac-oT41cQCMClEGy9WjrvNgLS4JC7cPDICsLdjTmJEVn_-C-jMQ0Zs9mOZMA7omBrSK0w4p8kvVbO2iu";
    public static final String PAYPAL_CLIENT_ID_live="AaYUqrVFn58K9kaZ2FBsAlZNdOB5H_2-Hpy5Tlf943e4NyUeT_ceHUW2c5nGMWzVFHi5uOFRg5tJIicr";
    String fpago,totalpago,nombreevento,idevento,fechappp,idpp,statuspp,txthtml,comisionpack,ideventopack="",datalugarespack="";
    String idzona,numerado,precio,idformaentrega,cargoxservicio,folio,idfila="",inicolumna="",fincolumna="",idfilafilaasiento="",filaasientos="",fila="",idvermapa,dataevento;
    Button entrar,btcrearcuenta;
    String tipousuario;
    TextView txvinfocrearcta;
    JSONArray Elementos;
    EditText edtusuario,edtcontra;
    String URL="";
    boolean resp=false,validasesion=false;
    String msj,usuario,id_cliente;
    int bloqueo_boton=0,dataeventosize=0,cant_boletos;
    private static final int PAYPAL_REQUEST_CODE=7171;
    private static PayPalConfiguration configPP = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION).clientId(PAYPAL_CLIENT_ID_live);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_usuario_fr, container, false);
        entrar=(Button)vista.findViewById(R.id.btentrar);
        btcrearcuenta=vista.findViewById(R.id.btcrearctauser);
        entrar.setBackgroundResource(R.color.grisclaro);
        txvinfocrearcta=(TextView)vista.findViewById(R.id.txvinfocrearcta);
        txthtml="<b>Crea tu cuenta para:</b><br><br>• Agilizar tu compra guardando tu información" +
                "<br>• Imprimir boletos desde tu casa" +
                "<br>• Obtener recomendaciones de eventos";
        txvinfocrearcta.setText(Html.fromHtml(txthtml));
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
        prefe=getActivity().getSharedPreferences("DatosCompra",Context.MODE_PRIVATE);
        prefe_sesion=getActivity().getSharedPreferences("datos_sesion",Context.MODE_PRIVATE);
        validasesion=prefe_sesion.getBoolean("validasesion",false);
        btcrearcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.masboletos.mx/crearperfil.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        recibir_datos();
        return vista;
    }

    void recibir_datos(){
        fpago=prefe.getString("idformapago","");
        totalpago=prefe.getString("total","0.00");
        nombreevento=prefe.getString("NombreEvento","");
        idevento=prefe.getString("idevento","");
        ideventopack=prefe.getString("ideventopack","0");
        idzona=prefe.getString("idsubzona","");
        cant_boletos= Integer.parseInt(prefe.getString("Cant_boletos","0"));
        numerado=prefe.getString("valornumerado","");
        precio=prefe.getString("precio","");
        idformaentrega=prefe.getString("idformaentrega","");
        cargoxservicio=prefe.getString("cargoxservicio","");
        edtusuario.setText(prefe.getString("email",""));
        idvermapa=prefe.getString("idvermapa","0");
        dataevento=prefe.getString("dataevento",null);
        dataeventosize= Integer.parseInt(prefe.getString("dataeventosize","0"));
        comisionpack=prefe.getString("comisionpack","0");
        datalugarespack=prefe.getString("datalugarespack","");


        if(numerado.equals("1")){/*si es numerado entra aqui*/
            idfila=prefe.getString("idfila","");
            inicolumna=prefe.getString("inicolumna","");
            fincolumna=prefe.getString("fincolumna","");
            fila=prefe.getString("fila","").replace(" ","");
            if(idvermapa.equals("1")){/*si el usuario eligió sus asientos se trabaja con los datos dentro del if sino con los datos anteriores*/
                idfilafilaasiento=prefe.getString("idfilafilaasiento","");
                filaasientos=prefe.getString("filaasientos","");
                idfila="";
                inicolumna="0";
                fincolumna="0";
                fila="";
            }

        }
        if(validasesion) {/* si la sesion está iniciada se procederá a realizar el pago obteniendo los dato almacenados en el dispositivo*/
            usuario=prefe_sesion.getString("usuario_s","");
            id_cliente=prefe_sesion.getString("id_cliente","");
            checar_tipo_pago();
        }else{ /*sino se procedera a iniciar sesion para guardar la informacion y mandarla con los datos de compra*/
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

    }

    void iniciar_sesion(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url="https://www.masboletos.mx/appMasboletos.fueralinea/validalogin.php";
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            Elementos = new JSONArray(response);
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                resp=datos.getBoolean("respuesta");
                                msj=datos.getString("mensaje");
                                id_cliente=datos.getString("id_cliente");
                                usuario=datos.getString("usuario");
                                tipousuario=datos.getString("tipousuario");
                            }
                            ((DetallesEventos)getActivity()).cerrar_cargando();
                            if(resp){// si la sesion es iniciada correctamente de procede a iniciar el proceso de pago
                                ((DetallesEventos)getActivity()).set_DatosCompra("email",edtusuario.getText().toString());
                                ((DetallesEventos)getActivity()).set_DatosUsuario("usuario_s",usuario,0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("contrasena_s",edtcontra.getText().toString(),0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("id_cliente",id_cliente,0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("tipousuario",tipousuario,0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("validasesion","true",1);
                                Toast.makeText(getActivity(),"Bienvenido: "+usuario,Toast.LENGTH_LONG).show();
                                checar_tipo_pago();
                                if(((DetallesEventos)getActivity()).cdtcrono!=null){
                                    ((DetallesEventos)getActivity()).txvcrono.setVisibility(View.INVISIBLE); ((DetallesEventos)getActivity()).cdtcrono.cancel();
                                }
                            }else {
                                Toast.makeText(getActivity(),msj,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("correo", edtusuario.getText().toString());Log.e("dato",String.valueOf(dataeventosize));
                params.put("contrasenia", edtcontra.getText().toString());Log.e("fila",fila);
                return params;
            }
        };
        queue.add(strRequest);
    }

    void checar_tipo_pago(){/*en este metodo se valida con el id de fpago con que proceso se va a realizar */
        if (fpago.equals("2")||fpago.equals("3")){
            if(idevento.equals("0")){
                pre_registro_packs("https://www.masboletos.mx/masBoletosEnviaDatosPaqueteMovil.php");
            }else {
                preregistroTCTD();
            }
            ((DetallesEventos)getActivity()).FRNombres[7]="8. Pago con TC";
        }else if(fpago.equals("5")){
            Intent intent= new Intent(getActivity(),PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configPP);
            getActivity().startService(intent);
            if(idevento.equals("0")){
                pre_registro_packs("https://www.masboletos.mx/masBoletosEnviadatosPaquetePaypalMovil.php");
            }else{
                preregistro_paypal("https://www.masboletos.mx/masBoletosEnviaDatosPaypalMovil.php?idevento="+idevento+"&numerado="
                        +numerado+"&cantidad="+cant_boletos+"&cargoxservicio="+cargoxservicio+"&zona="+idzona+"&idcliente="+id_cliente+"&formadepago="
                        +fpago+"&txtformaentrega="+idformaentrega+"&importe="+precio+"&idfila="+idfila+"&inicolumna="+inicolumna+"&fincolumna="+fincolumna
                        +"&filaasientos="+filaasientos+"&fila="+fila+"&idfilafilaasiento="+idfilafilaasiento);
            }
        }
    }

    private void preregistro_paypal(final String url){/*Este metodo aparta los boletos para poder realizar la transaccion obteniendo un folio de transaccion */
        ((DetallesEventos)getActivity()).iniciar_cargando(); Log.e("URL",URL);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        Log.e("URL",url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Resultado registro",response);
                        procesar_pagoPP();/*Aqui se inician lo servicios del API de Paypal*/
                        folio=response;
                        folio = folio.replace(" ", "").replace("\n", "");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                ((DetallesEventos)getActivity()).cerrar_cargando();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void pre_registro_packs(String url){/*Este metodo registra via post los datos para los boletos de un paquete*/
        ((DetallesEventos)getActivity()).iniciar_cargando();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        if(fpago.equals("2") || fpago.equals("3")){
                            ((DetallesEventos)getActivity()).set_DatosCompra("URLTC",response);
                            ((DetallesEventos)getActivity()).replaceFragment(new FRFinalizarCompra());
                            ((DetallesEventos)getActivity()).cerrar_cargando();
                        }else if(fpago.equals("5")) {
                            folio = response;
                            folio = folio.replace(" ", "").replace("\n", "");
                            procesar_pagoPP();
                        }
                        Log.e("respenvia",response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cantidadeventosxpaquete", String.valueOf(dataeventosize));Log.e("dato",String.valueOf(dataeventosize));
                params.put("fila", fila);Log.e("fila",fila);
                params.put("importe", precio);Log.e("dato",precio);
                params.put("cantidad", String.valueOf(cant_boletos));Log.e("dato",String.valueOf(cant_boletos));
                params.put("cargoxservicio", comisionpack);
                params.put("cargotdc", cargoxservicio);Log.e("dato",comisionpack);
                params.put("zona", idzona);Log.e("dato",idzona);
                params.put("numerado", numerado);
                params.put("idcliente",id_cliente);Log.e("dato",id_cliente);
                params.put("idfila", idfila); Log.e("idfila",idfila);
                params.put("inicolumna", inicolumna); Log.e("inicolumna",inicolumna);
                params.put("fincolumna", fincolumna); Log.e("fincolumna",fincolumna);
                params.put("filaasientos", filaasientos); Log.e("filaasientos",filaasientos);
                params.put("idfilafilaasiento", idfilafilaasiento);Log.e("idfilafilaasiento",idfilafilaasiento);
                params.put("formadepago", fpago);Log.e("dato",fpago);
                params.put("txtformaentrega", idformaentrega);Log.e("dato",idformaentrega);
                params.put("idpaquete", ideventopack);Log.e("dato",ideventopack);
                params.put("Comisionpaquete", comisionpack);Log.e("dato",comisionpack);
                params.put("dataEventos", dataevento.toString());Log.e("dato",dataevento);
                params.put("datafilasiento", "[]");
                params.put("dataeventozonafilasiento", datalugarespack); Log.e("datalugpack",datalugarespack);
                return params;
            }
        };
        queue.add(strRequest);
    }

    private void preregistroTCTD(){/*Este solo crea la URL para abrirla y direccionar al portal de pago para tarjetas de Credito, esta url se abrirá en el siguiente fragmento ya que devuelve un post automatico el script*/
        String URL=("https://www.masboletos.mx/masBoletosEnviaDatosMovil.php?idevento="+idevento+"&numerado="
                        +numerado+"&cantidad="+cant_boletos+"&cargoxservicio="+cargoxservicio+"&zona="+idzona+"&idcliente="+id_cliente+"&formadepago="
                        +fpago+"&txtformaentrega="+idformaentrega+"&importe="+precio+"&idfila="+idfila+"&inicolumna="+inicolumna+"&fincolumna="+fincolumna
                        +"&filaasientos="+filaasientos+"&fila="+fila+"&idfilafilaasiento="+idfilafilaasiento);  //para que la variable sea reconocida en todos los metodos
        ((DetallesEventos)getActivity()).set_DatosCompra("URLTC",URL);
        ((DetallesEventos)getActivity()).replaceFragment(new FRFinalizarCompra());
    }

    private void procesar_pagoPP(){/*Aqui se inicia el api de paypal*/
        ((DetallesEventos)getActivity()).cerrar_cargando();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(totalpago),"MXN","Pago por boletos de :"+nombreevento,PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(),PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configPP);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PAYPAL_REQUEST_CODE){
            if (resultCode== RESULT_OK){ /*Si el pago es realizado se procede a capturar los datos de compra*/
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
                        actualizaciondepago("0");/*y se actualiza la transaccion obtenida del apartado mandando una variable error donde 0 es pago aprobado*/
                    }catch (Exception e){}
                }
            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getActivity(),"Pago Cancelado",Toast.LENGTH_LONG).show();
                actualizaciondepago("1");/*si el pago es invalido o cancelado se envia 1 en error indicando que no se realizó*/
            }
        }else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID){

        }
    }

    public void actualizaciondepago(final String error){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url="https://www.masboletos.mx/masMoletosRecibeDatosPaypalMovil.php?EM_OrderID="+folio+"&error="+error; Log.e("URL",url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Resultado actualizacion",response);
                        if(error.equals("0")){/* si el pago se realizó se manda al usuario un email via script*/
                            envio_mail(response);
                        }
                        else { /*si el pago no se realizó se procede a cerrar el proceso de compra*/
                            ((DetallesEventos)getActivity()).cerrar_cargando();
                            ((DetallesEventos)getActivity()).finish();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                ((DetallesEventos)getActivity()).cerrar_cargando();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void envio_mail(final String fe){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url="https://www.masboletos.mx/sica/masmail.cfm?trans="+folio+"&fe="+fe+"&de=2"; Log.e("URL",url);
        Log.e("URL",url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resultado) {/*Despues del envio del mail se procede a la ultima ventana del proceso de compra*/
                        Log.e("Resultado actualizacion",resultado);
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                        ((DetallesEventos)getActivity()).set_DatosCompra("nombreuser",usuario);
                        ((DetallesEventos)getActivity()).set_DatosCompra("foliocompra",folio);
                        ((DetallesEventos)getActivity()).replaceFragment(new FRFinalizarCompra());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                ((DetallesEventos)getActivity()).cerrar_cargando();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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
