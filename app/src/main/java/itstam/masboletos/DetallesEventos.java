package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetallesEventos extends AppCompatActivity implements InfoFragment.OnFragmentInteractionListener, ComprarBoletoFr.OnFragmentInteractionListener {

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView imEvento;
    Button BTComprar,BTInfo;
    InfoFragment infoFragment = new InfoFragment();
    ComprarBoletoFr comprarBoletoFr = new ComprarBoletoFr();
    String indiceimagen;
    Bundle args = new Bundle();

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_eventos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("  Regresar");
        collapsingToolbarLayout.setExpandedTitleTextColor(ColorStateList.valueOf(Color.TRANSPARENT));

        BTComprar=(Button)findViewById(R.id.BComprar);
        BTInfo=(Button)findViewById(R.id.BInfo);
        ControlBotones();

        Bundle bundle = getIntent().getExtras();
        indiceimagen=bundle.getString("indiceimagen");
        imEvento=(ImageView) findViewById(R.id.imEvento);
        Picasso.with(getApplicationContext())
                .load(indiceimagen)
                .error(R.id.action_inicio)
                .into(imEvento);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void ControlBotones(){
        BTComprar.setBackgroundResource(R.color.azulmb);
        //BTComprar.setBottom(Color.rgb(146,192,25));
        args.putString("NombreMapa",indiceimagen);
        comprarBoletoFr.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFR,comprarBoletoFr).commit();
        BTComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprarBoletoFr.setArguments(args);
                BTComprar.setBackgroundResource(R.color.azulmb);
                BTInfo.setBackgroundResource(R.color.azulmboscuro);
                FragmentTransaction trans2= getSupportFragmentManager().beginTransaction();
                trans2.replace(R.id.contenedorFR,comprarBoletoFr);
                trans2.commit();
            }
        });
        BTInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFragment.setArguments(args);
                BTInfo.setBackgroundResource(R.color.azulmb);
                BTComprar.setBackgroundResource(R.color.azulmboscuro);
                FragmentTransaction trans2= getSupportFragmentManager().beginTransaction();
                trans2.replace(R.id.contenedorFR,infoFragment);
                trans2.commit();
            }
        });
    }

    public void regresar(View view){
        finish();
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
