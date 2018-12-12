package itstam.masboletos.carruselcompra;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jackandphantom.blurimage.BlurImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import itstam.masboletos.R;


public class DetallesEventos extends AppCompatActivity {

    ImageView IMVFondo,IMVEvento;
    String idevento="",ideventopack="",comisionpack="0",fpago="";
    String imgevento="",direevento="S/D",nomevento="",lugarevento="S/L",fechaevento="N/D",horaevento="N/D",descevento="",eventomapa="";
    TabLayout tabLayout;
    String[] FRNombres;
    ImageButton IMBTRegresar;
    TextView TXVNEvento,txvinfoevepac,txvdescripcionevepac,txvcrono;
    int contadorTab=0,ancho,alto;
    TabLayout.Tab tab;
    ProgressDialog dialogcarg;
    RelativeLayout rlimagsevento;
    CountDownTimer cdtcrono;
    JSONArray Elementos;
    Bitmap imageBlur;
    ScrollView scvcarruselcompra;
    int cont_regreso=0;
    SharedPreferences prefe;
    DecimalFormat df = new DecimalFormat("#0.00");


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);
        TXVNEvento = (TextView) findViewById(R.id.txvnombreevepaq);
        txvinfoevepac = findViewById(R.id.txvinfoevepac);
        txvdescripcionevepac = findViewById(R.id.TXVDescripcion);
        IMVFondo = (ImageView) findViewById(R.id.IMVFondo);
        IMVEvento = (ImageView) findViewById(R.id.IMVEvento);
        IMBTRegresar = (ImageButton) findViewById(R.id.imBtRegresar);
        rlimagsevento = findViewById(R.id.rlimagsevento);
        scvcarruselcompra=findViewById(R.id.scvcarruselcompra);
        txvcrono = findViewById(R.id.txvcrono);
        txvcrono.setVisibility(View.INVISIBLE);
        prefe = this.getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento = prefe.getString("idevento", "0");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;
        rlimagsevento.getLayoutParams().height = alto / 5;
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();


        if (idevento.equals("0")) { /*Si el ID es 0 significa que la compra será para paquetes*/
            ideventopack = prefe.getString("ideventopack", "");
            consulta_info("https://www.masboletos.mx/appMasboletos/getPaqueteEncabezado.php?IdEventoPack=" + ideventopack);
        } else if(appLinkAction!=null){
            Uri appLinkData = appLinkIntent.getData();
            //Log.e("pagina",appLinkData.toString());
            //Log.e("pagina2",appLinkAction);
            String[] sep= appLinkData.toString().split("=");
            consulta_info("https://www.masboletos.mx/appMasboletos/getEventoEncabezado.php?idevento=" + sep[1]);
        }else {
            consulta_info("https://www.masboletos.mx/appMasboletos/getEventoEncabezado.php?idevento=" + idevento);
        }
        set_DatosCompra("Cant_boletos", "0");
        set_DatosCompra("posEve", "0");

    }

    void consulta_info(String URL){
        iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                if(idevento.equals("0")){
                                    imgevento=datos.getString("imagen");
                                    nomevento=datos.getString("nombre");
                                    descevento= datos.getString("descripcion");
                                    eventomapa=datos.getString("EventoMapa");
                                    comisionpack=datos.getString("Comision");
                                    set_DatosCompra("comisionpack",comisionpack);
                                    set_DatosCompra("eventomapa",eventomapa);
                                }else {
                                    imgevento=datos.getString("imagen");
                                    direevento=datos.getString("direccion");
                                    nomevento=datos.getString("evento");
                                    lugarevento=datos.getString("lugar");
                                    fechaevento=datos.getString("FechaLarga");
                                    horaevento=datos.getString("hora");
                                    descevento=datos.getString("descripcion");
                                }
                            }
                            pintar_info();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void pintar_info(){
        TXVNEvento.setText(nomevento);
        String txt=lugarevento+", "+direevento+"<br><b>Fecha y Hora:</b> "+fechaevento+", "+horaevento;
        txvinfoevepac.setText(Html.fromHtml(txt));
        txvdescripcionevepac.setText("Información del evento: "+descevento);
        set_DatosCompra("fechaevento",fechaevento);
        set_DatosCompra("horaevento",horaevento);
        set_DatosCompra("NombreEvento",nomevento);
        difuminar_imagen();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void difuminar_imagen(){
        //Log.e("ImagenEventourl",imgevento);
        if(imgevento.equals("")) imgevento="https://www.masboletos.mx/img/imgMASBOLETOS.jpg";
        Picasso.get().load(imgevento).error(R.drawable.imgmberror).into(IMVEvento, new Callback() {
            @Override
            public void onSuccess() {
                imageBlur=((BitmapDrawable)IMVEvento.getDrawable()).getBitmap();
                BlurImage.with(getApplicationContext()).load(imageBlur).intensity(20).Async(true).into(IMVFondo);
                IMVFondo.setScaleType(ImageView.ScaleType.FIT_XY);
                cerrar_cargando();
                IniciarFragments();
            }
            @Override
            public void onError(Exception e) {
                imageBlur=((BitmapDrawable)IMVEvento.getDrawable()).getBitmap();
                BlurImage.with(getApplicationContext()).load(imageBlur).intensity(20).Async(true).into(IMVFondo);
                IMVFondo.setScaleType(ImageView.ScaleType.FIT_XY);
                cerrar_cargando();
                IniciarFragments();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void IniciarFragments(){
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("1. Cantidad de Boletos"));
        for(int i=2;i<=8;i++){
            tabLayout.addTab(tabLayout.newTab().setText(""+i));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ComprarBoletoFr comprarBoletoFr= new ComprarBoletoFr();
        getSupportFragmentManager().beginTransaction().add(R.id.pagerfragmets2,comprarBoletoFr).commit();
        FRNombres=new String[tabLayout.getTabCount()];
        FRNombres[0]="1. Cantidad de Boletos";FRNombres[1]="2. Selección de Zona";FRNombres[2]="3. Mas Boletos te recomienda";FRNombres[3]="4. Elige tu Forma de Pago";
        FRNombres[4]="5. Forma de Entrega"; FRNombres[5]="6. Revisión"; FRNombres[6]="7. Usuario"; FRNombres[7]="8. Finalización de compra";
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText(FRNombres[tab.getPosition()]);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setText(String.valueOf(tab.getPosition()+1));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

    }

    public void intent_compartir(View v){
        Intent compartir = new Intent(Intent.ACTION_SEND);
        compartir.setType("text/plain");
        String mensaje = "+Boletos te invita a '"+nomevento+"' que se llevará a cabo el/los día(s): "+fechaevento+"\nVisita el siguiente enlace: https://www.masboletos.mx/evento.php?idevento="+idevento;
        compartir.putExtra(Intent.EXTRA_SUBJECT, nomevento);
        compartir.putExtra(Intent.EXTRA_TEXT, mensaje);
        startActivity(Intent.createChooser(compartir, "Compartir vía"));
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

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.pagerfragmets2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        contadorTab++;
        if(contadorTab==1){
            AlertaCrono("Tiempo de Compra","A partir de este momento cuentas con 7 minutos para comprar tu boletos, continua con tu proceso").show();
        }
        if(contadorTab==7 && cdtcrono!=null){
            txvcrono.setVisibility(View.INVISIBLE); cdtcrono.cancel();
        }
        //Log.e("posicion tab",String.valueOf(contadorTab));
        cambiar_tab(contadorTab);
    }

    public void replaceFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.pagerfragmets2, fragment);
        transaction.commit();
    }

    void cambiar_tab(int pos){
        tab = tabLayout.getTabAt(pos);
        tab.select();
    }

    public void tab_anterior(){
        if(contadorTab>0 && contadorTab!=7) {
            contadorTab--;
            TabLayout.Tab tab = tabLayout.getTabAt(contadorTab);
            tab.select();
            super.onBackPressed();
        }else if(contadorTab==0 || contadorTab==7) {
            fpago=prefe.getString("idformapago","0");
            if(fpago.equals("4") && contadorTab==7)
                alerta_captura("¿Quieres cerrar la compra?","Si ya has tomado captura de pantalla al código de barras, pulsa ACEPTAR, sino pulsa CANCELAR y procede a realizarla");
            else if((fpago.equals("2") || fpago.equals("3")) && contadorTab==7)
                alerta_captura("¿Quieres cerrar la compra?","Si tu compra ha sido confirmada pulsa ACEPTAR sino pulsa CANCELAR para continuar con el PROCESO");
            else
                finish();
        }
        if(contadorTab==0 && cdtcrono!=null){
            txvcrono.setVisibility(View.INVISIBLE); cdtcrono.cancel();
        }
    }

    public void regresar(View view){
        onBackPressed();
    }

    public AlertDialog AlertaCrono(String titulo,String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mover_alfondo();
                                cronometro_comra();
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
        return builder.create();
    }

    public AlertDialog AlertaBoton(String titulo,String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false);
        return builder.create();
    }

    public void alerta_captura(String titu,String msj){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titu);
        builder.setMessage(msj);

        // add the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void set_DatosCompra(String ndato,String dato){//Este metodo almacena las variables que serán utilizadas en la compra
        SharedPreferences preferencias=getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    public void set_DatosUsuario(String ndato,String dato,int tipo){//Este metodo almacena las variables que tienen los datos del usuario que inicia sesion
        SharedPreferences preferencias=getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        if(tipo==0) {
            editor.putString(ndato, dato);
        }else{
            editor.putBoolean(ndato, Boolean.parseBoolean(dato));
        }
        editor.commit();
    }

    public void mover_alfondo(){
        cont_regreso=0;
        scvcarruselcompra.post(new Runnable() {
            @Override
            public void run() {
                scvcarruselcompra.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void cronometro_comra(){
        txvcrono.setVisibility(View.VISIBLE);
        if(cdtcrono!=null){
            cdtcrono.cancel();
        }
        cdtcrono= new CountDownTimer(420000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txvcrono.setText("("+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+")");
                if(cont_regreso==1)
                    scvcarruselcompra.fullScroll(ScrollView.FOCUS_UP);
                cont_regreso++;
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(),"Se ha terminado su tiempo de compra, vuelva a intentarlo",Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        tab_anterior();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cdtcrono!=null){
            cdtcrono.cancel();
        } //Log.e("Destroy","Destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
