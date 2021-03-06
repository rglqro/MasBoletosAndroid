package itstam.masboletos.principal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
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
import java.util.Timer;
import java.util.TimerTask;

import itstam.masboletos.HeightWrappingViewPager;
import itstam.masboletos.MyAdapter;
import itstam.masboletos.R;
import itstam.masboletos.carruselcompra.DetallesEventos;
import me.relex.circleindicator.CircleIndicator;

public class BoletosPrin extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    private static HeightWrappingViewPager mPager;

    Activity activity=getActivity();
    int currentPage = 0,alto,ancho;

    JSONArray Elementos = null;
    ArrayList<ImageButton> ImBotonEvento;
    ArrayList<ImageView> ImPaquete; ArrayList<View> lineasep;
    ArrayList<TextView> txvPaquete,btpaquete;
    ImageButton[] BtsOrganizadores;
    String[]ListaImagOrg;
    SharedPreferences prefe_user;
    ArrayList<String> ListaImagCarrusel,ListaImagBoton,IDEventos,NombresEvento,EventosGrupo,listaidorgpaq,listanombrepaq,listaimapaq,listadireccionpaq;
    Handler handler;
    Runnable Update;
    TableLayout tabla_imagenes;
    View vista;
    TableRow row;
    Timer swipeTimer;
    LinearLayout LLImagOrg,llpaquetes,llinfopaquetes;
    HorizontalScrollView scvorg;
    private SwipeRefreshLayout swipeContainer;
    TabHost thboletopaq;
    String nombreuser; Boolean validasesion=false;
    ImageView imvlogoarriba;
    ImageButton imbtcompartirapp;
    CountDownTimer cdtcerrarcarg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_boletos_prin, container, false);
        swipeContainer = (SwipeRefreshLayout) vista.findViewById(R.id.SWRLY);
        tabla_imagenes = (TableLayout) vista.findViewById(R.id.tabla_imagenes);
        LLImagOrg=(LinearLayout)vista.findViewById(R.id.LLImagOrg);
        thboletopaq=(TabHost)vista.findViewById(R.id.thboletopaq);
        llpaquetes=(LinearLayout)vista.findViewById(R.id.llpaquetes);
        imvlogoarriba=vista.findViewById(R.id.imvlogoarriba);
        scvorg=vista.findViewById(R.id.SCVOrg);
        imbtcompartirapp=vista.findViewById(R.id.imbtcompartirapp);

        prefe_user=getActivity().getSharedPreferences("datos_sesion",Context.MODE_PRIVATE);
        validasesion=prefe_user.getBoolean("validasesion",false);

        imbtcompartirapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent_compartir();
            }
        });

        iniciar_tabhost();

        swipeContainer.setOnRefreshListener(this);
        return vista;
    }

    void iniciar_tabhost(){
        thboletopaq.setup();
        TabHost.TabSpec tab1 = thboletopaq.newTabSpec("thboletos");  //aspectos de cada Tab (pestaña)
        TabHost.TabSpec tab2 = thboletopaq.newTabSpec("thpaquetes");

        tab1.setIndicator("Compra Hoy");    //qué queremos que aparezca en las pestañas
        tab1.setContent(R.id.llboletos); //definimos el id de cada Tab (pestaña)

        tab2.setIndicator("Venta de Paquetes");
        tab2.setContent(R.id.llpaquetes);

        thboletopaq.addTab(tab1); //añadimos los tabs ya programados
        thboletopaq.addTab(tab2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;

        imvlogoarriba.getLayoutParams().height=alto/10;

        consulta_info("https://www.masboletos.mx/appMasboletos/getEventosActivos.php","eventos");

        if (cdtcerrarcarg!=null)
            cdtcerrarcarg.cancel();
        cdtcerrarcarg = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.e("tiempo",String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                ((MainActivity)getActivity()).cerrar_cargando();
            }
        }.start();
    }

    void consulta_info(String liga, final String consulta){
        if(consulta.equalsIgnoreCase("eventos"))
            ((MainActivity)getActivity()).iniciar_cargando();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL=liga;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            if(consulta.equals("eventos")) {
                                ListaImagBoton = new ArrayList<String>();
                                ListaImagCarrusel = new ArrayList<String>();
                                NombresEvento = new ArrayList<String>();
                                IDEventos = new ArrayList<String>();
                                EventosGrupo = new ArrayList<String>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject datos = response.getJSONObject(i);
                                    ListaImagBoton.add("https://www.masboletos.mx/sica/imgEventos/" + datos.getString("imagen"));
                                    ListaImagCarrusel.add("https://www.masboletos.mx/sica/imgEventos/" + datos.getString("imagencarrusel"));
                                    NombresEvento.add(datos.getString("evento"));
                                    IDEventos.add(datos.getString("idevento"));
                                    EventosGrupo.add(datos.getString("eventogrupo"));
                                }
                                //iniciar_Carrusel2();
                                generarBotonesEvento();
                            }else if(consulta.equals("imgorganizadores")){
                                Elementos=response;
                                ListaImagOrg=new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    ListaImagOrg[i]= "https://www.masboletos.mx/sica/imgEventos/"+datos.getString("banner");
                                }
                                genera_Imag_Orga();
                            }
                            else if(consulta.equals("paquetes")){
                                Elementos=response;
                                listaimapaq=new ArrayList<String>();
                                listanombrepaq=new ArrayList<String>();
                                listadireccionpaq=new ArrayList<String>();
                                listaidorgpaq=new ArrayList<String>();
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    listaimapaq.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("banner"));
                                    listanombrepaq.add(datos.getString("nombre"));
                                    listadireccionpaq.add(datos.getString("domicilio"));
                                    listaidorgpaq.add(datos.getString("idorganizador"));
                                }
                                if(Elementos.length()>0)
                                    genera_datos_paquetes();
                                ((MainActivity)getActivity()).cerrar_cargando();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ((MainActivity)getActivity()).cerrar_cargando();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Snackbar.make(vista,"Ha ocurrido un error, recargue la ventana o intente más tarde",Snackbar.LENGTH_LONG).show();
                        ((MainActivity)getActivity()).cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void generarBotonesEvento(){
        int Tam_ListaImEve=ListaImagBoton.size();
        if(ListaImagBoton.size()%2!=0){
            Tam_ListaImEve++;}
        ImBotonEvento= new ArrayList<ImageButton>();
        ImBotonEvento.clear();
        int pos_arr_ima=0;
        //Log.e("Tamaño ima boton",String.valueOf(Tam_ListaImEve));
        for (int j=0;j<Tam_ListaImEve/2;j++){
            row = new TableRow(getActivity());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,1);
            TableRow.LayoutParams lp2 = new TableRow.LayoutParams(ancho/2, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            for (int i=0;i<2;i++){
                if(pos_arr_ima==ListaImagBoton.size()) break;
                ImBotonEvento.add(new ImageButton(getActivity()));
                ImBotonEvento.get(pos_arr_ima).setLayoutParams(lp2);
                ImBotonEvento.get(pos_arr_ima).setImageResource(R.drawable.imgmberror);
                ImBotonEvento.get(pos_arr_ima).setBackgroundColor(Color.TRANSPARENT);
                ImBotonEvento.get(pos_arr_ima).setScaleType(ImageView.ScaleType.FIT_XY);
                ImBotonEvento.get(pos_arr_ima).setTag(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setId(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setPadding(5,5,5,5);
                ImBotonEvento.get(pos_arr_ima).setAdjustViewBounds(true);
                //new DownloadImageTask(ImBotonEvento.get(pos_arr_ima),getActivity()).execute(ListaImagBoton.get(pos_arr_ima));
                Picasso.get().load(ListaImagBoton.get(pos_arr_ima)).error(R.drawable.imgmberror).into(ImBotonEvento.get(pos_arr_ima)); //Log.e("foto",ListaImagBoton.get(pos_arr_ima));
                ImBotonEvento.get(pos_arr_ima).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainIntent = new Intent().setClass(getActivity(), DetallesEventos.class);
                        set_DatosCompra("indiceimagen",ListaImagBoton.get(v.getId()).toString());
                        set_DatosCompra("idevento",IDEventos.get(v.getId()).toString());
                        set_DatosCompra("NombreEvento",NombresEvento.get(v.getId()).toString());
                        set_DatosCompra("eventogrupo",EventosGrupo.get(v.getId()).toString());
                        startActivity(mainIntent);
                    }
                });
                ImBotonEvento.get(pos_arr_ima).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(getActivity(),NombresEvento.get(v.getId()).toString(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                row.addView(ImBotonEvento.get(pos_arr_ima));
                pos_arr_ima++;
            }
            tabla_imagenes.setColumnShrinkable(j, true);
            tabla_imagenes.addView(row,j);
        }
        tabla_imagenes.setStretchAllColumns(true);
        //Log.e("Total Botones",String.valueOf(ImBotonEvento.size()));
        consulta_info("https://www.masboletos.mx/appMasboletos/getPatrocinadores.php","imgorganizadores");
    }

    void iniciar_Carrusel2(){
        currentPage=0;
        mPager = (HeightWrappingViewPager) vista.findViewById(R.id.pager);
        activity=getActivity();
        mPager.setAdapter(new MyAdapter(getActivity(),ListaImagCarrusel,IDEventos,NombresEvento,EventosGrupo,ListaImagBoton));
        CircleIndicator indicator = (CircleIndicator) vista.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        // Auto start of viewpager
        handler= new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == ListaImagCarrusel.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    @SuppressLint("ResourceAsColor")
    void genera_Imag_Orga(){
        int tam_lista=ListaImagOrg.length;
        scvorg.getLayoutParams().height=alto/9;
        BtsOrganizadores = new ImageButton[tam_lista];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(15,0,5,0);
        for(int i=0;i<BtsOrganizadores.length;i++){
            BtsOrganizadores[i]=new ImageButton(getActivity());
            BtsOrganizadores[i].setLayoutParams(lp);
            BtsOrganizadores[i].setBackgroundColor(Color.TRANSPARENT);
            BtsOrganizadores[i].setScaleType(ImageView.ScaleType.FIT_XY);
            //BtsOrganizadores[i].setPadding(0,0,15,0);
            Picasso.get().load(ListaImagOrg[i]).error(R.drawable.mbiconor).into(BtsOrganizadores[i]);
            BtsOrganizadores[i].setAdjustViewBounds(true);
            LLImagOrg.addView(BtsOrganizadores[i]);
        }
        consulta_info("https://www.masboletos.mx/appMasboletos/getPaquetesOrganizador.php","paquetes");
    }

    void genera_datos_paquetes(){
        if(llpaquetes!=null)
            llpaquetes.removeAllViews();
        ImPaquete= new ArrayList<ImageView>();
        txvPaquete= new ArrayList<TextView>();
        btpaquete= new ArrayList<TextView>();
        lineasep= new ArrayList<View>();
        for(int i=0;i<listaidorgpaq.size();i++){
            llinfopaquetes= new LinearLayout(getActivity());
            llinfopaquetes.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams lpinfo= new LinearLayout.LayoutParams(ancho/3, ViewGroup.LayoutParams.WRAP_CONTENT,1);
            lpinfo.setMargins(5,2,5,2);

            ImPaquete.add(new ImageButton(getActivity()));
            ImPaquete.get(i).setLayoutParams(lpinfo);
            Picasso.get().load(listaimapaq.get(i)).error(R.mipmap.logo_masboletos).into(ImPaquete.get(i)); //Log.e("foto",listaimapaq.get(i));
            ImPaquete.get(i).setBackgroundResource(R.color.grismasclaro);
            ImPaquete.get(i).setScaleType(ImageView.ScaleType.CENTER);
            ImPaquete.get(i).setTag(i);
            ImPaquete.get(i).setId(i);
            ImPaquete.get(i).setPadding(5,5,5,5);
            ImPaquete.get(i).setAdjustViewBounds(true);
            llinfopaquetes.addView(ImPaquete.get(i));

            txvPaquete.add(new TextView(getActivity()));
            txvPaquete.get(i).setText(listanombrepaq.get(i));
            txvPaquete.get(i).setLayoutParams(lpinfo);
            txvPaquete.get(i).setTextColor(Color.BLACK);
            llinfopaquetes.addView(txvPaquete.get(i));

            btpaquete.add(new Button(getActivity()));
            btpaquete.get(i).setText("Ver Paquetes");
            btpaquete.get(i).setLayoutParams(lpinfo);
            btpaquete.get(i).setId(i);
            btpaquete.get(i).setBackgroundResource(R.color.verdemb);
            btpaquete.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainIntent = new Intent().setClass(getActivity(), PaquetesAct.class);
                    set_DatosCompra("idorgpaq",listaidorgpaq.get(view.getId()));
                    startActivity(mainIntent);
                }
            });
            llinfopaquetes.addView(btpaquete.get(i));

            lineasep.add(new View(getActivity()));
            lineasep.get(i).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            lineasep.get(i).setBackgroundResource(R.color.grisclaro);
            llpaquetes.addView(llinfopaquetes);
            llpaquetes.addView(lineasep.get(i));
        }
        ((MainActivity)getActivity()).cerrar_cargando();
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    public void intent_compartir(){
        Intent compartir = new Intent(Intent.ACTION_SEND);
        compartir.setType("text/plain");
        String mensaje = "Descarga la app de MasBoletos para Android"+"\nVisita el siguiente enlace: https://play.google.com/store/apps/details?id=itstam.masboletos";
        compartir.putExtra(Intent.EXTRA_SUBJECT, "MasBoletos APP");
        compartir.putExtra(Intent.EXTRA_TEXT, mensaje);
        startActivity(Intent.createChooser(compartir, "Comparte nuestra APP vía"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if (handler!=null) {
            handler.removeCallbacks(Update);
        }
        super.onDetach();
        if(swipeTimer != null){
            swipeTimer.cancel();
            //cancel timer task and assign null
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handler = new Handler();
                if(swipeTimer != null){
                    swipeTimer.cancel();
                    //cancel timer task and assign null
                }
                if (ImBotonEvento!=null) {
                    ImBotonEvento.clear();
                    LLImagOrg.removeAllViews();
                    tabla_imagenes.removeAllViews();
                    //Log.e("tamañolista", String.valueOf(listaimapaq.size()));
                    if(listaimapaq.size()!=0)
                        llpaquetes.removeAllViews();
                }
                    consulta_info("https://www.masboletos.mx/appMasboletos/getEventosActivos.php","eventos");
                    swipeContainer.setRefreshing(false);

            }
        }, 3000);
    }

    @Override
    public void onDestroy() {
        if (handler!=null) {
            handler.removeCallbacks(Update);
        }
        if(swipeTimer != null){
            swipeTimer.cancel();
            //cancel timer task and assign null
        }
        //Log.e("Destroy BP","Destroy");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if (handler!=null) {
            handler.removeCallbacks(Update);
        }
        if(swipeTimer != null){
            swipeTimer.cancel();
            swipeTimer= new Timer();
            //cancel timer task and assign null
        }
        //Log.e("Stop BP","Stop");
        super.onStop();
    }

    @Override
    public void onPause() {
        //handler.removeCallbacks(Update);
        //Log.e("Pause BP","Pause");
        super.onPause();
    }

}
