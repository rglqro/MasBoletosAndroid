package itstam.masboletos.principal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

public class Perfil_Fr extends Fragment {
    View vista;
    RelativeLayout RLDatosPerfil; TextView TXVMsj,txvtengocta,btcrearcta;
    LinearLayout LLPrincipal,LLDatosPerfil;
    LinearLayout llinisesion;
    String nuser;
    TextView txvnuser;
    SharedPreferences prefe_sesion;
    Boolean valida_sesion=false;
    Button btcerrarsesion;

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
        txvnuser=(TextView)vista.findViewById(R.id.txvnuser);
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

        return vista;
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
        TXVMsj.setPadding(dpToPx(10),0,dpToPx(10),0);
        TXVMsj.setText("Únete a Mas Boletos \n\nY comieza a gozar de todos los beneficios que tenemos para ti");
        TXVMsj.setTextSize(dpToPx(5));
        TXVMsj.setLayoutParams(lp);
        llinisesion.addView(TXVMsj);

        btcrearcta= new TextView(getActivity());
        btcrearcta.setBackgroundColor(Color.WHITE);
        btcrearcta.setText("CREAR UN PERFIL AHORA");
        btcrearcta.setTextColor(Color.BLACK);
        btcrearcta.setLayoutParams(lp);
        btcrearcta.setTextSize(dpToPx(5));
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
        txvtengocta.setTextSize(dpToPx(5));
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


    public int dpToPx(int dp) {
        float density = getActivity().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
