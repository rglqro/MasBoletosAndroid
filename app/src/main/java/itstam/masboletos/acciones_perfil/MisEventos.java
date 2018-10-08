package itstam.masboletos.acciones_perfil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import itstam.masboletos.R;
import itstam.masboletos.carruselcompra.DetallesEventos;

public class MisEventos extends AppCompatActivity {

    JSONArray Elementos;
    ProgressDialog dialogcarg;
    int ancho,alto;
    View view;
    Boolean validasesion=false;
    SharedPreferences prefeuser;
    String idcliente;
    TableLayout tblmiseventos,tblmiseventospas;
    TextView[][] infomiseventos,infomiseventospass;
    ArrayList<ImageView> imagseventos,imagseventospass;
    ArrayList<View>separadores;
    ArrayList<String> cantidadlist,eventolist,fechalist,statuslist,cantidadlistpass,eventolistpass,
            fechalistpass,statuslistpass,fentregalis,transaccionlist,imagenlist,imagenlistpass;
    TableRow filatbl,rowsep,rowsep2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);
        prefeuser= getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        idcliente=prefeuser.getString("id_cliente","0");
        validasesion=prefeuser.getBoolean("validasesion",false);
        tblmiseventos=findViewById(R.id.tblmisboletos);
        tblmiseventospas=findViewById(R.id.tblmiseventospas);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;
        if(validasesion){
            iniciar_cargando();
            consulta_miseventos("https://www.masboletos.mx/appMasboletos/getMisEventosUsuario.php?idcliente="+idcliente,1);
        }else{
            AlertaBoton("Inicio de Sesión","Debe iniciar sesion para poder ver este contenido").show();
        }
    }

    void consulta_miseventos(String URL, final int numconsult){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            if (numconsult==1) {
                                cantidadlist = new ArrayList<>();
                                eventolist = new ArrayList<>();
                                fechalist = new ArrayList<>();
                                statuslist = new ArrayList<>();
                                fentregalis = new ArrayList<>();
                                transaccionlist= new ArrayList<>();
                                imagenlist= new ArrayList<>();
                                for (int i = 0; i < Elementos.length(); i++) {
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    cantidadlist.add(datos.getString("cantidad"));
                                    eventolist.add(datos.getString("evento"));
                                    fechalist.add(datos.getString("fechaevento"));
                                    statuslist.add(datos.getString("estatus"));
                                    fentregalis.add(datos.getString("idforma"));
                                    transaccionlist.add(datos.getString("transaccion"));
                                    if(datos.getString("imagen").equals("null")){
                                        imagenlist.add("https://www.masboletos.mx/img/imgMASBOLETOS.jpg");
                                    }else{
                                        imagenlist.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                                    }
                                }
                                consulta_miseventos("https://www.masboletos.mx/appMasboletos/getMisEventosPasadosUsuario.php?idcliente="+idcliente,2);
                            }else{
                                cantidadlistpass = new ArrayList<>();
                                eventolistpass = new ArrayList<>();
                                fechalistpass = new ArrayList<>();
                                statuslistpass = new ArrayList<>();
                                imagenlistpass= new ArrayList<>();
                                for (int i = 0; i < Elementos.length(); i++) {
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    cantidadlistpass.add(datos.getString("cantidad"));
                                    eventolistpass.add(datos.getString("evento"));
                                    fechalistpass.add(datos.getString("fechaevento"));
                                    statuslistpass.add(datos.getString("estatus"));
                                    if(datos.getString("imagen").equals("null")){
                                        imagenlistpass.add("https://www.masboletos.mx/img/imgMASBOLETOS.jpg");
                                    }else{
                                        imagenlistpass.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                                    }
                                }
                                pintar_mis_boletos();
                                pintar_miseventos_pass();
                                cerrar_cargando();
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
                        Snackbar.make(view,"Error...",Snackbar.LENGTH_LONG).show();
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void pintar_mis_boletos(){
        infomiseventos= new TextView[eventolist.size()][5];
        separadores= new ArrayList<>();
        imagseventos= new ArrayList<>();
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        for (int i=0;i<eventolist.size();i++){
            filatbl= new TableRow(this);
            filatbl.setGravity(Gravity.CENTER);
            infomiseventos[i][0]=new TextView(this);
            infomiseventos[i][0].setText(cantidadlist.get(i));
            infomiseventos[i][0].setTextColor(Color.BLACK);
            infomiseventos[i][0].setLayoutParams(lp);
            infomiseventos[i][0].setGravity(Gravity.CENTER);
            infomiseventos[i][0].setPadding(20,0,0,0);
            filatbl.addView(infomiseventos[i][0]);

            /*infomiseventos[i][1]=new TextView(this);
            infomiseventos[i][1].setText(eventolist.get(i));
            infomiseventos[i][1].setTextColor(Color.BLACK);
            infomiseventos[i][1].setLayoutParams(lp);
            infomiseventos[i][1].setGravity(Gravity.CENTER_VERTICAL);
            filatbl.addView(infomiseventos[i][1]);*/

            imagseventos.add(new ImageView(this));
            Picasso.get().load(imagenlist.get(i)).error(R.drawable.imgmberror).into(imagseventos.get(i));
            imagseventos.get(i).setBackgroundColor(Color.TRANSPARENT);
            imagseventos.get(i).setLayoutParams(lp);
            imagseventos.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
            imagseventos.get(i).setAdjustViewBounds(true);
            imagseventos.get(i).setId(i);
            imagseventos.get(i).setMaxWidth(ancho/4);
            imagseventos.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),eventolist.get(view.getId()),Toast.LENGTH_SHORT).show();
                }
            });
            filatbl.addView(imagseventos.get(i));

            infomiseventos[i][2]=new TextView(this);
            infomiseventos[i][2].setText(fechalist.get(i));
            infomiseventos[i][2].setTextColor(Color.BLACK);
            infomiseventos[i][2].setLayoutParams(lp);
            infomiseventos[i][2].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventos[i][2]);

            infomiseventos[i][3]=new TextView(this);
            infomiseventos[i][3].setText(statuslist.get(i));
            infomiseventos[i][3].setTextColor(Color.BLACK);
            infomiseventos[i][3].setLayoutParams(lp);
            infomiseventos[i][3].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventos[i][3]);

            infomiseventos[i][4]=new TextView(this);
            if(fentregalis.get(i).equals("2")){
                infomiseventos[i][4].setText("Visualizar");
                infomiseventos[i][4].setTextColor(Color.BLUE);
                infomiseventos[i][4].setId(i);
                infomiseventos[i][4].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MisEventos.this, BoletoElectronico.class);
                        intent.putExtra("transaccion", transaccionlist.get(view.getId()));
                        startActivity(intent);
                    }
                });
            }else{
                infomiseventos[i][4].setTextColor(Color.BLACK);
                infomiseventos[i][4].setText("N/D");
            }
            infomiseventos[i][4].setLayoutParams(lp);
            infomiseventos[i][4].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventos[i][4]);

            tblmiseventos.addView(filatbl);

            rowsep= new TableRow(this);
            separadores.add(new View(this));
            separadores.get(i).setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3,1));
            separadores.get(i).setBackgroundResource(R.color.gris);
            rowsep.addView(separadores.get(i));
            tblmiseventos.addView(rowsep);
        }
    }

    void pintar_miseventos_pass(){
        infomiseventospass= new TextView[eventolistpass.size()][4];
        separadores= new ArrayList<>();
        imagseventospass= new ArrayList<>();
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        for (int i=0;i<eventolistpass.size();i++){
            filatbl= new TableRow(this);
            filatbl.setGravity(Gravity.CENTER);
            infomiseventospass[i][0]=new TextView(this);
            infomiseventospass[i][0].setText(cantidadlistpass.get(i));
            infomiseventospass[i][0].setTextColor(Color.BLACK);
            infomiseventospass[i][0].setLayoutParams(lp);
            infomiseventospass[i][0].setPadding(20,0,0,0);
            infomiseventospass[i][0].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventospass[i][0]);

            /*infomiseventospass[i][1]=new TextView(this);
            infomiseventospass[i][1].setText(eventolistpass.get(i));
            infomiseventospass[i][1].setTextColor(Color.BLACK);
            infomiseventospass[i][1].setLayoutParams(lp);
            infomiseventospass[i][1].setGravity(Gravity.CENTER_VERTICAL);
            filatbl.addView(infomiseventospass[i][1]);*/

            imagseventospass.add(new ImageView(this));
            Picasso.get().load(imagenlistpass.get(i)).error(R.drawable.imgmberror).into(imagseventospass.get(i));
            imagseventospass.get(i).setBackgroundColor(Color.TRANSPARENT);
            imagseventospass.get(i).setLayoutParams(lp);
            imagseventospass.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
            imagseventospass.get(i).setAdjustViewBounds(true);
            imagseventospass.get(i).setId(i);
            imagseventospass.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),eventolistpass.get(view.getId()),Toast.LENGTH_SHORT).show();
                }
            });
            filatbl.addView(imagseventospass.get(i));

            infomiseventospass[i][2]=new TextView(this);
            infomiseventospass[i][2].setText(fechalistpass.get(i));
            infomiseventospass[i][2].setTextColor(Color.BLACK);
            infomiseventospass[i][2].setLayoutParams(lp);
            infomiseventospass[i][2].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventospass[i][2]);

            infomiseventospass[i][3]=new TextView(this);
            infomiseventospass[i][3].setText(statuslistpass.get(i));
            infomiseventospass[i][3].setTextColor(Color.BLACK);
            infomiseventospass[i][3].setLayoutParams(lp);
            infomiseventospass[i][3].setGravity(Gravity.CENTER);
            filatbl.addView(infomiseventospass[i][3]);

            tblmiseventospas.addView(filatbl);

            rowsep2= new TableRow(this);
            separadores.add(new View(this));
            separadores.get(i).setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3,1));
            separadores.get(i).setBackgroundResource(R.color.gris);
            rowsep2.addView(separadores.get(i));
            tblmiseventospas.addView(rowsep2);
        }
    }

    public void regresar(View view) {
        finish();
    }

    public AlertDialog AlertaBoton(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                .setCancelable(false);
        return builder.create();
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
