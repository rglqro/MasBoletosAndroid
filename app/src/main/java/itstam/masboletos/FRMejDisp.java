package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Element;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;


public class FRMejDisp extends Fragment {

    Double precio, subtotal, Total,comision, cargoTC;
    int Cant_Boletos,cont_asientos=1;
    int []inicia,termina;
    String[] fila=null,dispfila=null;
    String zona,asientos,numerado,idevento,idsubzona,idvermapa;
    View vista;
    TextView TXVSeccionComp,TXVAsientos,TXVInfoCompra,TXVTotal;
    TextView[][] txvnombreasiento;
    Button btComprar;
    DecimalFormat df = new DecimalFormat("#.00");
    JSONArray Elementos=null;
    TableLayout TBLasientos; TableRow rowasientos;
    ImageButton[][] btasientos;
    ArrayList<String>asientosmar;
    String asientosel;
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
        idsubzona=prefe.getString("idsubzona","0");
        comision=Double.parseDouble(prefe.getString("comision","0.00"));
        zona=prefe.getString("zona","");
        idvermapa=prefe.getString("idvermapa","");
        llenar_info();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void llenar_info(){
        if(numerado.equals("0")) {
            TXVAsientos.setText(asientos);
            btComprar.setBackgroundResource(R.color.verdemb);
            btComprar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DetallesEventos)getActivity()).replaceFragment(new FPagoFR());
                }
            });
        }else{
            if(idvermapa.equals("1")) {
                TXVAsientos.setText("");
                consulta_asientos();
            }else{
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

    void consulta_asientos(){
        ((DetallesEventos)getActivity()).iniciar_cargando();
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getButacas.php?idevento="+idevento+"&idzona="+idsubzona);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                ((DetallesEventos)getActivity()).cerrar_cargando();
                                if(TBLasientos!=null){
                                    TBLasientos.removeAllViews();
                                }
                                fila= new String[Elementos.length()];
                                inicia= new int[Elementos.length()];
                                termina= new int[Elementos.length()];
                                dispfila= new String[Elementos.length()];
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    fila[i]=datos.getString("fila");
                                    inicia[i]=datos.getInt("inicia");
                                    termina[i]=datos.getInt("termina");
                                    dispfila[i]=datos.getString("asientos");
                                }
                                pintar_asientos();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }});  //permite trabajar con la interfaz grafica
            }};
        tr.start();
    }


    void pintar_asientos(){
        btasientos= new ImageButton[fila.length][termina[0]];
        txvnombreasiento = new TextView[fila.length][termina[0]];
        TableLayout.LayoutParams lptbl=new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        TableRow.LayoutParams lptbra = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,90,1);
        lptbra.setMargins(1,0,2,0);
        asientosmar= new ArrayList<String>(); Log.e("# asientos", String.valueOf(asientosmar.size()));
        for (int j=0;j<fila.length;j++) {
            rowasientos = new TableRow(getActivity());
            rowasientos.setLayoutParams(lptbl); cont_asientos=0;
            for (int i = inicia[j]; i <= termina[j]; i++) {
                btasientos[j][i - 1] = new ImageButton(getActivity());
                btasientos[j][i - 1].setId(j*100+i);
                btasientos[j][i-1].setTag(j+","+i+",0");
                btasientos[j][i - 1].setBackgroundColor(Color.TRANSPARENT);
                btasientos[j][i - 1].setLayoutParams(lptbra);
                btasientos[j][i - 1].setAdjustViewBounds(true);
                btasientos[j][i - 1].setScaleType(ImageView.ScaleType.CENTER);
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
                                asientosel = fila[f] + String.valueOf(a + 1);
                                Log.e("AsientoSel",asientosel);
                                asientosmar.add(asientosel);
                                Log.e("# asientos", String.valueOf(asientosmar.size()));
                                ver_asientos_sel();
                            }else{
                                ((DetallesEventos)getActivity()).AlertaBoton("Limite Alcanzado","Ya ha seleccionado todos sus boletos").show();
                            }
                        }else{
                            btasientos[f][a].setImageResource(R.drawable.asiento_disp);
                            btasientos[f][a].setTag(f+","+(a+1)+",0");
                            asientosel=fila[f]+String.valueOf(a+1);
                            Log.e("AsientoSel",asientosel);
                            for(int i =0;i<asientosmar.size();i++){
                                if(asientosmar.size()==1){
                                    asientosmar.clear();
                                }else if(asientosmar.get(i).equals(asientosel)){
                                    asientosmar.remove(i);
                                }
                            }
                            Log.e("# asientos", String.valueOf(asientosmar.size()));
                            ver_asientos_sel();
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
                rowasientos.addView(btasientos[j][i - 1]);
                cont_asientos++;
            }
            TBLasientos.addView(rowasientos);
            rowasientos = new TableRow(getActivity());
            rowasientos.setLayoutParams(lptbl);
            for (int i=inicia[j];i<=termina[j];i++){
                txvnombreasiento[j][i-1]= new TextView(getActivity());
                txvnombreasiento[j][i-1].setTextColor(Color.BLACK);
                txvnombreasiento[j][i-1].setText(fila[j]+""+i);
                rowasientos.addView(txvnombreasiento[j][i-1]);
            }
            TBLasientos.addView(rowasientos);
        }
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asientosmar.size()==Cant_Boletos) {
                    set_DatosCompra("asientos",TXVAsientos.getText().toString());
                    set_DatosCompra("fila","");
                    ((DetallesEventos) getActivity()).replaceFragment(new FPagoFR());
                }else{
                    ((DetallesEventos)getActivity()).AlertaBoton("Selección de Boletos","No ha seleccionado todos sus lugares aún").show();
                }
            }
        });
    }

    void ver_asientos_sel(){
        Iterator<String> nombreIterator = asientosmar.iterator();
        String dato=""; TXVAsientos.setText("");
        while(nombreIterator.hasNext()){
            String elemento = nombreIterator.next();
            dato+=elemento+",";
            TXVAsientos.setText(dato);
        }
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    public String inserta(String enlace){ // metodo que inserta los parametros en la BD
        URL url = null;
        Log.d("Enlace ",enlace);
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
        try {

        }catch (Exception e){}
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
