package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;

public class DetallesEventos extends AppCompatActivity implements  View.OnClickListener
,ComprarBoletoFr.Funcion_NumBolListener,SeleccionZonaFR.SelZonaList{

    ImageView IMVFondo,IMVEvento;
    String indiceimagen,nombreEvento,eventogrupo;
    JSONArray Elementos=null;
    ViewPager viewPager;
    FRPagerAdapter adapter;
    TabLayout tabLayout;
    DatosCompra datosCompra = new DatosCompra();
    TextView TXVNEvento;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);
        TXVNEvento=(TextView)findViewById(R.id.txvNombreEve);

        Bundle bundle = getIntent().getExtras();
        indiceimagen=bundle.getString("indiceimagen");


        SharedPreferences prefe=this.getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        TXVNEvento.setText((prefe.getString("NombreEvento","")));

        IMVFondo=(ImageView)findViewById(R.id.IMVFondo);
        IMVEvento=(ImageView)findViewById(R.id.IMVEvento);
        difuminar_imagen();
        IniciarFragments();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void IniciarFragments(){
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Cantidad de Boletos"));
        tabLayout.addTab(tabLayout.newTab().setText("Selección de Zona"));
        tabLayout.addTab(tabLayout.newTab().setText("Mejor Disponible"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pagerFragments);
        adapter = new FRPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext(),nombreEvento,eventogrupo);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        Log.e("VPActual",String.valueOf(viewPager.getCurrentItem()));
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }
    public void pagina_anterior(){
        if(viewPager.getCurrentItem()>0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }else{
            finish();
        }
    }

    public void regresar(View view){
        finish();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void difuminar_imagen(){
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.drawable.ic_inicio)
                .into(IMVEvento);
        Bitmap resultBmp = BlurCreador.blur(this, BitmapFactory.decodeResource(getResources(),R.drawable.mblogo));
        IMVFondo.setImageBitmap(resultBmp);
    }

    @Override
    public void setFuncion_NumBol(String idevento, String Cant_Boletos) {
        Log.e("Datos a Mandar ",idevento+Cant_Boletos);
        set_DatosCompra("Cant_boletos",Cant_Boletos);
        set_DatosCompra("idevento",idevento);
        SeleccionZonaFR seleccionZonaFR= (SeleccionZonaFR) getSupportFragmentManager().findFragmentById(R.id.pagerFragments);
        seleccionZonaFR.Recibir_Funcion_CBol();
    }

    @Override
    public void setSelZona(String numerado, String zona, String idzonaxgrupo, String precio, String comision) {
        set_DatosCompra("numerado",numerado);
        set_DatosCompra("zona",zona);
        set_DatosCompra("idzonaxgrupo",idzonaxgrupo);
        set_DatosCompra("precio",precio);
        set_DatosCompra("comision",comision);
        FRMejDisp frMejDisp =(FRMejDisp) getSupportFragmentManager().findFragmentById(R.id.pagerFragments);
        frMejDisp.RecibirDatos();
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
