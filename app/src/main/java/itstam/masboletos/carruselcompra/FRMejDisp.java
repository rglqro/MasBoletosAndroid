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
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    int Cant_Boletos,cont_asientos=1,ancho,alto,asientosxsel=0,filamayor=0;
    int []inicia,termina;
    String[] fila=null,dispfila=null,idfila=null;
    String zona,subzona,asientos,numerado,idevento,idsubzona,idvermapa,asientosmartxt="",idfilaasientotxt,ideventopack;
    String ideventoasientopack,idzonapack,filapack,asientopack,seccionpack,idfilapack;
    View vista;
    TextView TXVSeccionComp,TXVAsientos,TXVInfoCompra,TXVTotal,txvtotalasientos;
    TextView[][] txvnombreasiento;
    Button btComprar;
    DecimalFormat df = new DecimalFormat("#0.00");
    JSONArray Elementos=null;
    TableLayout TBLasientos; TableRow rowasientos; LinearLayout llasientotexto,llleyendaasientos;
    ImageButton[][] btasientos;
    ArrayList<String>asientosmar,idfilaasiento,datalugaresobtenidos;
    String asientosel;
    JSONObject jodataastospack =new JSONObject();
    JSONArray jadataatospack= new JSONArray();
    NestedScrollView scvertAsientos;
    SharedPreferences prefe;
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
        txvtotalasientos=vista.findViewById(R.id.txvtotalasientos);
        scvertAsientos=vista.findViewById(R.id.scvertAsientos);

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
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        Cant_Boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        asientos=prefe.getString("asientos","");
        numerado=prefe.getString("valornumerado", "");
        idevento=prefe.getString("idevento","");
        ideventopack=prefe.getString("ideventopack","0");
        idsubzona=prefe.getString("idsubzona","0");
        comision=Double.parseDouble(prefe.getString("comision","0.00"));
        zona=prefe.getString("zona","");
        subzona=prefe.getString("subzona","");
        seccionpack=prefe.getString("subzona","");
        idvermapa=prefe.getString("idvermapa","");
        llenar_info();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void llenar_info(){
        set_DatosCompra("filaasientos","");
        if(numerado.equals("0")) { // si numerado es 0 solo se motrará la informacion obtenida de los boletos
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
        }else{ // sino enrtrará aqui en donde checará si el usuario ha decidido elegir sus lugares o quedarse con los que da el servidor
            if(idvermapa.equals("1")) { //si es 1 procederá a consultar los lugares disponibles
                TXVAsientos.setText("");
                if(idevento.equals("0")) {// si es el idevento es 0 significa que se va a trabajar con el idpaquete
                    consulta_asientos("https://www.masboletos.mx/appMasboletos.fueralinea/getButacasPaquete.php?idpaquete="+ideventopack+"&idzona="+idsubzona);
                }else{
                    consulta_asientos("https://www.masboletos.mx/appMasboletos.fueralinea/getButacas.php?idevento="+idevento+"&idzona="+idsubzona);
                }
            }else{ /*sino solo se encargará de pintar los lugares obtenidos del servidor*/
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
        TXVSeccionComp.setText(zona+"/"+subzona);
        subtotal=Cant_Boletos*precio;
        //subtotal+=comision*Cant_Boletos;
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
                                if(filamayor<datos.getInt("termina")) /*Este if obtiene la fila mas larga para que el arreglo de asientos pueda generarla sin conflictos con las filas más cortas*/
                                filamayor=datos.getInt("termina");
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
        scvertAsientos.getLayoutParams().height=alto/3;
        txvtotalasientos.setText("Asientos seleccionados: "+asientosxsel+" de "+Cant_Boletos);
        btasientos= new ImageButton[fila.length][filamayor];
        txvnombreasiento = new TextView[fila.length][filamayor];
        TableLayout.LayoutParams lptbl=new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams lptbra = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lptbra.setMargins(1,0,1,0);
        asientosmar= new ArrayList<String>(); Log.e("# asientos", String.valueOf(asientosmar.size()));/*Aqui se iran almacenando los asientos seleccionados de la sig forma A1,A2,A3*/
        idfilaasiento= new ArrayList<String>();/*Aqui se almacenaran lo asientos de la siguiente forma: A-1-13256,A-2-13256,A-3-13256 (FILA-#asiento-IDFILA)*/
        datalugaresobtenidos= new ArrayList<>(); /*En caso de ser paquete aqui irán los asientos selecionados*/
        asientosxsel=0;
        for (int j=0;j<fila.length;j++) {
            rowasientos = new TableRow(getActivity());
            rowasientos.setLayoutParams(lptbl); cont_asientos=0;
            for (int i = inicia[j]; i <= termina[j]; i++) {
                llasientotexto= new LinearLayout(getActivity());
                llasientotexto.setLayoutParams(lptbra);
                llasientotexto.setOrientation(LinearLayout.VERTICAL);
                btasientos[j][i - 1] = new ImageButton(getActivity());
                btasientos[j][i - 1].setId(j*100+i);
                btasientos[j][i-1].setTag(j+","+i+",0");/*El tag servirá para indicar si es seleccionado quedando asi: 0,1,0 (INDICELISTAFILA,ASIENTO,STATUSPULSADO)*/
                btasientos[j][i - 1].setBackgroundColor(Color.TRANSPARENT);
                btasientos[j][i - 1].setLayoutParams(new LinearLayout.LayoutParams(ancho/15, alto/15));
                //btasientos[j][i - 1].setAdjustViewBounds(true);
                btasientos[j][i - 1].setScaleType(ImageView.ScaleType.FIT_XY);
                btasientos[j][i - 1].setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onClick(View v) {
                        String[] coord= ((String) v.getTag()).split(",");/*Aqui se separa el tag con un split de comas,*/
                        int f=0,a=0;/*f será el indice de fila, a el indice de asiento*/
                        f=Integer.parseInt(coord[0]); a=Integer.parseInt(coord[1])-1;/*Recordar que a es el asiento entonces se le resta 1 por la posicion del arreglo en donde el asiento 1 irá en la poscion 0 del arreglo*/
                        if (coord[2].equals("0")) { // recibe texto de la sig forma 0,51,0 siendo fila,asiento,estado seleccion donde 0 es no pulsado y 1 lo es, sino es pulsado lo cambia a seleccionado
                            if(asientosmar.size()<Cant_Boletos) { /*Si los asientos no han sido seleccionados todos deja avanzar*/
                                asientosxsel++;
                                btasientos[f][a].setImageResource(R.drawable.asiento_sel);
                                btasientos[f][a].setTag(f + "," + (a + 1) + ",1"); /*Si llegamos aqui es que el asiento puede ser seleccionado y su estatus cambia a 1 de que fue seleccionado asignandoles 0,1,1*/
                                if(idevento.equals("0")){/*Si almacena las almacena de la mima forma en otras variables*/
                                    filapack=fila[f]; asientopack= String.valueOf(a+1); idfilapack=idfila[f];
                                    consulta_lugares_paquete();
                                }
                                asientosel = fila[f] + String.valueOf(a + 1); Log.e("AsientoSel",asientosel); /*Esta variable crea el asiento seleccionado A1*/
                                asientosmar.add(asientosel); Log.e("# asientos", String.valueOf(asientosmar.size())); /*Se agrega a la lista que los almacenará*/
                                asientosel = fila[f] +"-"+ String.valueOf(a + 1)+"-"+idfila[f]; Log.e("AsientoSel",asientosel); /*Aqui se crea el asiento A-1-13256*/
                                idfilaasiento.add(asientosel); /*Se agrega a su lista correspondiente*/
                                ver_asientos_sel();/*Este metodo imprimirá los asientos que se vayan seleccionando separandolos por comas*/
                                id_asientos_sel();/*Este metodo hará lo mismo que el metodo anterior*/
                            }else{
                                ((DetallesEventos)getActivity()).AlertaBoton("Limite Alcanzado","Ya ha seleccionado todos sus boletos").show();
                            }
                            txvtotalasientos.setText("Asientos seleccionados: "+asientosxsel+" de "+Cant_Boletos);
                        }else{
                            asientosxsel--;
                            btasientos[f][a].setImageResource(R.drawable.asiento_disp); /*Aqui se cambia la imagen a disponible de nuevo*/
                            btasientos[f][a].setTag(f+","+(a+1)+",0"); /*y su status cambia a 0 que es dispobible*/
                            asientosel=fila[f]+String.valueOf(a+1); /*se crea nuevamente el asiento seleccionado "A1"*/
                            Log.e("AsientoSel",asientosel);
                            for(int i =asientosmar.size()-1;i>=0;i--){ /*y este for busca el asiento sleccionado para borrarlo de la lista*/
                                if(asientosmar.get(i).equals(asientosel)){
                                    asientosmar.remove(i);
                                }
                            }
                            Log.e("# asientos", String.valueOf(asientosmar.size()));
                            ver_asientos_sel();/* y se vuleven a pintar los asientos restantes */

                            if(idevento.equals("0")){
                                for(int i=datalugaresobtenidos.size()-1;i>=0;i--){/*En los paquetes de la misma forma se busca borrar el asiento que se desmarcó*/
                                    String[] parts = datalugaresobtenidos.get(i).split(","); Log.e("idfilaasientopackget",parts[4]);
                                    if(asientosel.equals(parts[4])){/*la estructura viene separada por comas en donde se extrae la posicion 4 que es filaasiento "A1" para ser comparado con el seleccionado y borrarlo de la lista*/
                                        datalugaresobtenidos.remove(i);
                                    }
                                }
                                genera_arreglo_lugarespack();
                            }

                            asientosel=fila[f]+"-"+String.valueOf(a+1)+"-"+idfila[f];/* se crea la estructura A-1-1256*/
                            Log.e("idAsientoSel",asientosel);
                            for(int i =idfilaasiento.size()-1 ;i>=0;i--){/* y este for se encarga de eliminarlo de los seleccionados*/
                                if(idfilaasiento.get(i).equals(asientosel)){
                                    idfilaasiento.remove(i);
                                }
                            }
                            Log.e("# idasientos", String.valueOf(idfilaasiento.size()));
                            id_asientos_sel();
                            txvtotalasientos.setText("Asientos seleccionados: "+asientosxsel+" de "+Cant_Boletos);/*Y aqui se le actualiza al usuario cuantos le faltan por seleccionar*/
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
                rowasientos.setGravity(Gravity.CENTER);
                cont_asientos++;
            }
            TBLasientos.addView(rowasientos);
            TBLasientos.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asientosmar.size()==Cant_Boletos) {
                    set_DatosCompra("asientos",TXVAsientos.getText().toString());
                    set_DatosCompra("fila","");
                    set_DatosCompra("filaasientos",asientosmartxt);
                    set_DatosCompra("idfilafilaasiento",idfilaasientotxt);
                    set_DatosCompra("datalugarespack",jadataatospack.toString());
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
                                    (filapack+"-"+asientopack+"-"+idfilapack)+","+idzonapack);/*(1,A,1380,13256,A1,A-1-13256,13856) Aqui se crea la lista con la informacion de los asiento de los paquetes donde esta insercion a la lista son del asiento que ha elegido el usuario en la app*/
                            for(int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                datalugaresobtenidos.add(asientopack+","+filapack+","+datos.getString("idevento")+","+datos.getString("idfila")+","+filapack+asientopack+
                                        ","+(filapack+"-"+asientopack+"-"+idfilapack)+","+datos.getString("idzona"));/*Si la consulta es correcta se obtiene un JSON del servidor con el resto de la informacion y tambien se agrega a la lista separadas por comas*/
                            }
                            Log.e("tamlugar_pack", String.valueOf(datalugaresobtenidos.size()));
                            genera_arreglo_lugarespack();/*Una vez generada la lista se construye el arreglo JSON que se enviará al servidor*/
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
