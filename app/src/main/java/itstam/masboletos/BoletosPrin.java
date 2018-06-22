package itstam.masboletos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoletosPrin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoletosPrin extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static ViewPager mPager;

    Activity activity=getActivity();
    int currentPage = 0;

    ArrayList<ImageButton> ImBotonEvento;
    ArrayList<String> ListaImagCarrusel,ListaImagBoton;
    Spinner spcategorias, sporganizadores;
    Handler handler;
    Runnable Update;
    TableLayout tabla_imagenes;
    View vista;
    TableRow row;
    private SwipeRefreshLayout swipeContainer;

    public BoletosPrin() {
        // Required empty public constructor
        handler = new Handler();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BoletosPrin.
     */
    // TODO: Rename and change types and number of parameters
    public static BoletosPrin newInstance(String param1, String param2) {
        BoletosPrin fragment = new BoletosPrin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_boletos_prin, container, false);
        swipeContainer = (SwipeRefreshLayout) vista.findViewById(R.id.SWRLY);
        tabla_imagenes = (TableLayout) vista.findViewById(R.id.tabla_imagenes);
        Consulta_Imagen_Botones(vista);
        swipeContainer.setOnRefreshListener(this);
        return vista;
    }


    void consulta_Imagenes_Carrusel(final View vista){
        /*Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getImgCarrusel.php");  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            JSONArray Elementos = null;
                            try {
                                Elementos = new JSONArray(resultado);
                                ListaImagCarrusel = new ArrayList<String>();
                                ListaImagCarrusel.clear();
                                Log.d("Total de Imagenes",String.valueOf(Elementos.length()));
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    ListaImagCarrusel.add("http://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                                }
                                iniciar_listas_spinner(vista);
                                iniciar_Carrusel2(vista);
                                Consulta_Imagen_Botones(vista);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });  //permite trabajar con la interfaz grafica
            }
        };
        tr.start();*/
    }

    void Consulta_Imagen_Botones(final View vista){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getEventosActivos.php");  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            JSONArray Elementos = null;
                            try {
                                Elementos = new JSONArray(resultado);
                                ListaImagBoton = new ArrayList<String>();
                                ListaImagBoton.clear();
                                ListaImagCarrusel = new ArrayList<String>();
                                ListaImagCarrusel.clear();
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    ListaImagBoton.add("http://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagen"));
                                    ListaImagCarrusel.add("http://www.masboletos.mx/sica/imgEventos/"+datos.getString("imagencarrusel"));
                                }
                                iniciar_listas_spinner(vista);
                                iniciar_Carrusel2(vista);
                                generarBotonesEvento(vista);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });  //permite trabajar con la interfaz grafica
            }
        };
        tr.start();
    }

    void generarBotonesEvento(View vista){
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
                ImBotonEvento.get(i).setLayoutParams(lp2);
                Picasso.with(getActivity())
                        .load(ListaImagBoton.get(pos_arr_ima))
                        .error(R.id.action_inicio)
                        .into(ImBotonEvento.get(pos_arr_ima));
                ImBotonEvento.get(pos_arr_ima).setBackgroundColor(Color.TRANSPARENT);
                ImBotonEvento.get(pos_arr_ima).setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImBotonEvento.get(pos_arr_ima).setAdjustViewBounds(true);
                ImBotonEvento.get(pos_arr_ima).setTag(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setId(pos_arr_ima);
                ImBotonEvento.get(pos_arr_ima).setPadding(10,10,10,10);
                ImBotonEvento.get(pos_arr_ima).setAdjustViewBounds(true);
                ImBotonEvento.get(pos_arr_ima).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mainIntent = new Intent().setClass(
                                getActivity(), DetallesEventos.class);
                        mainIntent.putExtra("indiceimagen",ListaImagBoton.get(v.getId()).toString());
                        startActivity(mainIntent);
                    }
                });
                row.addView(ImBotonEvento.get(pos_arr_ima));
                pos_arr_ima++;
            }
            tabla_imagenes.setColumnShrinkable(j, true);
            tabla_imagenes.addView(row,j);
        }
        Log.d("Total Botones",String.valueOf(ImBotonEvento.size()));
    }

    void iniciar_listas_spinner(View vista){
        sporganizadores=(Spinner) vista.findViewById(R.id.sp_organizadores);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.organizadores, R.layout.spinner_item_2);
        adapter2.setDropDownViewResource(R.layout.spinner_lista2);
        sporganizadores.setAdapter(adapter2);

        spcategorias=(Spinner) vista.findViewById(R.id.spcategorias);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cat_Evento, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_lista);
        spcategorias.setAdapter(adapter);
    }

    void iniciar_Carrusel2(View vista){
        mPager = (ViewPager) vista.findViewById(R.id.pager);
        activity=getActivity();
        mPager.setAdapter(new MyAdapter(getActivity(),ListaImagCarrusel,ListaImagBoton));
        CircleIndicator indicator = (CircleIndicator) vista.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        // Auto start of viewpager
        Update = new Runnable() {
            public void run() {
                if (currentPage == ListaImagCarrusel.size()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);
    }

    public String inserta(String enlace){ // metodo que inserta los parametros en la BD

        URL url = null;
        int respuesta = 0;
        String linea = "",valor="";
        StringBuilder resul = null;
        try {
            url = new URL(enlace);
            HttpURLConnection conection;
            conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((linea = reader.readLine()) != null) {
                    resul.append(linea);
                }
            }
            if(resul!=null) {
                valor = resul.toString();
            }
        } catch (Exception e) {
            //resul.append("Error ----");
        }
        Log.d("Resultado pagina",valor);
        return valor;
    }

    public int validadatos(String response){
        int respuesta = 0;
        if (response.length()>0){
            respuesta=1;
        }
        return respuesta;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        mListener = null;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(Update);
                handler = new Handler();
                ImBotonEvento.clear();
                tabla_imagenes.removeAllViews();
                Consulta_Imagen_Botones(vista);
                swipeContainer.setRefreshing(false);
            }
        }, 3000);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(Update);
        Log.d("Destroy","Destroy");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        handler.removeCallbacks(Update);
        Log.d("Stop","Stop");
        super.onStop();
    }

    @Override
    public void onPause() {
        //handler.removeCallbacks(Update);
        Log.d("Pause","Pause");
        super.onPause();
    }



}
