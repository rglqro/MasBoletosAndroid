package itstam.masboletos;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class OrdenarBoleto extends AppCompatActivity implements FPagoFR.OnFragmentInteractionListener,FEntregaFr.OnFragmentInteractionListener
                                                                ,RevisionFR.OnFragmentInteractionListener,UsuarioFR.OnFragmentInteractionListener
                                                                ,CuentaFR.OnFragmentInteractionListener{

    TabLayout tabLayout;
    ImageView IMVFondo, IMVEvento;
    String URLIMEvento;
    private int BLUR_PRECENTAGE = 50;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenar_boleto);
        IMVFondo=(ImageView)findViewById(R.id.IMVFondo);
        IMVEvento=(ImageView)findViewById(R.id.IMVEvento);
        SharedPreferences prefe=getSharedPreferences("InfoEvento", Context.MODE_PRIVATE);
        URLIMEvento=(prefe.getString("URLIMCuadrada",""));
        iniciarTabLY();
        difuminar_imagen();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void difuminar_imagen(){
        Picasso.get()
                .load(URLIMEvento)
                .error(R.drawable.ic_inicio)
                .into(IMVEvento);
        Bitmap resultBmp = BlurCreador.blur(this, BitmapFactory.decodeResource(getResources(),R.drawable.mblogo));
        IMVFondo.setImageBitmap(resultBmp);
    }

    void iniciarTabLY(){
        tabLayout = (TabLayout) findViewById(R.id.TBLOrdenar);
        tabLayout.addTab(tabLayout.newTab().setText("Forma de Pago"));
        tabLayout.addTab(tabLayout.newTab().setText("Forma de Entrega"));
        tabLayout.addTab(tabLayout.newTab().setText("Revision"));
        tabLayout.addTab(tabLayout.newTab().setText("Usuario"));
        tabLayout.addTab(tabLayout.newTab().setText("Cuenta"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.VPFROrdenar);
        final FRPagerAdOrder adapter = new FRPagerAdOrder(getSupportFragmentManager(), tabLayout.getTabCount(),getApplicationContext());
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

    @Override
    public void onFragmentInteraction(Uri uri) {

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
