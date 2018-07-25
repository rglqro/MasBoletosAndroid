package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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


public class DetallesEventos extends AppCompatActivity implements  View.OnClickListener {

    ImageView IMVFondo,IMVEvento;
    String indiceimagen,nombreEvento;
    TabLayout tabLayout;
    String[] FRNombres;
    ImageButton IMBTRegresar;
    TextView TXVNEvento;
    int contadorTab=0;
    ProgressDialog dialogcarg;


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
        set_DatosCompra("Cant_boletos","0");
        set_DatosCompra("posEve","0");
    }

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
        FRNombres[0]="1. Cantidad de Boletos";FRNombres[1]="2. Selección de Zona";FRNombres[2]="3. Mas Boletos te recomienda";FRNombres[3]="4. Forma de Pago";
        FRNombres[4]="5. Forma de Entrega"; FRNombres[5]="6. Revisión"; FRNombres[6]="7. Usuario"; FRNombres[7]="8. Cuenta";
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

    public void pagina_anterior(){
        if(contadorTab>0) {
            contadorTab--;
            TabLayout.Tab tab = tabLayout.getTabAt(contadorTab);
            tab.select();
        }else {
            finish();
        }
    }

    public void regresar(View view){
        onBackPressed();
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
                BlurImage.with(getApplicationContext()).load(imageBlur).intensity(20).Async(true).into(IMVFondo);
                IMVEvento.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Log.e("ImagenEventourl",indiceimagen);
        Picasso.get().load(indiceimagen).error(R.drawable.mbiconor).into(target);
        IMVFondo.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void iniciar_cargando(){
        dialogcarg= new ProgressDialog(this);
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
        TabLayout.Tab tab = tabLayout.getTabAt(contadorTab);
        tab.select();
    }

    public AlertDialog AlertaBoton(String titulo,String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
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
        super.onBackPressed();
    }
}
