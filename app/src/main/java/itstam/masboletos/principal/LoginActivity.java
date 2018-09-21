package itstam.masboletos.principal;
import itstam.masboletos.R;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText edtcorreo,edtcontrasena;
    int bloqueo_boton=0,ancho,alto;
    Button btisesion;
    JSONArray Elementos=null;
    ImageView imvavatar;
    String msj,usuario,id_cliente,correo,contrasena,tipousuario;
    Boolean resp;


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
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        correo=edtcorreo.getText().toString(); contrasena=edtcontrasena.getText().toString();
        String URL="https://www.masboletos.mx/appMasboletos/validalogin.php?correo="+correo+"&contrasenia="+contrasena;
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(LoginActivity.this,"Error al iniciar sesión",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
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
}
