package itstam.masboletos.principal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import itstam.masboletos.R;
import itstam.masboletos.acciones_perfil.*;

public class Perfil_Fr extends Fragment {
    View vista;
    RelativeLayout RLDatosPerfil; TextView TXVMsj,txvtengocta,btcrearcta;
    LinearLayout LLPrincipal,LLDatosPerfil;
    LinearLayout llinisesion;
    String nuser;
    TextView txvnuser;
    SharedPreferences prefe_sesion;
    Boolean valida_sesion=false;
    Button btcerrarsesion,btmeventos,btavisopriv,btayuda,btbuzon,btacercade;

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
        btavisopriv=vista.findViewById(R.id.btavisopriv);
        btayuda=vista.findViewById(R.id.btayuda);
        btbuzon=vista.findViewById(R.id.btbuzon);
        btacercade=vista.findViewById(R.id.btacercade);

        prefe_sesion=getActivity().getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        nuser=prefe_sesion.getString("usuario_s", "");
        valida_sesion=prefe_sesion.getBoolean("validasesion",false);
        if(valida_sesion){
            txvnuser.setText(nuser);
            btcerrarsesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cierra_sesion();
                    RLDatosPerfil.setVisibility(View.GONE);
                    vista_no_sesion();
                    btcerrarsesion.setVisibility(View.GONE);
                }
            });
        }else {
            vista_no_sesion();
            btcerrarsesion.setVisibility(View.GONE);
        }
        btmeventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), MisEventos.class);
                startActivity(mainIntent);
            }
        });
        eventos_botones();
        return vista;
    }

    void eventos_botones(){
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
                Toast.makeText(getActivity(),nuser,Toast.LENGTH_LONG).show();
                sesion_iniciada();

            }else {
                Toast.makeText(getActivity(),"Aun no ha iniciado sesión",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sesion_iniciada(){
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
        editor.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
