package itstam.masboletos;

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
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import me.relex.circleindicator.CircleIndicator;

public class BoletosPrin extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

    private OnFragmentInteractionListener mListener;

    private static HeightWrappingViewPager mPager;

    Activity activity=getActivity();
    int currentPage = 0;

    JSONArray Elementos = null;
    ArrayList<ImageButton> ImBotonEvento;
    ImageButton[] BtsOrganizadores;
    String[]ListaImagOrg;
    ArrayList<String> ListaImagCarrusel,ListaImagBoton,IDEventos,NombresEvento,EventosGrupo;
    Spinner spcategorias;
    Handler handler;
    Runnable Update;
    TableLayout tabla_imagenes;
    View vista;
    TableRow row;
    Timer swipeTimer;
    LinearLayout LLImagOrg;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_boletos_prin, container, false);
        swipeContainer = (SwipeRefreshLayout) vista.findViewById(R.id.SWRLY);
        tabla_imagenes = (TableLayout) vista.findViewById(R.id.tabla_imagenes);
        LLImagOrg=(LinearLayout)vista.findViewById(R.id.LLImagOrg);

        Consulta_Imagen_Botones();

        swipeContainer.setOnRefreshListener(this);
        return vista;
    }

    void Consulta_Imagen_Botones(){
        ((MainActivity)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="https://www.masboletos.mx/appMasboletos/getEventosActivos.php"; Log.e("Enlace", URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            ListaImagBoton = new ArrayList<String>();
                            ListaImagBoton.clear();
                            ListaImagCarrusel = new ArrayList<String>();
                            ListaImagCarrusel.clear();
                            NombresEvento= new ArrayList<String>();
                            NombresEvento.clear();
                            IDEventos= new ArrayList<String>();
                            IDEventos.clear();
                            EventosGrupo= new ArrayList<String>();
                            EventosGrupo.clear();
                            for (int i=0;i<response.length();i++){
                                JSONObject datos = response.getJSONObject(i);
                                ListaImagBoton.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                                ListaImagCarrusel.add("https://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagencarrusel"));
                                NombresEvento.add(datos.getString("evento"));
                                IDEventos.add(datos.getString("idevento"));
                                EventosGrupo.add(datos.getString("eventogrupo"));
                            }
                            iniciar_listas_spinner();
                            iniciar_Carrusel2();
                            generarBotonesEvento();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
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
        Log.d("Tamaño ima boton",String.valueOf(Tam_ListaImEve));
        for (int j=0;j<Tam_ListaImEve/2;j++){
            row = new TableRow(getActivity());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams lp2 = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            for (int i=0;i<2;i++){
                if(pos_arr_ima==ListaImagBoton.size()) break;
                ImBotonEvento.add(new ImageButton(getActivity()));
                ImBotonEvento.get(pos_arr_ima).setLayoutParams(lp2);
                ImBotonEvento.get(pos_arr_ima).setImageResource(R.drawable.mbiconor);
                ImBotonEvento.get(pos_arr_ima).setBackgroundColor(Color.TRANSPARENT);
                ImBotonEvento.get(pos_arr_ima).setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImBotonEvento.get(pos_arr_ima).setTag(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setId(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setPadding(5,5,5,5);
                ImBotonEvento.get(pos_arr_ima).setAdjustViewBounds(true);
                Picasso.get().load(ListaImagBoton.get(pos_arr_ima)).error(R.drawable.mbiconor).into(ImBotonEvento.get(pos_arr_ima));
                ImBotonEvento.get(pos_arr_ima).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainIntent = new Intent().setClass(
                                getActivity(), DetallesEventos.class);
                        mainIntent.putExtra("indiceimagen",ListaImagBoton.get(v.getId()).toString());
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
        Log.d("Total Botones",String.valueOf(ImBotonEvento.size()));
        Consulta_Imagen_Organizadores();
    }

    void iniciar_listas_spinner(){

        spcategorias=(Spinner) vista.findViewById(R.id.spcategorias);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cat_Evento, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_lista);
        spcategorias.setAdapter(adapter);
    }

    void Consulta_Imagen_Organizadores(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="https://www.masboletos.mx/appMasboletos/getPatrocinadores.php"; Log.e("Enlace", URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos=response;
                            ListaImagOrg=new String[Elementos.length()];
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                ListaImagOrg[i]= "https://www.masboletos.mx/sica/imgEventos/"+datos.getString("banner");
                            }
                            genera_Imag_Orga();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    @SuppressLint("ResourceAsColor")
    void genera_Imag_Orga(){
        ((MainActivity)getActivity()).cerrar_cargando();
        int tam_lista=ListaImagOrg.length;
        BtsOrganizadores = new ImageButton[tam_lista];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(5,dpToPx(5),5,dpToPx(5));
        for(int i=0;i<BtsOrganizadores.length;i++){
            BtsOrganizadores[i]=new ImageButton(getActivity());
            BtsOrganizadores[i].setLayoutParams(lp);
            BtsOrganizadores[i].setBackgroundColor(Color.TRANSPARENT);
            BtsOrganizadores[i].setAdjustViewBounds(true);
            BtsOrganizadores[i].setScaleType(ImageView.ScaleType.FIT_XY);
            BtsOrganizadores[i].setPadding(15,0,15,0);
            Picasso.get().load(ListaImagOrg[i]).error(R.drawable.mbiconor).into(BtsOrganizadores[i]);
            LLImagOrg.addView(BtsOrganizadores[i]);
        }
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

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        handler.removeCallbacks(Update);
        super.onDetach();
        if(swipeTimer != null){
            swipeTimer.cancel();
            //cancel timer task and assign null
        }
        mListener = null;
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
                }
                    Consulta_Imagen_Botones();
                    swipeContainer.setRefreshing(false);

            }
        }, 3000);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(Update);
        if(swipeTimer != null){
            swipeTimer.cancel();
            //cancel timer task and assign null
        }
        Log.d("Destroy BP","Destroy");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(Update);
        if(swipeTimer != null){
            swipeTimer.cancel();
            swipeTimer= new Timer();
            //cancel timer task and assign null
        }
        Log.d("Stop BP","Stop");
        super.onStop();
    }

    @Override
    public void onPause() {
        //handler.removeCallbacks(Update);
        Log.d("Pause BP","Pause");
        super.onPause();
    }


}
