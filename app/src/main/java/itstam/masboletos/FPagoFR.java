package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FPagoFR extends Fragment {
    View vista;
    String datoscargos;
    ArrayList<String>idtipopagom,ptajecargo,itefijo,spTitFP,spDescFP,spTitFP2,spDescFP2;
    ArrayList<Integer>spImagesFP,spImagesFP2;
    ListView lvfpago;
    int cant_datos=0,idsel=0,pos=0;
    Button btContinuar4;
    JSONArray Elementos;
    SharedPreferences prefe;
    String idevento;

    public FPagoFR() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_fpago_fr, container, false);
        lvfpago=(ListView)vista.findViewById(R.id.lvfpago);
        btContinuar4=(Button)vista.findViewById(R.id.btContinuar4);
        btContinuar4.setBackgroundResource(R.color.grisclaro);
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=prefe.getString("idevento","");
        spImagesFP= new ArrayList<Integer>();
        spImagesFP.add(R.drawable.puntoventa); spImagesFP.add(R.drawable.mcpago); spImagesFP.add(R.drawable.mcpago); spImagesFP.add(R.drawable.oxxopago); spImagesFP.add(R.drawable.pppago);
        spTitFP= new ArrayList<String>();
        spTitFP.add("Pago en punto de venta"); spTitFP.add("Tarjeta de Crédito "); spTitFP.add("Tarjeta de Débito"); spTitFP.add("Oxxo "); spTitFP.add("PayPal");
        spDescFP= new ArrayList<String>();
        spDescFP.add("Paga y recoje en punto de venta"); spDescFP.add("Compra con tarjeta de crédito..."); spDescFP.add("Compra con tarjeta de débito..."); spDescFP.add("Paga en efectivo con oxxo"); spDescFP.add("Compra con PayPal");
        ConsultaFormasPago();
        return vista;
    }

    void ConsultaFormasPago(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="http://www.masboletos.mx/appMasboletos/getFormaPagoFormaEntrega.php?idevento="+idevento;
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            cant_datos=0;
                            Elementos = response;
                            spImagesFP2= new ArrayList<Integer>();
                            spTitFP2= new ArrayList<String>();
                            spDescFP2= new ArrayList<String>();
                            idtipopagom= new ArrayList<String>();
                            ptajecargo= new ArrayList<String>();
                            itefijo= new ArrayList<String>();
                            String tipo="",cargopte,cargofijo;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                tipo=datos.getString("Tipo");
                                if (tipo.equals("FormaPago")) {
                                    cargopte=datos.getString("porcentajecargo"); cargofijo=datos.getString("ImporteFijo");
                                    if (cargopte.equals("null")){ cargopte="0.00";} if (cargofijo.equals("null")){cargofijo="0.00";}
                                    idtipopagom.add( datos.getString("IdTipoPago"));
                                    ptajecargo.add(cargopte);
                                    itefijo.add(cargofijo);
                                    spTitFP2.add(datos.getString("texto")+" - % Cargo "+cargopte+" + Cargo $"+cargofijo);
                                    spDescFP2.add(spDescFP.get(Integer.parseInt(datos.getString("IdTipoPago"))-1));
                                    spImagesFP2.add(spImagesFP.get(Integer.parseInt(datos.getString("IdTipoPago"))-1));
                                    cant_datos++;
                                }
                            }
                            LlenadoLista();
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
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void LlenadoLista(){
        SpinnerAdater adapter= new SpinnerAdater(getActivity(),spTitFP2,spImagesFP2,spDescFP2);
        lvfpago.setAdapter(null);
        lvfpago.setAdapter(adapter);
        ((DetallesEventos)getActivity()).cerrar_cargando();
        lvfpago.setNestedScrollingEnabled(true);
        idsel=0;
        lvfpago.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                view.setSelected(true);
                btContinuar4.setBackgroundResource(R.color.verdemb);
                pos=position;
                idsel++;
            }
        });
        btContinuar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idsel!=0) {
                    datoscargos = idtipopagom.get(pos) + ",";
                    datoscargos += ptajecargo.get(pos) + ",";
                    datoscargos += itefijo.get(pos);
                    Log.e("datoscargos", datoscargos);
                    String [] fpago= spTitFP2.get(pos).split("-");
                    set_DatosCompra("datoscargos", datoscargos);
                    set_DatosCompra("formapago", fpago[0]);
                    set_DatosCompra("idformapago", idtipopagom.get(pos));
                    FEntregaFr fEntregaFr = new FEntregaFr();
                    ((DetallesEventos) getActivity()).replaceFragment(fEntregaFr);
                }else {
                    ((DetallesEventos) getActivity()).AlertaBoton("Forma de Pago","Debe elegir al menos una forma de pago").show();
                }
            }
        });
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
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

}
