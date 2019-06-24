package itstam.masboletos.principal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.jackandphantom.blurimage.BlurImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import itstam.masboletos.R;
import itstam.masboletos.carruselcompra.DetallesEventos;

public class PaquetesAct extends AppCompatActivity {
    String idorgpaq,nombrepaq,domiciliopaq,telpaq,mailpaq,imapaq;
    SharedPreferences prefe;
    JSONArray Elementos;
    ImageView imvpaq,imvfondopaq;
    TextView txvnombrepaq,txvinfopaq;
    ArrayList<String> nombrepaqxorg,imagpaqxorg,ideventopack,infoevtopaq,imgevtopaq;
    ArrayList<Double> preciopaqxorg;
    ArrayList<View> separadores;
    TableLayout tblpaquetes;
    ArrayList<ImageView> imvpaqxorg,imvevtopaq;
    ArrayList<TextView> txvnombrepaqxorg,txvpreciopaqxorg,btverpaq,btcomprarpack,txvevtopaq;
    LinearLayout llbotones;
    DecimalFormat df = new DecimalFormat("#0.00");
    TableRow rowpaqxorg;
    int ancho,alto;
    RelativeLayout rlimagspaq;
    LinearLayout llevtospaq;
    ProgressDialog dialogcarg;
    Dialog cdevtopaq = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paquetes);
        prefe=this.getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idorgpaq=prefe.getString("idorgpaq","0");
        imvpaq=(ImageView)findViewById(R.id.IMVPaq);
        imvfondopaq=(ImageView)findViewById(R.id.IMVFondopaq);
        txvnombrepaq=(TextView)findViewById(R.id.txvnombrepaq);
        txvinfopaq=(TextView)findViewById(R.id.txvinfopaq);
        rlimagspaq=findViewById(R.id.rlimagspaq);
        tblpaquetes=(TableLayout) findViewById(R.id.tblpaquetes);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;
        consulta_info_org();
        consulta_paqxorg();

        rlimagspaq.getLayoutParams().height=alto/5;
        imvpaq.setMaxWidth(ancho/3);
    }

    void consulta_info_org(){
        // Initialize a new RequestQueue instance
        iniciar_cargando();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos/getPaqueteInfo.php?idorganizador="+idorgpaq; //Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                nombrepaq=datos.getString("nombre");        if(nombrepaq.equals("null")) nombrepaq="";
                                domiciliopaq=datos.getString("domicilio");  if(domiciliopaq.equals("null")) domiciliopaq="";
                                telpaq=datos.getString("telefono");         if(telpaq.equals("null")) telpaq="";
                                mailpaq=datos.getString("mail");            if(mailpaq.equals("null")) mailpaq="";
                                imapaq="https://www.masboletos.mx/sica/imgEventos/"+datos.getString("banner");
                            }
                            pintar_detalles();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error en la consulta: \n"+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void pintar_detalles(){
        //Log.e("imagen",imapaq);
        Picasso.get().load(imapaq).error(R.drawable.mbiconor).into(imvpaq, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap imageBlur=((BitmapDrawable)imvpaq.getDrawable()).getBitmap();
                BlurImage.with(getApplicationContext()).load(imageBlur).intensity(20).Async(true).into(imvfondopaq);
                imvfondopaq.setScaleType(ImageView.ScaleType.FIT_XY);
                txvnombrepaq.setText(nombrepaq);
                txvinfopaq.setText(domiciliopaq+"\n"+telpaq+"\n"+mailpaq+"\n");
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    void consulta_paqxorg(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos/getPaquetesOrganizador_detalle.php?idorganizador="+idorgpaq; //Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            nombrepaqxorg= new ArrayList<>();
                            imagpaqxorg= new ArrayList<>();
                            preciopaqxorg= new ArrayList<>();
                            ideventopack= new ArrayList<>();
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                nombrepaqxorg.add(datos.getString("nombre"));
                                preciopaqxorg.add(Double.parseDouble(datos.getString("precio")));
                                ideventopack.add(datos.getString("IdEventoPack"));
                                imagpaqxorg.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                            }
                            pinta_paqxorg();
                            cerrar_cargando();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error en la consulta: \n"+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void pinta_paqxorg(){
        imvpaqxorg= new ArrayList<>();
        txvnombrepaqxorg= new ArrayList<>();
        txvpreciopaqxorg= new ArrayList<>();
        btverpaq= new ArrayList<>();
        btcomprarpack= new ArrayList<>();
        TableRow.LayoutParams lp= new TableRow.LayoutParams(ancho/4, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        lp.setMargins(5,8,5,8);
        TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        for(int i=0; i<nombrepaqxorg.size();i++){
            rowpaqxorg= new TableRow(getApplicationContext());
            rowpaqxorg.setLayoutParams(lp2);

            imvpaqxorg.add(new ImageView(getApplicationContext()));
            imvpaqxorg.get(i).setAdjustViewBounds(true);
            Picasso.get().load(imagpaqxorg.get(i)).error(R.drawable.imgmberror).into(imvpaqxorg.get(i));
            imvpaqxorg.get(i).setBackgroundColor(Color.TRANSPARENT);
            imvpaqxorg.get(i).setScaleType(ImageView.ScaleType.FIT_CENTER);
            imvpaqxorg.get(i).setTag(i);
            imvpaqxorg.get(i).setId(i);
            imvpaqxorg.get(i).setPadding(5,5,5,5);
            imvpaqxorg.get(i).setLayoutParams(lp);

            rowpaqxorg.addView(imvpaqxorg.get(i));

            txvnombrepaqxorg.add(new TextView(this));
            txvnombrepaqxorg.get(i).setText(nombrepaqxorg.get(i));
            txvnombrepaqxorg.get(i).setGravity(Gravity.CENTER);
            txvnombrepaqxorg.get(i).setTextColor(Color.BLACK);
            txvnombrepaqxorg.get(i).setLayoutParams(lp);
            rowpaqxorg.addView(txvnombrepaqxorg.get(i));

            txvpreciopaqxorg.add(new TextView(this));
            txvpreciopaqxorg.get(i).setText("$"+df.format(preciopaqxorg.get(i)));
            txvpreciopaqxorg.get(i).setTextColor(Color.BLACK);
            txvpreciopaqxorg.get(i).setGravity(Gravity.CENTER);
            txvpreciopaqxorg.get(i).setLayoutParams(lp);
            rowpaqxorg.addView(txvpreciopaqxorg.get(i));

            llbotones= new LinearLayout(this);
            llbotones.setOrientation(LinearLayout.VERTICAL);
            llbotones.setLayoutParams(lp);
            llbotones.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp3= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
            lp3.setMargins(0,5,0,5);
            btverpaq.add(new TextView(this));
            btverpaq.get(i).setText("Ver Eventos");
            btverpaq.get(i).setTextColor(Color.WHITE);
            btverpaq.get(i).setPadding(0,15,0,15);
            btverpaq.get(i).setGravity(Gravity.CENTER);
            btverpaq.get(i).setLayoutParams(lp3);
            btverpaq.get(i).setId(i);
            btverpaq.get(i).setBackgroundResource(R.color.azulmb);
            btverpaq.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    consulta_eventosdepaq(ideventopack.get(view.getId()));
                }
            });
            llbotones.addView(btverpaq.get(i));

            btcomprarpack.add(new TextView(this));
            btcomprarpack.get(i).setText("Comprar...");
            btcomprarpack.get(i).setTextColor(Color.WHITE);
            btcomprarpack.get(i).setLayoutParams(lp3);
            btcomprarpack.get(i).setGravity(Gravity.CENTER);
            btcomprarpack.get(i).setPadding(0,15,0,15);
            btcomprarpack.get(i).setBackgroundResource(R.color.verdemb);
            btcomprarpack.get(i).setId(i);
            btcomprarpack.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    set_DatosCompra("eventogrupo","0");
                    set_DatosCompra("idevento","0");
                    set_DatosCompra("ideventopack",ideventopack.get(view.getId()));
                    Intent mainIntent = new Intent().setClass(getApplicationContext(), DetallesEventos.class);
                    startActivity(mainIntent);
                }
            });
            llbotones.addView(btcomprarpack.get(i));

            rowpaqxorg.addView(llbotones);
            tblpaquetes.addView(rowpaqxorg,i);
        }
    }

    void consulta_eventosdepaq(String id){
        iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL="https://www.masboletos.mx/appMasboletos/getImgEventosPack.php?idEventoPack="+id; //Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            infoevtopaq= new ArrayList<>();
                            imgevtopaq= new ArrayList<>();
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                infoevtopaq.add(datos.getString("informacion"));
                                imgevtopaq.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagenes"));
                            }
                            mostrar_eventosxpaq();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getApplicationContext(),"Ha ocurrido un error en la consulta: \n"+error.toString(),Toast.LENGTH_SHORT).show();
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void mostrar_eventosxpaq(){
        // con este tema personalizado evitamos los bordes por defecto
        cdevtopaq = new Dialog(this);
        //deshabilitamos el título por defecto
        cdevtopaq.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        cdevtopaq.setCancelable(false);
        //establecemos el contenido de nuestro dialog
        cdevtopaq.setContentView(R.layout.eventospaquete);
        llevtospaq=cdevtopaq.findViewById(R.id.llevtopaq);

        imvevtopaq= new ArrayList<>();
        txvevtopaq= new ArrayList<>();
        separadores= new ArrayList<>();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        lp.setMargins(0,10,0,5);
        for (int i=0;i<imgevtopaq.size();i++){
            imvevtopaq.add(new ImageView(this));
            imvevtopaq.get(i).setLayoutParams(lp);
            imvevtopaq.get(i).setAdjustViewBounds(true);
            imvevtopaq.get(i).setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(imgevtopaq.get(i)).error(R.drawable.imgmberror).into(imvevtopaq.get(i)); //Log.e("img",imgevtopaq.get(i));
            imvevtopaq.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
            llevtospaq.addView(imvevtopaq.get(i));


            txvevtopaq.add(new TextView(this));
            txvevtopaq.get(i).setText(infoevtopaq.get(i));
            txvevtopaq.get(i).setTextSize(15);
            txvevtopaq.get(i).setTextColor(Color.BLACK);
            txvevtopaq.get(i).setLayoutParams(lp);
            txvevtopaq.get(i).setGravity(Gravity.CENTER);
            txvevtopaq.get(i).setPadding(0,5,0,5);
            llevtospaq.addView(txvevtopaq.get(i));

            separadores.add(new View(this));
            separadores.get(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
            separadores.get(i).setBackgroundResource(R.color.gris);
            llevtospaq.addView(separadores.get(i));
        }
        llevtospaq.setGravity(Gravity.CENTER_HORIZONTAL);
        cdevtopaq.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        cdevtopaq.show();
        cerrar_cargando();
    }

    public void cerrar_cdevtopaq(View view){
        cdevtopaq.dismiss();
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

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    public void regresar(View view){
        finish();
    }
}
