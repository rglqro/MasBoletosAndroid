package itstam.masboletos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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


public class FEntregaFr extends Fragment {
    View vista;
    SharedPreferences prefe;
    String seccion,fila,datoscargos,idevento="",fentregas="";
    Double precio=0.00,total=0.00, ptecargo=0.00,imtecargo=0.00, CargosServ=0.00,CargoFEntr=0.00,CargoFEntr2=0.00;
    int asiento=0,cant_datos=0;
    TextView txvprecio,txvfila,txvasiento,txvseccion,txvtotal,txvcserv,txvfentr;
    DecimalFormat df = new DecimalFormat("#.00");
    Button btcontinuar5;
    CheckBox cbseguro,cbwillc;
    Double[]idtipoentrega,ptecentr,costoentrega,ptecentr2,costoentrega2;
    JSONArray Elementos;

    public FEntregaFr() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this
        vista=inflater.inflate(R.layout.fragment_fentrega, container, false);
        btcontinuar5=(Button)vista.findViewById(R.id.btContinuar5);
        txvseccion=(TextView)vista.findViewById(R.id.txvSeccionFE);
        txvasiento=(TextView)vista.findViewById(R.id.txvAsientosFE);
        txvfila=(TextView)vista.findViewById(R.id.txvfilaFE);
        txvprecio=(TextView)vista.findViewById(R.id.txvprecioFE);
        txvcserv=(TextView)vista.findViewById(R.id.txvcservFE);
        txvfentr=(TextView)vista.findViewById(R.id.txvcfentrFE);
        cbseguro=(CheckBox)vista.findViewById(R.id.cbseguro);
        cbwillc=(CheckBox)vista.findViewById(R.id.cbwillcall);
        txvtotal=(TextView)vista.findViewById(R.id.txvtotalFE);

        btcontinuar5.setClickable(false);
        recepcion_datos();
        return vista;
    }

    void recepcion_datos(){
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=prefe.getString("idevento","");
        seccion=prefe.getString("zona","");
        fila=prefe.getString("fila","");
        asiento=Integer.parseInt(prefe.getString("asientos","0"));
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        datoscargos=prefe.getString("datoscargos","");
        String[] datocargo = datoscargos.split(",");
        ptecargo=Double.parseDouble(datocargo[1]);
        imtecargo=Double.parseDouble(datocargo[2]);
        txvseccion.setText(seccion);
        txvasiento.setText(""+asiento);
        txvfila.setText(fila);
        total=precio*asiento;
        CargosServ=((precio*(ptecargo/100))+imtecargo);
        txvprecio.setText("MX $"+df.format(precio)+" x "+asiento);
        txvcserv.setText("MX $"+df.format(CargosServ)+" x "+asiento);
        txvfentr.setText("MX $0.00"+" x "+asiento);
        total+=CargosServ*asiento;
        txvtotal.setText("MX $"+df.format(total));
        consulta_formas_entrega();

    }

    void consulta_formas_entrega(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("http://www.masboletos.mx/appMasboletos/getFormaPagoFormaEntrega.php?idevento="+idevento);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            try {
                                Elementos = new JSONArray(resultado);
                                idtipoentrega= new Double[Elementos.length()];
                                ptecentr= new Double[Elementos.length()];
                                costoentrega = new Double[Elementos.length()];
                                String tipo="";
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    tipo=datos.getString("Tipo");
                                    if (tipo.equals("FormaEntrega")) {
                                        idtipoentrega[i]=Double.parseDouble(datos.getString("idforma"));
                                        ptecentr[i]=Double.parseDouble(datos.getString("porcentajecargo"));
                                        costoentrega[i]=Double.parseDouble(datos.getString("Costo"));
                                        cant_datos++;
                                    }else if (tipo.equals("FormaPago")) {
                                        idtipoentrega[i]=0.00;
                                        ptecentr[i]=0.00;
                                        costoentrega[i]=0.00;
                                    }
                                }
                                lista_willcall_seguro();
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

    void lista_willcall_seguro(){
        int cont=0;
        ptecentr2= new Double[cant_datos];
        costoentrega2 = new Double[cant_datos];
        for (int i=0; i<idtipoentrega.length;i++){
            if(idtipoentrega[i]==1.0 || idtipoentrega[i]==4.0) {
                ptecentr2[cont] = ptecentr[i];
                costoentrega2[cont] = costoentrega[i];
                cont++;
            }
        }
        willcallyseguro();
    }

    void willcallyseguro(){
        final Double total2=total;
        cbwillc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CargoFEntr=costoentrega2[1]+(total2*(ptecentr2[1]/100));
                if (isChecked){
                    btcontinuar5.setClickable(true);
                    txvfentr.setText("MX $"+df.format(CargoFEntr+CargoFEntr2)+" x "+asiento);
                    CargoFEntr=CargoFEntr*asiento;
                    total=total2+CargoFEntr;
                    txvtotal.setText("MX $"+df.format(total));
                    fentregas="WILLCALL";
                }else{
                    total=total-(CargoFEntr*asiento);
                    CargoFEntr=0.00;
                    txvtotal.setText("MX $"+df.format(total));
                    txvfentr.setText("MX $"+(CargoFEntr+CargoFEntr2)+" x "+asiento);
                }
            }
        });
        cbseguro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CargoFEntr2=costoentrega2[0]+(total2*(ptecentr2[0]/100));
                if(isChecked){
                    btcontinuar5.setClickable(true);
                    txvfentr.setText("MX $"+df.format(CargoFEntr2+CargoFEntr)+" x "+asiento);
                    CargoFEntr2=CargoFEntr2*asiento;
                    total=total2+CargoFEntr2;
                    txvtotal.setText("MX $"+df.format(total));

                }else {
                    total=total-(CargoFEntr2*asiento);
                    CargoFEntr2=0.00;
                    txvtotal.setText("MX $"+df.format(total));
                    txvfentr.setText("MX $"+CargoFEntr2+CargoFEntr+" x "+asiento);
                }
            }
        });
        continuar();
    }

    void continuar(){
        btcontinuar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbseguro.isChecked() || cbwillc.isChecked()) {
                    set_DatosCompra("cargos_servicio",txvcserv.getText().toString());
                    set_DatosCompra("cargos_entrega",txvfentr.getText().toString());
                    set_DatosCompra("fentrega",fentregas);
                    set_DatosCompra("total", df.format(total));
                    ((DetallesEventos) getActivity()).replaceFragment(new RevisionFR());
                }else{
                    Toast.makeText(getActivity(),"Debe seleccionar al menos una de las formas de entrega",Toast.LENGTH_LONG).show();
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
