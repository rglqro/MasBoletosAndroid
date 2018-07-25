package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
    String[] spTitFP,spDescFP,idtipopagom,ptajecargo,itefijo,listatipopagos,listadescpago,listaptecar,listaitecar,listaidpago,listatipopagos2;
    String datoscargos;
    int[] spImagesFP,listaimagpagos;
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
        ConsultaFormasPago();
        return vista;
    }

    void ConsultaFormasPago(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getFormaPagoFormaEntrega.php?idevento="+idevento);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                cant_datos=0;
                                Elementos = new JSONArray(resultado);
                                idtipopagom= new String[Elementos.length()];
                                ptajecargo= new String[Elementos.length()];
                                itefijo= new String[Elementos.length()];
                                String tipo="";
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    tipo=datos.getString("Tipo");
                                    if (tipo.equals("FormaPago")) {
                                        idtipopagom[i] = datos.getString("IdTipoPago");
                                        ptajecargo[i] = datos.getString("porcentajecargo");
                                        itefijo[i] = datos.getString("ImporteFijo");
                                        cant_datos++;
                                    }else if (tipo.equals("FormaEntrega")) {
                                        idtipopagom[i] = "0";
                                        ptajecargo[i] = "0";
                                        itefijo[i] = "0";
                                    }
                                }
                                LlenadoLista();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void LlenadoLista(){
        spImagesFP= new int[]{R.drawable.puntoventa,R.drawable.mcpago,R.drawable.mcpago,R.drawable.oxxopago,R.drawable.pppago};
        spTitFP= new String[]{"Pago en punto de venta","Tarjeta de Crédito ", "Tarjeta de Débito","Oxxo ","PayPal"};
        spDescFP= new String[]{"Paga y recoje en punto de venta","Compra con tarjeta de crédito...","Compra con tarjeta de débito...","Paga en efectivo con oxxo","Compra con PayPal"};
        listatipopagos= new String[cant_datos];listatipopagos2= new String[cant_datos];
        listadescpago= new String[cant_datos];
        listaimagpagos= new int[cant_datos];
        listaptecar= new String[cant_datos];
        listaitecar= new String[cant_datos];
        listaidpago= new String[cant_datos];
        int cont=0;
        for(int i=0;i<idtipopagom.length;i++){
            for (int j=1;j<=5;j++){
                if(idtipopagom[i].equals(String.valueOf(j))){
                    if(itefijo[i].equals("null")) {itefijo[i]="0.00";}
                    listatipopagos[cont]=spTitFP[j-1]+" + % Cargo "+ptajecargo[i]+" + Cargo $"+itefijo[i];
                    listatipopagos2[cont]=spTitFP[j-1];
                    listadescpago[cont]=spDescFP[j-1];
                    listaptecar[cont]=ptajecargo[i];
                    listaitecar[cont]=itefijo[i];
                    listaidpago[cont]=idtipopagom[i];
                    listaimagpagos[cont]=spImagesFP[j-1]; cont++;
                }
            }
        }
        SpinnerAdater adapter= new SpinnerAdater(getActivity(),listatipopagos,listaimagpagos,listadescpago);
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
                    datoscargos = listaidpago[pos] + ",";
                    datoscargos += listaptecar[pos] + ",";
                    datoscargos += listaitecar[pos];
                    Log.e("datoscargos", datoscargos);
                    set_DatosCompra("datoscargos", datoscargos);
                    set_DatosCompra("formapago", listatipopagos2[pos]);
                    set_DatosCompra("idformapago", listaidpago[pos]);
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

    public String inserta(String enlace){ // metodo que inserta los parametros en la BD
        URL url = null;
        Log.e("Enlace",enlace);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
