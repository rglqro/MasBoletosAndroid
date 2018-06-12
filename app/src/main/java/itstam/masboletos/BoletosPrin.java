package itstam.masboletos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.goodiebag.carouselpicker.CarouselPicker;
import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BoletosPrin.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoletosPrin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoletosPrin extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static ViewPager mPager;

    Activity activity;
    private static int currentPage = 0;
    private static final Integer[] EventosImag= {R.drawable.rockshow,R.drawable.mixon,R.drawable.granjazita};
    private ArrayList<Integer> EventosImagArray = new ArrayList<Integer>();

    Spinner spcategorias, sporganizadores;
    TableLayout tablaimagenes;

    public BoletosPrin() {
        // Required empty public constructor
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
        View vista = inflater.inflate(R.layout.fragment_boletos_prin, container, false);
        iniciar_listas_spinner(vista);
        tabla_imas(vista);


        iniciar_carrusel(vista);
        return vista;
    }

    void tabla_imas(View vista){
        tablaimagenes=(TableLayout) vista.findViewById(R.id.tabla_imagenes);
        for (int i = 0; i < 2; i++) {
            tablaimagenes.setColumnShrinkable(i, true);
        }

        tablaimagenes.setPadding(20,20,20,20);
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

    public void pagina(View view){
        Toast.makeText(getActivity(),"Pulsado",Toast.LENGTH_SHORT ).show();
    }

    private void iniciar_carrusel(View vista) {
        for(int i=0;i<EventosImag.length;i++)
            EventosImagArray.add(EventosImag[i]);

        mPager = (ViewPager) vista.findViewById(R.id.pager);
        activity=getActivity();
        mPager.setAdapter(new MyAdapter(activity,EventosImagArray));
        CircleIndicator indicator = (CircleIndicator) vista.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == EventosImag.length) {
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
        }, 1500, 3000);
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
        super.onDetach();
        mListener = null;
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
}
