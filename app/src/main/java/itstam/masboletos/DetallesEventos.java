package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

public class DetallesEventos extends AppCompatActivity implements InfoFragment.OnFragmentInteractionListener, ComprarBoletoFr.OnFragmentInteractionListener, View.OnClickListener{

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView imEvento;
    String indiceimagen,idevento,nombreEvento,eventogrupo;
    JSONArray Elementos=null;

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" Atrás");
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));

        Bundle bundle = getIntent().getExtras();
        indiceimagen=bundle.getString("indiceimagen");
        idevento=bundle.getString("idevento");
        imEvento=(ImageView) findViewById(R.id.imEvento);
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.id.action_inicio)
                .into(imEvento);
        Consulta_Datos_Evento();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void ControlBotones(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Compra tus Boletos"));
        tabLayout.addTab(tabLayout.newTab().setText("Información"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerFragments);
        final FRPagerAdapter adapter = new FRPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext(),nombreEvento,eventogrupo,idevento);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
                                ControlBotones();
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


}
