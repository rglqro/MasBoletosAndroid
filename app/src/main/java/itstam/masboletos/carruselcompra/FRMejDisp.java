package itstam.masboletos.carruselcompra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import itstam.masboletos.R;


public class FRMejDisp extends Fragment {

    Double precio, subtotal,comision, cargoTC;
    int Cant_Boletos,cont_asientos=1,ancho,alto;
    int []inicia,termina;
    String[] fila=null,dispfila=null,idfila=null;
    String zona,asientos,numerado,idevento,idsubzona,idvermapa,asientosmartxt="",idfilaasientotxt,ideventopack;
    String ideventoasientopack,idzonapack,filapack,asientopack,seccionpack,idfilapack;
    View vista;
    TextView TXVSeccionComp,TXVAsientos,TXVInfoCompra,TXVTotal;
    TextView[][] txvnombreasiento;
    Button btComprar;
    DecimalFormat df = new DecimalFormat("#.00");
    JSONArray Elementos=null;
    TableLayout TBLasientos; TableRow rowasientos; LinearLayout llasientotexto,llleyendaasientos;
    ImageButton[][] btasientos;
    ArrayList<String>asientosmar,idfilaasiento,datalugaresobtenidos;
    String asientosel;
    JSONObject jodataastospack;
    JSONArray jadataatospack= new JSONArray();
    public FRMejDisp() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_frmej_disp, container, false);
        TXVSeccionComp=(TextView) vista.findViewById(R.id.txvSeccion);
        TXVAsientos=(TextView)vista.findViewById(R.id.txvAsientos);
        TXVInfoCompra=(TextView)vista.findViewById(R.id.txvInfo);
        TXVTotal=(TextView)vista.findViewById(R.id.txvTotal);
        btComprar=(Button)vista.findViewById(R.id.btComprar);
        btComprar.setBackgroundResource(R.color.grisclaro);
        TBLasientos=(TableLayout)vista.findViewById(R.id.TBLAsientos);
        llleyendaasientos=(LinearLayout)vista.findViewById(R.id.llyLeyendaAsientos);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;

        RecibirDatos();
        return vista;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void RecibirDatos(){
        ((DetallesEventos)getActivity()).cerrar_cargando();
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        Cant_Boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        asientos=prefe.getString("asientos","");
        numerado=prefe.getString("valornumerado", "");
        idevento=prefe.getString("idevento","");
        ideventopack=prefe.getString("ideventopack","0");
        idsubzona=prefe.getString("idsubzona","0");
        comision=Double.parseDouble(prefe.getString("comision","0.00"));
        zona=prefe.getString("zona","");
        seccionpack=prefe.getString("subzona","");
        idvermapa=prefe.getString("idvermapa","");
        llenar_info();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void llenar_info(){
        set_DatosCompra("filaasientos","");
        if(numerado.equals("0")) {
            asientos= String.valueOf(Cant_Boletos);
            set_DatosCompra("asientos",asientos);
            TXVAsientos.setText(asientos);
            btComprar.setBackgroundResource(R.color.verdemb);
            btComprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DetallesEventos)getActivity()).replaceFragment(new FPagoFR());
                }
            });
            llleyendaasientos.setVisibility(View.GONE);
        }else{
            if(idvermapa.equals("1")) {
                TXVAsientos.setText("");
                if(idevento.equals("0")) {// si es el idevento es 0 significa que se va a trabajar con el idpaquete
                    consulta_asientos("https://www.masboletos.mx/appMasboletos/getButacasPaquete.php?idpaquete="+ideventopack+"&idzona="+idsubzona);
                }else{
                    consulta_asientos("https://www.masboletos.mx/appMasboletos/getButacas.php?idevento="+idevento+"&idzona="+idsubzona);
                }
            }else{
                llleyendaasientos.setVisibility(View.GONE);
                btComprar.setBackgroundResource(R.color.verdemb);
                TXVAsientos.setText(asientos);
                btComprar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DetallesEventos)getActivity()).replaceFragment(new FPagoFR());
                    }
                });
            }
        }
        TXVSeccionComp.setText(zona);
        subtotal=Cant_Boletos*precio;
        subtotal+=comision*Cant_Boletos;
        cargoTC=subtotal*0.03;
        String TxTotal="$"+String.valueOf(df.format(subtotal));
        TXVInfoCompra.setText("$"+precio+" x "+Cant_Boletos);
        TXVTotal.setText(TxTotal);
    }

    void consulta_asientos(String URL){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                            ((DetallesEventos)getActivity()).cerrar_cargando();
                            if(TBLasientos!=null){
                                TBLasientos.removeAllViews();
                            }
                            fila= new String[Elementos.length()];
                            inicia= new int[Elementos.length()];
                            termina= new int[Elementos.length()];
                            dispfila= new String[Elementos.length()];
                            idfila= new String[Elementos.length()];
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                fila[i]=datos.getString("fila");
                                fila[i]=fila[i].replace(" ","");
                                inicia[i]=datos.getInt("inicia");
                                termina[i]=datos.getInt("termina");
                                dispfila[i]=datos.getString("asientos");
                                idfila[i]=datos.getString("id");
                                ideventoasientopack=datos.getString("idevento");
                                idzonapack=datos.getString("idzona");
                            }
                            pintar_asientos();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    void pintar_asientos(){
        btasientos= new ImageButton[fila.length][termina[0]];
        txvnombreasiento = new TextView[fila.length][termina[0]];
        TableLayout.LayoutParams lptbl=new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams lptbra = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lptbra.setMargins(1,0,1,0);
        asientosmar= new ArrayList<String>(); Log.e("# asientos", String.valueOf(asientosmar.size()));
        idfilaasiento= new ArrayList<String>();
        datalugaresobtenidos= new ArrayList<>();
        for (int j=0;j<fila.length;j++) {
            rowasientos = new TableRow(getActivity());
            rowasientos.setLayoutParams(lptbl); cont_asientos=0;
            for (int i = inicia[j]; i <= termina[j]; i++) {
                llasientotexto= new LinearLayout(getActivity());
                llasientotexto.setLayoutParams(lptbra);
                llasientotexto.setOrientation(LinearLayout.VERTICAL);
                btasientos[j][i - 1] = new ImageButton(getActivity());
                btasientos[j][i - 1].setId(j*100+i);
                btasientos[j][i-1].setTag(j+","+i+",0");
                btasientos[j][i - 1].setBackgroundColor(Color.TRANSPARENT);
                btasientos[j][i - 1].setLayoutParams(new LinearLayout.LayoutParams(ancho/15, alto/15));
                //btasientos[j][i - 1].setAdjustViewBounds(true);
                btasientos[j][i - 1].setScaleType(ImageView.ScaleType.FIT_XY);
                btasientos[j][i - 1].setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        String[] coord= ((String) v.getTag()).split(",");
                        int f=0,a=0;
                        f=Integer.parseInt(coord[0]); a=Integer.parseInt(coord[1])-1;
                        if (coord[2].equals("0")) { // recibe texto de la sig forma 0,51,0 siendo fila,asiento,estado seleccion donde 0 es no pulsado y 1 lo es
                            if(asientosmar.size()<Cant_Boletos) {
                                btasientos[f][a].setImageResource(R.drawable.asiento_sel);
                                btasientos[f][a].setTag(f + "," + (a + 1) + ",1");
                                if(idevento.equals("0")){
                                    filapack=fila[f]; asientopack= String.valueOf(a+1); idfilapack=idfila[f];
                                    consulta_lugares_paquete();
                                }
                                asientosel = fila[f] + String.valueOf(a + 1); Log.e("AsientoSel",asientosel);
                                asientosmar.add(asientosel); Log.e("# asientos", String.valueOf(asientosmar.size()));
                                asientosel = fila[f] +"-"+ String.valueOf(a + 1)+"-"+idfila[f]; Log.e("AsientoSel",asientosel);
                                idfilaasiento.add(asientosel);
                                ver_asientos_sel();
                                id_asientos_sel();
                            }else{
                                ((DetallesEventos)getActivity()).AlertaBoton("Limite Alcanzado","Ya ha seleccionado todos sus boletos").show();
                            }
                        }else{
                            btasientos[f][a].setImageResource(R.drawable.asiento_disp);
                            btasientos[f][a].setTag(f+","+(a+1)+",0");
                            asientosel=fila[f]+String.valueOf(a+1);
                            Log.e("AsientoSel",asientosel);
                            for(int i =asientosmar.size()-1;i>=0;i--){
                                if(asientosmar.get(i).equals(asientosel)){
                                    asientosmar.remove(i);
                                }
                            }
                            Log.e("# asientos", String.valueOf(asientosmar.size()));
                            ver_asientos_sel();

                            if(idevento.equals("0")){
                                for(int i=datalugaresobtenidos.size()-1;i>=0;i--){
                                    String[] parts = datalugaresobtenidos.get(i).split(","); Log.e("idfilaasientopackget",parts[4]);
                                    if(asientosel.equals(parts[4])){
                                        datalugaresobtenidos.remove(i);
                                    }
                                }
                                genera_arreglo_lugarespack();
                            }

                            asientosel=fila[f]+"-"+String.valueOf(a+1)+"-"+idfila[f];
                            Log.e("idAsientoSel",asientosel);
                            for(int i =idfilaasiento.size()-1 ;i>=0;i--){
                                if(idfilaasiento.get(i).equals(asientosel)){
                                    idfilaasiento.remove(i);
                                }
                            }
                            Log.e("# idasientos", String.valueOf(idfilaasiento.size()));
                            id_asientos_sel();

                        }
                        if(asientosmar.size()==Cant_Boletos){
                            btComprar.setBackgroundResource(R.color.verdemb);
                        }else{btComprar.setBackgroundResource(R.color.grisclaro);}
                    }
                });
                if("0".equals(String.valueOf(dispfila[j].charAt(i-1)))) {
                    btasientos[j][i - 1].setImageResource(R.drawable.asiento_disp);
                }else{
                    btasientos[j][i - 1].setImageResource(R.drawable.asiento_ocupado);
                    btasientos[j][i - 1].setClickable(false);
                }
                //btasientos[j][i - 1].setRotation((float) 180);
                llasientotexto.addView(btasientos[j][i - 1]);
                txvnombreasiento[j][i-1]= new TextView(getActivity());
                txvnombreasiento[j][i-1].setTextColor(Color.BLACK);
                txvnombreasiento[j][i-1].setText(fila[j]+""+i);
                txvnombreasiento[j][i-1].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txvnombreasiento[j][i-1].setTextSize(10);
                llasientotexto.addView(txvnombreasiento[j][i-1]);

                rowasientos.addView(llasientotexto);
                cont_asientos++;
            }
            TBLasientos.addView(rowasientos);
        }
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asientosmar.size()==Cant_Boletos) {
                    set_DatosCompra("asientos",TXVAsientos.getText().toString());
                    set_DatosCompra("fila","");
                    set_DatosCompra("filaasientos",asientosmartxt);
                    set_DatosCompra("idfilafilaasiento",idfilaasientotxt);
                    ((DetallesEventos) getActivity()).replaceFragment(new FPagoFR());
                }else{
                    ((DetallesEventos)getActivity()).AlertaBoton("Selección de Boletos","No ha seleccionado todos sus lugares aún").show();
                }
            }
        });
    }

    void ver_asientos_sel(){
        int cont=1;
        Iterator<String> nombreIterator = asientosmar.iterator();
        String dato=""; TXVAsientos.setText("");
        while(nombreIterator.hasNext()){
            dato+= nombreIterator.next();
            if(cont<asientosmar.size()){
                dato+=",";
            } cont++;
            TXVAsientos.setText(dato);
        }
        asientosmartxt=dato;
    }

    void id_asientos_sel(){
        int cont=1;
        Iterator<String> nombreIterator = idfilaasiento.iterator();
        String dato="";
        while(nombreIterator.hasNext()){
            dato+= nombreIterator.next();
            if(cont<idfilaasiento.size()){
                dato+=",";
            } cont++;
        }
        idfilaasientotxt=dato;
        Log.e("idfilaasiento",idfilaasientotxt);
    }

    void consulta_lugares_paquete(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        String url="https://www.masboletos.mx/phps/obtengolugareseventospaquete.php";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                        Log.e("respenvia",response);
                        try {
                            Elementos= new JSONArray(response);
                            datalugaresobtenidos.add(asientopack+","+filapack+","+ideventoasientopack+","+idfilapack+","+filapack+asientopack+","+
                                    (filapack+"-"+asientopack+"-"+idfilapack)+","+idzonapack);
                            for(int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                datalugaresobtenidos.add(asientopack+","+filapack+","+datos.getString("idevento")+","+datos.getString("idfila")+","+filapack+asientopack+
                                        ","+(filapack+"-"+asientopack+"-"+idfilapack)+","+datos.getString("idzona"));
                            }
                            Log.e("tamlugar_pack", String.valueOf(datalugaresobtenidos.size()));
                            genera_arreglo_lugarespack();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idevento", ideventoasientopack);
                params.put("idzona", idzonapack);
                params.put("fila", filapack);
                params.put("asiento", asientopack);
                params.put("idpaquete", ideventopack);
                params.put("nombre", seccionpack);
                return params;
            }
        };
        queue.add(strRequest);
    }

    void genera_arreglo_lugarespack(){
        jadataatospack=new JSONArray();
        for(int i=0;i<datalugaresobtenidos.size();i++){
            String[] parts = datalugaresobtenidos.get(i).split(",");
            try {
                jodataastospack=new JSONObject();
                jodataastospack.put("asientos",parts[0]);
                jodataastospack.put("fila",parts[1]);
                jodataastospack.put("idevento",parts[2]);
                jodataastospack.put("idfila",parts[3]);
                jodataastospack.put("idfilaasiento",parts[4]);
                jodataastospack.put("idfilafilasiento",parts[5]);
                jodataastospack.put("idzona",parts[6]);
                jadataatospack.put(jodataastospack);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("arrayhecho",jadataatospack.toString());
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

}
