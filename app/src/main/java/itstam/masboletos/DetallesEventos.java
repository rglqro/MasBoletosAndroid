package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import java.util.ArrayList;

public class DetallesEventos extends AppCompatActivity implements SeleccionZonaFR.OnFragmentInteractionListener, ComprarBoletoFr.OnFragmentInteractionListener, View.OnClickListener
,ComprarBoletoFr.Funcion_NumBolListener{

    ImageView IMVFondo,IMVEvento;
    String indiceimagen,idevento,nombreEvento,eventogrupo;
    JSONArray Elementos=null;
    ViewPager viewPager;
    FRPagerAdapter adapter;
    TabLayout tabLayout;
    DatosCompra datosCompra = new DatosCompra();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);

        Bundle bundle = getIntent().getExtras();
        indiceimagen=bundle.getString("indiceimagen");

        SharedPreferences preferencias=getSharedPreferences("InfoEvento", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("URLIMCuadrada", indiceimagen);
        editor.commit();

        idevento=bundle.getString("idevento");
        Log.e("idevento",idevento);
        IMVFondo=(ImageView)findViewById(R.id.IMVFondo);
        IMVEvento=(ImageView)findViewById(R.id.IMVEvento);
        difuminar_imagen();
        Consulta_Datos_Evento();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void IniciarFragments(){
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Cantidad de Boletos"));
        tabLayout.addTab(tabLayout.newTab().setText("Selección de Zona"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pagerFragments);
        adapter = new FRPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext(),nombreEvento,eventogrupo,idevento);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.setupWithViewPager(viewPager);
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

    public void next_page(){
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void regresar(View view){
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {

    }

    public void intent_compartir(View v){
        Intent compartir = new Intent(android.content.Intent.ACTION_SEND);
        compartir.setType("text/plain");
        String mensaje = "Asiste a '"+nombreEvento+"' que se llevará a cabo el/los día(s): "+"\nVisita el siguiente enlace: ";
        compartir.putExtra(android.content.Intent.EXTRA_SUBJECT, nombreEvento);
        compartir.putExtra(android.content.Intent.EXTRA_TEXT, mensaje);
        startActivity(Intent.createChooser(compartir, "Compartir vía"));
    }

    void Consulta_Datos_Evento(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getEventosActivos.php");  //para que la variable sea reconocida en todos los metodos
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);

                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    if(idevento.equals(datos.getString("idevento"))){
                                        nombreEvento=datos.getString("evento");
                                        eventogrupo=datos.getString("eventogrupo");
                                    }
                                }
                                IniciarFragments();
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


    @Override
    public void setFuncion_NumBol(String idevento, String Cant_Boletos) {
        Log.e("Datos a Mandar ",idevento+Cant_Boletos);
        set_DatosCompra("Cant_boletos",Cant_Boletos);
        set_DatosCompra("idevento",idevento);
        SeleccionZonaFR seleccionZonaFR= (SeleccionZonaFR) getSupportFragmentManager().findFragmentById(R.id.pagerFragments);
        seleccionZonaFR.Recibir_Funcion_CBol();
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void difuminar_imagen(){
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.drawable.ic_inicio)
                .into(IMVEvento);
        Bitmap resultBmp = BlurCreador.blur(this, BitmapFactory.decodeResource(getResources(),R.drawable.mblogo));
        IMVFondo.setImageBitmap(resultBmp);
    }

    public static class BlurCreador {

        private static final float BITMAP_SCALE = 0.9f;
        private static final float BLUR_RADIUS = 20f;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);

            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            intrinsicBlur.setRadius(BLUR_RADIUS);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }

    }
}
