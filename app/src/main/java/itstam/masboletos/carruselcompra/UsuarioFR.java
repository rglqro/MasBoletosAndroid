package itstam.masboletos.carruselcompra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    public static final String PAYPAL_CLIENT_ID="AYlSJbea6ruWz6FAn1X0ZXRKYTcY19Y0t_niLDKQRdBRn3gF5znxBzMaYa2km9CBrd-6qC0Zq6IRjFIx";
    String fpago,totalpago,nombreevento,idevento,fechappp,idpp,statuspp,txthtml,comisionpack,ideventopack="";
    String idzona,numerado,precio,idformaentrega,cargoxservicio,folio,idfila="",inicolumna="",fincolumna="",idfilafilaasiento="",filaasientos="",fila="",idvermapa,dataevento;
    Button entrar;
    TextView txvinfocrearcta;
    JSONArray Elementos;
    EditText edtusuario,edtcontra;
    String URL="";
    boolean resp=false,validasesion=false;
    String msj,usuario,id_cliente;
    int bloqueo_boton=0,dataeventosize=0,cantidadeventosxpaquete,cant_boletos;
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

        cantidadeventosxpaquete=dataeventosize*cant_boletos;

        if(numerado.equals("1")){
            idfila=prefe.getString("idfila","");
            inicolumna=prefe.getString("inicolumna","");
            fincolumna=prefe.getString("fincolumna","");
            fila=prefe.getString("fila","");
            if(idvermapa.equals("1")){
                idfilafilaasiento=prefe.getString("idfilafilaasiento","");
                filaasientos=prefe.getString("filaasientos","");
            }

        }
        if(validasesion) {
            usuario=prefe_sesion.getString("usuario_s","");
            id_cliente=prefe_sesion.getString("id_cliente","");
            checar_tipo_pago();
        }else{
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
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="https://www.masboletos.mx/appMasboletos/validalogin.php?correo="+edtusuario.getText().toString()+"&contrasenia="+edtcontra.getText().toString();
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
                                ((DetallesEventos)getActivity()).set_DatosCompra("email",edtusuario.getText().toString());

                                ((DetallesEventos)getActivity()).set_DatosUsuario("usuario_s",usuario,0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("contrasena_s",edtcontra.getText().toString(),0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("id_cliente",id_cliente,0);
                                ((DetallesEventos)getActivity()).set_DatosUsuario("validasesion","true",1);
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
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void checar_tipo_pago(){
        if (fpago.equals("2")||fpago.equals("3")){
            preregistroTCTD();
            ((DetallesEventos)getActivity()).FRNombres[7]="8. Pago con TC";
        }
        if(fpago.equals("5")){
            Intent intent= new Intent(getActivity(),PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configPP);
            getActivity().startService(intent);
            if(idevento.equals("0")){
                pre_registro_paypal_post("https://www.masboletos.mx/masBoletosEnviadatosPaquetePaypalMovil.php");
            }else{
                preregistro_paypal("https://www.masboletos.mx/masBoletosEnviaDatosPaypalMovil.php?idevento="+idevento+"&numerado="
                        +numerado+"&cantidad="+cant_boletos+"&cargoxservicio="+cargoxservicio+"&zona="+idzona+"&idcliente="+id_cliente+"&formadepago="
                        +fpago+"&txtformaentrega="+idformaentrega+"&importe="+precio+"&idfila="+idfila+"&inicolumna="+inicolumna+"&fincolumna="+fincolumna
                        +"&filaasientos="+filaasientos+"&fila="+fila+"&idfilafilaasiento="+idfilafilaasiento);
            }
        }
    }

    private void preregistro_paypal(final String URL){
        ((DetallesEventos)getActivity()).iniciar_cargando(); Log.e("URL",URL);
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta(URL);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            Log.e("Resultado registro",resultado);
                            procesar_pagoPP();
                            folio=resultado;
                        }
                    }});  //permite trabajar con la interfaz grafica
            }};
        tr.start();
    }

    private void pre_registro_paypal_post(String url){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response","error");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("cantidadeventosxpaquete", String.valueOf(dataeventosize));Log.e("dato",String.valueOf(dataeventosize));
                params.put("asientos", String.valueOf(cant_boletos));Log.e("dato",String.valueOf(cant_boletos));
                params.put("seccion", "BRONCE");
                params.put("fila", "GENERAL");
                params.put("importe", precio);Log.e("dato",precio);
                params.put("cantidad", String.valueOf(cant_boletos));Log.e("dato",String.valueOf(cant_boletos));
                params.put("cargoxservicio", comisionpack);
                params.put("cargotdc", cargoxservicio);Log.e("dato",comisionpack);
                params.put("zona", idzona);Log.e("dato",idzona);
                params.put("numerado", "0");
                params.put("idcliente",id_cliente);Log.e("dato",id_cliente);
                //params.put("idfila", "0");
                //params.put("inicolumna", "0");
                //params.put("fincolumna", "0");
                //params.put("filaasientos", "");
                //params.put("idfilafilaasiento", "");
                params.put("formadepago", fpago);Log.e("dato",fpago);
                params.put("txtformaentrega", idformaentrega);Log.e("dato",idformaentrega);
                params.put("idpaquete", ideventopack);Log.e("dato",ideventopack);
                params.put("Comisionpaquete", comisionpack);Log.e("dato",comisionpack);
                params.put("dataEventos", dataevento.toString());Log.e("dato",dataevento);
                params.put("datafilasiento", "[]");
                params.put("dataeventozonafilasiento", "[]");


                /*preregistro_paypal("https://www.masboletos.mx/masBoletosEnviadatosPaquetePaypalMovil.php?idpaquete="+ideventopack+"&numerado="
                        +numerado+"&cantidad="+cant_boletos+"&cargoxservicio="+cargoxservicio+"&zona="+idzona+"&idcliente="+id_cliente+"&formadepago="
                        +fpago+"&txtformaentrega="+idformaentrega+"&importe="+precio+"&idfila="+idfila+"&inicolumna="+inicolumna+"&fincolumna="+fincolumna
                        +"&filaasientos="+filaasientos+"&fila="+fila+"&idfilafilaasiento="+idfilafilaasiento+"&cantidadeventosxpaquete="+cantidadeventosxpaquete
                        +"&Comisionpaquete="+comisionpack+"&dataEventos="+dataevento);*/
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void preregistroTCTD(){
        String URL=("https://www.masboletos.mx/masBoletosEnviaDatosMovil.php?idevento="+idevento+"&numerado="
                        +numerado+"&cantidad="+cant_boletos+"&cargoxservicio="+cargoxservicio+"&zona="+idzona+"&idcliente="+id_cliente+"&formadepago="
                        +fpago+"&txtformaentrega="+idformaentrega+"&importe="+precio+"&idfila="+idfila+"&inicolumna="+inicolumna+"&fincolumna="+fincolumna
                        +"&filaasientos="+filaasientos+"&fila="+fila+"&idfilafilaasiento="+idfilafilaasiento);  //para que la variable sea reconocida en todos los metodos
        ((DetallesEventos)getActivity()).set_DatosCompra("URLTC",URL);
        ((DetallesEventos)getActivity()).replaceFragment(new FRFinalizarCompra());
    }

    private void procesar_pagoPP(){
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
                        actualizaciondepago("0");
                    }catch (Exception e){}
                }
            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getActivity(),"Pago Cancelado",Toast.LENGTH_LONG).show();
                actualizaciondepago("1");
            }
        }else if(resultCode==PaymentActivity.RESULT_EXTRAS_INVALID){

        }
    }

    public void actualizaciondepago(final String error){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("https://www.masboletos.mx/masMoletosRecibeDatosPaypalMovil.php?EM_OrderID="+folio+"&error="+error);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            Log.e("Resultado actualizacion",resultado);
                            if(error.equals("0")){
                                ((DetallesEventos)getActivity()).cerrar_cargando();
                                ((DetallesEventos)getActivity()).set_DatosCompra("nombreuser",usuario);
                                ((DetallesEventos)getActivity()).set_DatosCompra("foliocompra",folio);
                                ((DetallesEventos)getActivity()).replaceFragment(new FRFinalizarCompra());
                                envio_mail(resultado);
                            }
                            else {
                                ((DetallesEventos)getActivity()).cerrar_cargando();
                                ((DetallesEventos)getActivity()).finish();
                            }
                        }
                    }});  //permite trabajar con la interfaz grafica
            }};
        tr.start();
    }

    public void envio_mail(final String fe){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("https://www.masboletos.mx/sica/masmail.cfm?trans="+folio+"&fe="+fe+"&de=2");  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            Log.e("Resultado actualizacion",resultado);
                            ((DetallesEventos)getActivity()).cerrar_cargando();

                        }
                    }});  //permite trabajar con la interfaz grafica
            }};
        tr.start();
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
