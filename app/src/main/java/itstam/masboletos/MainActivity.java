package itstam.masboletos;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements BoletosPrin.OnFragmentInteractionListener, Perfil_Fr.OnFragmentInteractionListener, View.OnClickListener {
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    BoletosPrin frboletos;
    Perfil_Fr frperfil = new Perfil_Fr();
    ImageButton BTInicio,BTUbic,BTPerfil;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTInicio=(ImageButton)findViewById(R.id.BTInicio);
        BTUbic=(ImageButton)findViewById(R.id.BTUbicacion);
        BTPerfil=(ImageButton)findViewById(R.id.BTPerfil);

        Menu_Navegacion();
    }

    FragmentManager fm;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void Menu_Navegacion(){
        frboletos = new BoletosPrin();
        fm =getSupportFragmentManager();
        fm.beginTransaction().add(R.id.contenedor,frboletos).commit();
        BTInicio.setOnClickListener(this);
        BTUbic.setOnClickListener(this);
        BTPerfil.setOnClickListener(this);

        BTInicio.setBackgroundResource(R.color.azulmb);
        BTInicio.setClickable(false);
    }


    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public void onClick(View v) {

        if(v==BTInicio){
            BTInicio.setBackgroundResource(R.color.azulmb);
            BTPerfil.setBackgroundResource(R.color.azulmboscuro);
            BTUbic.setBackgroundResource(R.color.azulmboscuro);
            BTInicio.setClickable(false); BTUbic.setClickable(true); BTPerfil.setClickable(true);
            Fragment frg = getSupportFragmentManager().findFragmentById(R.id.contenedor);
            if(frg!=null){
                fm.beginTransaction().remove(frg).commit();
                fm.beginTransaction().add(R.id.contenedor,frboletos).commit();
            }else {
                fm.beginTransaction().replace(R.id.contenedor,frboletos).commit();
            }
        }
        if(v==BTUbic){
            /*BTUbic.setBackgroundResource(R.color.azulmb);
            BTPerfil.setBackgroundResource(R.color.azulmboscuro);
            BTInicio.setBackgroundResource(R.color.azulmboscuro);
            BTUbic.setClickable(false); BTInicio.setClickable(true); BTPerfil.setClickable(true);*/
            Intent i=new Intent(getApplicationContext() ,UbicacionAct.class);
            startActivity(i);
        }
        if (v==BTPerfil){
            BTPerfil.setBackgroundResource(R.color.azulmb);
            BTUbic.setBackgroundResource(R.color.azulmboscuro);
            BTInicio.setBackgroundResource(R.color.azulmboscuro);
            BTPerfil.setClickable(false); BTInicio.setClickable(true); BTUbic.setClickable(true);
            Fragment frg2 = getSupportFragmentManager().findFragmentById(R.id.contenedor);
            if(frg2!=null){
                fm.beginTransaction().remove(frg2).commit();
                fm.beginTransaction().add(R.id.contenedor,frperfil).commit();
            }else {
                fm.beginTransaction().replace(R.id.contenedor,frperfil).commit();
            }
        }
    }
}

