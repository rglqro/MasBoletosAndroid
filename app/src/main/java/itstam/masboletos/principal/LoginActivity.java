package itstam.masboletos.principal;
import itstam.masboletos.R;
import itstam.masboletos.carruselcompra.DetallesEventos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText edtcorreo,edtcontrasena;
    int bloqueo_boton=0,ancho,alto;
    Button btisesion;
    JSONArray Elementos=null;
    ImageView imvavatar;
    String msj,usuario,id_cliente,correo,contrasena,tipousuario;
    Boolean resp;
    ProgressDialog dialogcarg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtcorreo=(EditText)findViewById(R.id.edtcorreologin);
        edtcontrasena=(EditText)findViewById(R.id.edtcontrasenalogin);
        imvavatar=findViewById(R.id.imvavatar);
        btisesion=(Button)findViewById(R.id.btsesionlog); btisesion.setBackgroundResource(R.color.grisclaro);
        cambio_texto();
        btisesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bloqueo_boton==1){
                    correo=edtcorreo.getText().toString();
                    contrasena=edtcontrasena.getText().toString();
                    iniciar_sesion();
                }else{
                    Toast.makeText(LoginActivity.this,"Datos incorrectos, verifiquelos",Toast.LENGTH_LONG).show();
                }
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;

        imvavatar.getLayoutParams().height=alto/4;
    }

    void cambio_texto(){
        edtcorreo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable txt) {
                if (/*edtcorreo.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") && txt.length() > 0 &&*/ edtcontrasena.getText().length()>3) {
                    btisesion.setBackgroundResource(R.color.azulmb);
                    bloqueo_boton=1;
                }
                else {
                    bloqueo_boton=0;
                    btisesion.setBackgroundResource(R.color.grisclaro);
                }
            }
        });

        edtcontrasena.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable txt) {
                if(txt.length()>1){
                    btisesion.setBackgroundResource(R.color.azulmb);
                    bloqueo_boton=1;
                }else{
                    bloqueo_boton=0;
                    btisesion.setBackgroundResource(R.color.grisclaro);
                }
            }
        });
    }

    void iniciar_sesion(){
        iniciar_cargando();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url="https://www.masboletos.mx/appMasboletos.fueralinea/validalogin.php";
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            Log.e("resp json",response);
                            Elementos = new JSONArray(response);
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                resp=datos.getBoolean("respuesta");
                                msj=datos.getString("mensaje");
                                id_cliente=datos.getString("id_cliente");
                                usuario=datos.getString("usuario");
                                tipousuario=datos.getString("tipousuario");
                            }
                            if(resp) {
                                Intent intent = new Intent();
                                intent.putExtra("validasesion", usuario);
                                intent.putExtra("tipousuario",tipousuario);
                                setResult(RESULT_OK, intent);
                                guarda_sesion();
                                if (tipousuario.equals("2")){
                                    alerta_inicio_sesion("Bienvenido(a) "+usuario+"\nAhora podrás consultar tus ventas por evento y MAS...");
                                }else {
                                    alerta_inicio_sesion("Bienvenido(a) "+usuario+" a tu cuenta \nDisfruta todos los beneficios de +Boletos");
                                }
                            }else{
                                Toast.makeText(LoginActivity.this,msj,Toast.LENGTH_LONG).show();
                            }
                            cerrar_cargando();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        cerrar_cargando();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("correo", correo);
                params.put("contrasenia", contrasena);
                return params;
            }
        };
        queue.add(strRequest);
    }

    void guarda_sesion() {
        SharedPreferences preferencias=getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("usuario_s", usuario);
        editor.putString("contrasena_s",contrasena);
        editor.putString("id_cliente",id_cliente);
        editor.putBoolean("validasesion",true);
        editor.putString("tipousuario",tipousuario);
        editor.commit();
    }

    void alerta_inicio_sesion(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void regresar(View view){
        finish();
    }

    public void iniciar_cargando(){
        dialogcarg= new ProgressDialog(this,R.style.ProgressDialogStyle);
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
