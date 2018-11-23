package itstam.masboletos.acciones_perfil;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import itstam.masboletos.R;

public class EventosxOrganizador extends AppCompatActivity {
    TabHost threporteorg;
    ProgressDialog dialogcarg;
    JSONArray Elementos;
    String idorg;
    TextView[][]txvinfopaq;
    ArrayList<String> nevento,idevento,fecha,hora;
    ArrayList<Double> aforo,ventaboleto,ventadinero,cortesias,apartados,consignados;
    TableLayout tblreportegral,tblverdetrep,tblcanalventa,tblefectivo,tblboletosrep,tblpaquetesrep;
    TableRow filatbl;
    SharedPreferences prefeuser;
    DecimalFormat df = new DecimalFormat("#0.0");
    DecimalFormat df2 = new DecimalFormat("###,###");
    int alto,ancho;
    Dialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventosx_organizador);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        threporteorg=findViewById(R.id.threporteorg);
        tblreportegral=findViewById(R.id.tblreportegral);
        tblcanalventa=findViewById(R.id.tblcanalventa);
        tblefectivo=findViewById(R.id.tblefectivo);
        tblboletosrep=findViewById(R.id.tblboletosrep);
        tblpaquetesrep=findViewById(R.id.tblpaqueterep);
        prefeuser= getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        idorg=prefeuser.getString("id_cliente","0");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;
        tblreportegral.getLayoutParams().width= (int) (ancho*1.5);
        tblcanalventa.getLayoutParams().width= (int) (ancho*1.5);
        tblefectivo.getLayoutParams().width= (int) (ancho*1.5);
        tblboletosrep.getLayoutParams().width= (int) (ancho*1.5);
        iniciar_tab();
    }

    void iniciar_tab(){
        threporteorg.setup();
        TabHost.TabSpec tab1 = threporteorg.newTabSpec("threporte");  //aspectos de cada Tab (pestaña)
        TabHost.TabSpec tab2 = threporteorg.newTabSpec("thcanal");
        TabHost.TabSpec tab3 = threporteorg.newTabSpec("thefectivo");
        TabHost.TabSpec tab4 = threporteorg.newTabSpec("thboletos");
        TabHost.TabSpec tab5 = threporteorg.newTabSpec("thpaquetes");

        tab1.setIndicator("Reporte General");    //qué queremos que aparezca en las pestañas
        tab1.setContent(R.id.tabrepgral); //definimos el id de cada Tab (pestaña)

        tab2.setIndicator("Canal Venta");
        tab2.setContent(R.id.tabcanalventa);

        tab3.setIndicator("Efectivo");
        tab3.setContent(R.id.tabefectivo);

        tab4.setIndicator("Boletos");
        tab4.setContent(R.id.tabboletos);

        tab5.setIndicator("Paquetes");
        tab5.setContent(R.id.tabpaquetes);

        threporteorg.addTab(tab1); //añadimos los tabs ya programados
        threporteorg.addTab(tab2);
        threporteorg.addTab(tab3);
        threporteorg.addTab(tab4);
        threporteorg.addTab(tab5);
        nevento=new ArrayList<>();
        idevento=new ArrayList<>();
        fecha=new ArrayList<>();
        hora=new ArrayList<>();
        aforo=new ArrayList<>();
        ventaboleto=new ArrayList<>();
        ventadinero=new ArrayList<>();
        cortesias=new ArrayList<>();
        apartados=new ArrayList<>();
        consignados=new ArrayList<>();
        nevento=new ArrayList<>();
        iniciar_cargando();
        consulta_evento("https://www.masboletos.mx/appMasboletos/getEventosOrganizador.php?idorganizador="+idorg,1);
    }

    void consulta_evento(String URL, final int tabla){
        idevento.clear();
        fecha.clear();
        hora.clear();
        aforo.clear();
        ventaboleto.clear();
        ventadinero.clear();
        cortesias.clear();
        apartados.clear();
        consignados.clear();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @TargetApi(Build.VERSION_CODES.O)
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                nevento.add(datos.getString("evento"));
                                idevento.add(datos.getString("idevento"));
                                fecha.add(datos.getString("fecha"));
                                hora.add(datos.getString("hora"));
                                if(tabla==1) {
                                    aforo.add(datos.getDouble("aforo"));
                                    ventaboleto.add(datos.get("VentaBoletos").equals(null) ? 0 : datos.getDouble("VentaBoletos"));
                                    ventadinero.add(datos.get("VentaDinero").equals(null) ? 0 : datos.getDouble("VentaDinero"));
                                    cortesias.add(datos.get("Cortesias").equals(null) ? 0 : datos.getDouble("Cortesias"));
                                    apartados.add(datos.get("apartados").equals(null) ? 0 : datos.getDouble("apartados"));
                                    consignados.add(datos.get("Consignados").equals(null) ? 0 : datos.getDouble("Consignados"));

                                }
                                if(tabla==2){
                                    aforo.add(datos.getDouble("aforo"));
                                    ventaboleto.add(datos.get("VentaBoletos").equals(null) ? 0 : datos.getDouble("VentaBoletos"));
                                    ventadinero.add(datos.get("PuntoVenta").equals(null) ? 0 : datos.getDouble("PuntoVenta"));
                                    cortesias.add(datos.get("Tarjeta").equals(null) ? 0 : datos.getDouble("Tarjeta"));
                                    apartados.add(datos.get("oxxo").equals(null) ? 0 : datos.getDouble("oxxo"));
                                    consignados.add(datos.get("PayPal").equals(null) ? 0 : datos.getDouble("PayPal"));
                                }
                                if(tabla==3){
                                    ventaboleto.add(datos.get("VentaTotal").equals(null) ? 0 : datos.getDouble("VentaTotal"));
                                    ventadinero.add(datos.get("Efectivo").equals(null) ? 0 : datos.getDouble("Efectivo"));
                                    cortesias.add(datos.get("credito").equals(null) ? 0 : datos.getDouble("credito"));
                                    apartados.add(datos.get("debito").equals(null) ? 0 : datos.getDouble("debito"));
                                    consignados.add(datos.get("comercio").equals(null) ? 0 : datos.getDouble("comercio"));
                                }
                                if(tabla==4){
                                    ventaboleto.add(datos.get("Venta").equals(null) ? 0 : datos.getDouble("Venta"));
                                    ventadinero.add(datos.get("Cortesia").equals(null) ? 0 : datos.getDouble("Cortesia"));
                                    cortesias.add(datos.get("Consignado").equals(null) ? 0 : datos.getDouble("Consignado"));
                                    apartados.add(datos.get("Electronico").equals(null) ? 0 : datos.getDouble("Electronico"));
                                }
                                if(tabla==5){
                                    ventaboleto.add(datos.get("Paquetes").equals(null) ? 0 : datos.getDouble("Paquetes"));
                                }
                            }
                            if(tabla==1)
                                pintarinfo();
                            if (tabla==2)
                                pintarinfo_canal();
                            if (tabla==3)
                                pintainfo_efectivo();
                            if (tabla==4)
                                pintainfo_boletos();
                            if (tabla==5)
                                pintainfo_paquetes();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void pintarinfo(){
        df.setRoundingMode(RoundingMode.CEILING);
        txvinfopaq= new TextView[idevento.size()][15];
        int j;
        float pteventa,ptecorte,pteapartado,pteconsig,pteocup;
        for (int i=0;i<idevento.size();i++){
            filatbl= new TableRow(this);

            j=0;
            if(i%2==0)
                filatbl.setBackgroundResource(R.color.grismasclaro);
            filatbl.setGravity(Gravity.CENTER);
            /*Este es el primero*/
            creadortxv(i,j,"Ver");j++;
            creadortxv(i,j,nevento.get(i));j++;
            creadortxv(i,j,fecha.get(i));j++;
            creadortxv(i,j,hora.get(i));j++;
            creadortxv(i,j,df2.format(aforo.get(i)));j++;

            creadortxv(i,j,df2.format(ventaboleto.get(i)));j++;
            creadortxv(i,j,df2.format(ventadinero.get(i)));j++;
            pteventa= (float) (ventaboleto.get(i)/aforo.get(i)*100); Log.e("pteventa", String.valueOf(pteventa));
            creadortxv(i,j,df.format(pteventa)+"%");j++;

            creadortxv(i,j,cortesias.get(i).toString());j++;
            ptecorte= (float) ((cortesias.get(i) / aforo.get(i)) * 100);
            creadortxv(i,j,df.format(ptecorte)+"%");j++;

            creadortxv(i,j,apartados.get(i).toString());j++;
            pteapartado= (float) ((apartados.get(i) / aforo.get(i)) * 100);
            creadortxv(i,j,df.format(pteapartado)+"%");j++;

            creadortxv(i,j,consignados.get(i).toString());j++;
            pteconsig= (float) ((consignados.get(i) / aforo.get(i)) * 100);
            creadortxv(i,j,df.format(pteconsig)+"%");j++;

            pteocup= (float) (ventaboleto.get(i) + cortesias.get(i) + apartados.get(i) + consignados.get(i));
            pteocup= (float) ((pteocup/aforo.get(i))*100);
            creadortxv(i,j,df.format(pteocup)+"%");j++;

            tblreportegral.addView(filatbl);
        }
        consulta_evento("https://www.masboletos.mx/appMasboletos/getEventosOrganizadorCanal.php?idorganizador="+idorg,2);
    }

    void pintarinfo_canal(){
        df.setRoundingMode(RoundingMode.CEILING);
        txvinfopaq=null;
        txvinfopaq= new TextView[idevento.size()][15];
        int j;
        float pteventa,ptecorte,pteapartado,pteconsig,pteocup;
        for (int i=0;i<idevento.size();i++){
            filatbl= new TableRow(this);
            j=0;
            if(i%2==0)
                filatbl.setBackgroundResource(R.color.grismasclaro);
            filatbl.setGravity(Gravity.CENTER);
            /*Este es el segundo*/
            j++;
            creadortxv(i,j,nevento.get(i));j++;
            creadortxv(i,j,fecha.get(i));j++;
            creadortxv(i,j,hora.get(i));j++;
            creadortxv(i,j,df2.format(aforo.get(i)));j++;
            creadortxv(i,j,df2.format(ventaboleto.get(i)));j++;

            creadortxv(i,j,df2.format(ventadinero.get(i)));j++;
            pteventa= ventaboleto.get(i)!=0 ? (float) ((ventadinero.get(i)*100)/ventaboleto.get(i)) :0; //Log.e("pteventa",pteventa.toString());
            creadortxv(i,j,df.format(pteventa)+"%");j++;

            creadortxv(i,j,cortesias.get(i).toString());j++;
            ptecorte= ventaboleto.get(i)!=0 ? (float) ((cortesias.get(i)*100) / ventaboleto.get(i)) :0; Log.e("ptecredito", String.valueOf(ptecorte));
            creadortxv(i,j,df.format(ptecorte)+"%");j++;

            creadortxv(i,j,apartados.get(i).toString());j++;
            pteapartado= ventaboleto.get(i)!=0 ? (float) ((apartados.get(i)*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteapartado)+"%");j++;

            creadortxv(i,j,consignados.get(i).toString());j++;
            pteconsig= ventaboleto.get(i)!=0 ? (float) ((consignados.get(i)*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteconsig)+"%");j++;

            pteocup= (float) ((ventaboleto.get(i)/aforo.get(i))*100);
            creadortxv(i,j,df.format(pteocup)+"%");j++;

            tblcanalventa.addView(filatbl);
        }
        consulta_evento("https://www.masboletos.mx/appMasboletos/getEventosOrganizadorEfectivo.php?idorganizador="+idorg,3);
    }

    void pintainfo_efectivo(){
        df.setRoundingMode(RoundingMode.CEILING);
        txvinfopaq=null;
        txvinfopaq= new TextView[idevento.size()][15];
        int j;
        float pteventa,ptecorte,pteapartado,pteconsig;
        for (int i=0;i<idevento.size();i++){
            filatbl= new TableRow(this);
            j=0;
            if(i%2==0)
                filatbl.setBackgroundResource(R.color.grismasclaro);
            filatbl.setGravity(Gravity.CENTER);

            /*Este es el tercero*/
            j++;
            creadortxv(i,j,nevento.get(i));j++;
            creadortxv(i,j,fecha.get(i));j++;
            creadortxv(i,j,hora.get(i));j++;
            creadortxv(i,j,"$"+df2.format(ventaboleto.get(i)));j++;

            creadortxv(i,j,"$"+df2.format(ventadinero.get(i)));j++;
            pteventa= ventaboleto.get(i)!=0 ? (float) ((ventadinero.get(i)*100)/ventaboleto.get(i)) :0; //Log.e("pteventa",pteventa.toString());
            creadortxv(i,j,df.format(pteventa)+"%");j++;

            pteventa= (float) (cortesias.get(i)+apartados.get(i)+consignados.get(i));
            creadortxv(i,j,"$"+df2.format(pteventa));j++;
            pteventa= ventaboleto.get(i)!=0 ? (float) ((pteventa*100)/ventaboleto.get(i)) :0; //Log.e("pteventa",pteventa.toString());
            creadortxv(i,j,df.format(pteventa)+"%");j++;

            creadortxv(i,j,"$"+cortesias.get(i).toString());j++;
            ptecorte= ventaboleto.get(i)!=0 ? (float) ((cortesias.get(i)*100) / ventaboleto.get(i)) :0; Log.e("ptecredito", String.valueOf(ptecorte));
            creadortxv(i,j,df.format(ptecorte)+"%");j++;

            creadortxv(i,j,"$"+apartados.get(i).toString());j++;
            pteapartado= ventaboleto.get(i)!=0 ? (float) ((apartados.get(i)*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteapartado)+"%");j++;

            creadortxv(i,j,"$"+consignados.get(i).toString());j++;
            pteconsig= ventaboleto.get(i)!=0 ? (float) ((consignados.get(i)*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteconsig)+"%");j++;


            tblefectivo.addView(filatbl);
        }
        consulta_evento("https://www.masboletos.mx/appMasboletos/getEventosOrganizadorBoletos.php?idorganizador="+idorg,4);
    }

    void pintainfo_boletos(){
        df.setRoundingMode(RoundingMode.CEILING);
        txvinfopaq= new TextView[idevento.size()][13];
        int j;
        float pteventa,ptecorte,pteapartado,pteconsig,pteocup;
        for (int i=0;i<idevento.size();i++){
            filatbl= new TableRow(this);

            j=0;
            if(i%2==0)
                filatbl.setBackgroundResource(R.color.grismasclaro);
            filatbl.setGravity(Gravity.CENTER);
            /*Este es el primero*/
            j++;
            creadortxv(i,j,nevento.get(i));j++;
            creadortxv(i,j,fecha.get(i));j++;
            creadortxv(i,j,hora.get(i));j++;
            pteventa= (float) (ventaboleto.get(i)+apartados.get(i)+ventadinero.get(i)+cortesias.get(i));
            creadortxv(i,j, String.valueOf(pteventa));j++;
            creadortxv(i,j,df2.format(ventaboleto.get(i)));j++;
            creadortxv(i,j,df2.format(ventadinero.get(i)));j++;
            creadortxv(i,j,cortesias.get(i).toString());j++;

            pteventa= (float) (apartados.get(i)+ventadinero.get(i)+cortesias.get(i));
            creadortxv(i,j, String.valueOf(pteventa));j++;
            pteapartado= ventaboleto.get(i)!=0 ? (float) ((pteventa*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteapartado)+"%");j++;

            creadortxv(i,j,apartados.get(i).toString());j++;
            pteapartado= ventaboleto.get(i)!=0 ? (float) ((apartados.get(i)*100) / ventaboleto.get(i)) :0;
            creadortxv(i,j,df.format(pteapartado)+"%");


            tblboletosrep.addView(filatbl);
        }
        consulta_evento("https://www.masboletos.mx/appMasboletos/getEventosOrganizadorPaquetes.php?idorganizador="+idorg,5);
    }

    void pintainfo_paquetes(){
        df.setRoundingMode(RoundingMode.CEILING);
        txvinfopaq= new TextView[idevento.size()][6];
        int j;
        for (int i=0;i<idevento.size();i++){
            filatbl= new TableRow(this);

            j=0;
            if(i%2==0)
                filatbl.setBackgroundResource(R.color.grismasclaro);
            filatbl.setGravity(Gravity.CENTER);
            /*Este es el primero*/
            j++;
            creadortxv(i,j,nevento.get(i));j++;
            creadortxv(i,j,fecha.get(i));j++;
            creadortxv(i,j,hora.get(i));j++;
            creadortxv(i,j,df2.format(ventaboleto.get(i)));j++;

            tblpaquetesrep.addView(filatbl);
        }
        cerrar_cargando();
    }

    void creadortxv(int i,int j,String txt){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,2);
        txvinfopaq[i][j]=new TextView(this);
        txvinfopaq[i][j].setText(txt);
        txvinfopaq[i][j].setTextColor(Color.BLACK);
        if(j==1)
            txvinfopaq[i][j].setLayoutParams(lp2);
        else
            txvinfopaq[i][j].setLayoutParams(lp);
        if(j==0) {
            txvinfopaq[i][j].setTextColor(ContextCompat.getColor(this, R.color.azul_link));
            txvinfopaq[i][j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detalles_ver();
                }
            });
        }
        txvinfopaq[i][j].setGravity(Gravity.CENTER);
        txvinfopaq[i][j].setPadding(20,0,0,0);
        filatbl.addView(txvinfopaq[i][j]);
    }

    void detalles_ver(){
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialogver_detalle);
        customDialog.show();
        tblverdetrep=customDialog.findViewById(R.id.tblverdetrep);
        Window window = customDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tblverdetrep.getLayoutParams().width= (int) (ancho*1.5);
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
        if(dialogcarg!=null)
            dialogcarg.dismiss();
    }

    public void regresar(View view){
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
