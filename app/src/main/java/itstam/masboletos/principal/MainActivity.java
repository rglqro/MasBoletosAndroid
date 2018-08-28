package itstam.masboletos.principal;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

import itstam.masboletos.NetworkUtils;
import itstam.masboletos.R;
import itstam.masboletos.UbicacionAct;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    BoletosPrin frboletos;
    Perfil_Fr frperfil = new Perfil_Fr();
    ImageButton BTInicio,BTUbic,BTPerfil;
    ProgressDialog dialogcarg;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BTInicio=(ImageButton)findViewById(R.id.BTInicio);
        BTUbic=(ImageButton)findViewById(R.id.BTUbicacion);
        BTPerfil=(ImageButton)findViewById(R.id.BTPerfil);


            if(NetworkUtils.isNetworkConnected(this)){
                Menu_Navegacion();
                frboletos = new BoletosPrin();
                getSupportFragmentManager().beginTransaction().add(R.id.contenedor,frboletos).commit();
            }else {
                alertanointernet();
            }

    }

    FragmentManager fm;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void Menu_Navegacion(){
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
            frboletos = new BoletosPrin();
            android.support.v4.app.FragmentTransaction trans1= getSupportFragmentManager().beginTransaction();
            trans1.replace(R.id.contenedor,frboletos);
            trans1.addToBackStack(null);
            trans1.commit();
        }
        if(v==BTUbic){
            BTPerfil.setBackgroundResource(R.color.azulmboscuro);
            BTInicio.setBackgroundResource(R.color.azulmb);
            //BTInicio.setClickable(false); BTPerfil.setClickable(true);
            Intent i=new Intent(getApplicationContext() ,UbicacionAct.class);
            startActivity(i);
        }
        if (v==BTPerfil){
            BTPerfil.setBackgroundResource(R.color.azulmb);
            BTUbic.setBackgroundResource(R.color.azulmboscuro);
            BTInicio.setBackgroundResource(R.color.azulmboscuro);
            BTPerfil.setClickable(false); BTInicio.setClickable(true); BTUbic.setClickable(true);
            frperfil = new Perfil_Fr();
            android.support.v4.app.FragmentTransaction trans2= getSupportFragmentManager().beginTransaction();
            trans2.replace(R.id.contenedor,frperfil);
            trans2.addToBackStack(null);
            trans2.commit();
        }
    }

    public boolean conectadoAInternet() throws InterruptedException, IOException
    { // metodo que verifica que haya conexion a internet con un ping
        String comando = "ping -c 1 www.masboletos.mx";
        return (Runtime.getRuntime().exec (comando).waitFor() == 0);
    }

    void alertanointernet(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(this);
        confirmacion.setMessage("Verifique su conexion a internet para continuar, y despues pulse ACEPTAR\n" +
                "Y si no reinicie el proceso más tarde");
        confirmacion.setTitle("NO HAY CONEXION A INTERNET");
        confirmacion.setCancelable(false);
        confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int i) {
                try {
                    if(conectadoAInternet()){
                        Menu_Navegacion();
                    }else {
                        alertanointernet();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        confirmacion.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                System.exit(0);
            }
        });
        AlertDialog mostrar = confirmacion.create();
        mostrar.show();
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

}

