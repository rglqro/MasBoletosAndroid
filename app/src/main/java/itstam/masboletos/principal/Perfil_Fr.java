package itstam.masboletos.principal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import itstam.masboletos.R;
import itstam.masboletos.acciones_perfil.*;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Perfil_Fr extends Fragment {
    View vista;
    RelativeLayout RLDatosPerfil; TextView TXVMsj,txvtengocta,btcrearcta;
    LinearLayout LLPrincipal,LLDatosPerfil;
    LinearLayout llinisesion,llbotonesuser1,llbotonesuser2;
    String nuser,tipousuario,urlimgorg;
    TextView txvnuser,txvtituloperfil;
    SharedPreferences prefe_sesion;
    Boolean valida_sesion=false;
    Button btcerrarsesion,btmeventos,btavisopriv,btayuda,btbuzon,btacercade, btvalidaboleto,btreportevta;
    ImageView imvfondoorg;

    public Perfil_Fr() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_perfil_, container, false);

        RLDatosPerfil=(RelativeLayout) vista.findViewById(R.id.RLDatosPerfil);
        LLPrincipal =(LinearLayout) vista.findViewById(R.id.LLPrincipal);
        LLDatosPerfil=(LinearLayout)vista.findViewById(R.id.LLDatosPerfil);
        btcerrarsesion=(Button)vista.findViewById(R.id.btcerrarsesion);
        btmeventos=(Button)vista.findViewById(R.id.BtMEventos);
        txvnuser=(TextView)vista.findViewById(R.id.txvnuser);
        txvtituloperfil=vista.findViewById(R.id.txvtituloperfil);
        btavisopriv=vista.findViewById(R.id.btavisopriv);
        btayuda=vista.findViewById(R.id.btayuda);
        btbuzon=vista.findViewById(R.id.btbuzon);
        btacercade=vista.findViewById(R.id.btacercade);
        llbotonesuser1=vista.findViewById(R.id.llbotonesuser1);
        llbotonesuser2=vista.findViewById(R.id.llbotonesuser2);
        btvalidaboleto=vista.findViewById(R.id.btvalidaboleto);
        btreportevta=vista.findViewById(R.id.btreporteventa);
        imvfondoorg=vista.findViewById(R.id.imvfondoorg);

        prefe_sesion=getActivity().getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);

        checar_sesion();
        eventos_botones();

        RLDatosPerfil.getLayoutParams().height=((MainActivity)getActivity()).alto/8;
        return vista;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void checar_sesion(){
        nuser=prefe_sesion.getString("usuario_s", "");
        tipousuario=prefe_sesion.getString("tipousuario","0");
        valida_sesion=prefe_sesion.getBoolean("validasesion",false);
        urlimgorg=prefe_sesion.getString("urlimgorg","surl");
        if(valida_sesion){
            txvnuser.setText(nuser);
            if(tipousuario.equals("2")){
                txvtituloperfil.setText(nuser);
                Picasso.get().load(urlimgorg).error(R.mipmap.logo_masboletos).into(imvfondoorg); Log.e("fondo org",urlimgorg);
                llbotonesuser1.setVisibility(View.GONE);
            }else {
                if(imvfondoorg!=null)
                    imvfondoorg.setBackgroundColor(Color.TRANSPARENT);
                llbotonesuser2.setVisibility(View.GONE);
            }
        }else {
            vista_no_sesion();
            btcerrarsesion.setVisibility(View.GONE);
            llbotonesuser2.setVisibility(View.GONE);
        }
        btcerrarsesion.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                cierra_sesion();
                vista_no_sesion();
            }
        });
    }

    void eventos_botones(){
        btmeventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), MisEventos.class);
                startActivity(mainIntent);
            }
        });
        btavisopriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.masboletos.mx/politicascompra.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btayuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerta_llamada();
            }
        });
        btbuzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), buzonsuger.class);
                startActivity(mainIntent);
            }
        });
        btacercade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), acercade.class);
                startActivity(mainIntent);
            }
        });
        btvalidaboleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), ScannerQR.class);
                startActivity(mainIntent);
            }
        });
        btreportevta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), EventosxOrganizador.class);
                startActivity(mainIntent);
            }
        });
    }

    void alerta_llamada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("¿Deseas comunicarte con Mas Boletos?")
                .setCancelable(false)
                .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"4422122496")));
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void vista_no_sesion(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,30,0,20);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
        RLDatosPerfil.setVisibility(View.GONE);
        llinisesion= new LinearLayout(getActivity());
        llinisesion.setLayoutParams(lp2);
        llinisesion.setOrientation(LinearLayout.VERTICAL);
        llinisesion.setGravity(Gravity.CENTER);
        TXVMsj = new TextView(getActivity());
        TXVMsj.setTextColor(Color.WHITE);
        TXVMsj.setGravity(Gravity.CENTER);
        TXVMsj.setPadding(25,0,25,0);
        TXVMsj.setText("Únete a Mas Boletos \n\nY comieza a gozar de todos los beneficios que tenemos para ti");
        TXVMsj.setTextSize(15);
        TXVMsj.setLayoutParams(lp);
        llinisesion.addView(TXVMsj);

        btcrearcta= new TextView(getActivity());
        btcrearcta.setBackgroundColor(Color.WHITE);
        btcrearcta.setText("CREAR UN PERFIL AHORA");
        btcrearcta.setTextColor(Color.BLACK);
        btcrearcta.setLayoutParams(lp);
        btcrearcta.setTextSize(15);
        btcrearcta.setPadding(50,15,50,15);
        btcrearcta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.masboletos.mx/crearperfil.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        llinisesion.addView(btcrearcta);

        txvtengocta= new TextView(getActivity());
        txvtengocta.setText("YA TENGO CUENTA");
        txvtengocta.setTextColor(Color.WHITE);
        txvtengocta.setTextSize(15);
        txvtengocta.setLayoutParams(lp);
        txvtengocta.setPadding(50,15,50,15);
        txvtengocta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), LoginActivity.class);
                //startActivity(mainIntent);
                startActivityForResult(mainIntent,1014);
            }
        });
        llinisesion.addView(txvtengocta);

        LLDatosPerfil.addView(llinisesion);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1014) {
            if(resultCode == Activity.RESULT_OK) {
                nuser = data.getStringExtra("validasesion");
                tipousuario=data.getStringExtra("tipousuario");
                urlimgorg=data.getStringExtra("urlimgorg");
                sesion_iniciada();
            }else {
                Toast.makeText(getActivity(),"Aun no ha iniciado sesión",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("ResourceType")
    public void sesion_iniciada(){
        if(tipousuario.equals("2")){
            llbotonesuser1.setVisibility(View.GONE);
            llbotonesuser2.setVisibility(View.VISIBLE);
            Picasso.get().load(urlimgorg).error(R.mipmap.logo_masboletos).into(imvfondoorg); Log.e("fondo org",urlimgorg);
            txvtituloperfil.setText(nuser);
        }else{
            imvfondoorg.setImageResource(Color.TRANSPARENT);
        }
        btcerrarsesion.setVisibility(View.VISIBLE);
        llinisesion.setVisibility(View.GONE);
        RLDatosPerfil.setVisibility(View.VISIBLE);
        txvnuser.setText(nuser);
        btmeventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), MisEventos.class);
                startActivity(mainIntent);
            }
        });
    }

    void cierra_sesion() {
        SharedPreferences preferencias=getActivity().getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("usuario_s", "");
        editor.putString("contrasena_s","");
        editor.putBoolean("validasesion",false);
        editor.putString("id_cliente","");
        editor.putString("tipousuario","0");
        editor.commit();
        txvtituloperfil.setText("Perfil");
        RLDatosPerfil.setVisibility(View.GONE);
        btcerrarsesion.setVisibility(View.GONE);
        if (llbotonesuser2.getVisibility() == View.VISIBLE) {
            llbotonesuser2.setVisibility(View.GONE);
        }
        if(llbotonesuser1.getVisibility() == View.GONE){
            llbotonesuser1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
