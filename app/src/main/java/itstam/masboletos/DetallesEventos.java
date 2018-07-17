package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jackandphantom.blurimage.BlurImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class DetallesEventos extends AppCompatActivity implements  View.OnClickListener
,ComprarBoletoFr.Funcion_NumBolListener,SeleccionZonaFR.SelZonaList,FRMejDisp.Continuar_compra{

    ImageView IMVFondo,IMVEvento;
    String indiceimagen,nombreEvento,eventogrupo;
    NonSwipeableViewPager viewPager;
    FRPagerAdapter adapter;
    TabLayout tabLayout;
    String[] FRNombres;
    ImageButton IMBTRegresar;
    TextView TXVNEvento;
    FRPagerAdOrder pagerAdOrder;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);
        TXVNEvento=(TextView)findViewById(R.id.txvNombreEve);
        IMVFondo=(ImageView)findViewById(R.id.IMVFondo);
        IMVEvento=(ImageView)findViewById(R.id.IMVEvento);
        IMBTRegresar=(ImageButton)findViewById(R.id.imBtRegresar);
        Bundle bundle = getIntent().getExtras();
        indiceimagen=bundle.getString("indiceimagen");
        SharedPreferences prefe=this.getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        TXVNEvento.setText((prefe.getString("NombreEvento","")));

        difuminar_imagen();
        IniciarFragments();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void IniciarFragments(){
        tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("1. Cantidad de Boletos"));
        tabLayout.addTab(tabLayout.newTab().setText("2"));
        tabLayout.addTab(tabLayout.newTab().setText("3"));
        tabLayout.addTab(tabLayout.newTab().setText("4"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (NonSwipeableViewPager) findViewById(R.id.pagerFragments);
        adapter = new FRPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext(),nombreEvento,eventogrupo);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        FRNombres=new String[tabLayout.getTabCount()];
        FRNombres[0]="1. Cantidad de Boletos";FRNombres[1]="2. Selección de Zona";FRNombres[2]="3. Mejor disponible";FRNombres[3]="4. Forma de Pago";
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setText(FRNombres[tab.getPosition()]);
            }

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
        pagina_anterior();
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

    Bitmap imageBlur;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void difuminar_imagen(){

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageBlur=bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.drawable.ic_inicio)
                .into(target);
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.drawable.ic_inicio)
                .into(IMVEvento);
        BlurImage.with(getApplicationContext()).load(imageBlur).intensity(20).Async(true).into(IMVFondo);
        IMVFondo.setScaleType(ImageView.ScaleType.FIT_XY);
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
        if (frMejDisp!=null){
            frMejDisp.RecibirDatos();
        }else{
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            FRMejDisp newFragment = new FRMejDisp();

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.pagerFragments, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    @Override
    public void seguir_compra() {
        FPagoFR fPagoFR=(FPagoFR) getSupportFragmentManager().findFragmentById(R.id.pagerFragments);
        fPagoFR.RecepcionDatos();
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }




    @Override
    public void onBackPressed() {
        pagina_anterior();
        //super.onBackPressed();
    }
}
