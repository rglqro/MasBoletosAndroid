package itstam.masboletos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    String seccion,fila,datoscargos,idevento="",fentregas="",asiento="";
    Double precio=0.00,total=0.00, ptecargo=0.00,imtecargo=0.00, CargosServ=0.00,CargoFEntr=0.00,Sumafentr=0.00;
    Double cargoseg=0.00,sumaseg=0.00;
    int cant_datos=0,cant_boletos;
    TextView txvprecio,txvfila,txvasiento,txvseccion,txvtotal,txvcserv,txvfentr;
    DecimalFormat df = new DecimalFormat("#.00");
    Button btcontinuar5;RadioButton[]rbentregas; RadioGroup rgentregas;
    TextView[]txventregas;
    Double[]idtipoentrega,ptecentr,costoentrega,ptecentr2,costoentrega2;
    String[]tipoentre,tipoentre2,txtentre,txtentre2;
    LinearLayout llentregas,llseguros;
    Double total2=0.00,total4=0.00;
    JSONArray Elementos;
    CheckBox cbseguros[];

    public FEntregaFr() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
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
        txvtotal=(TextView)vista.findViewById(R.id.txvtotalFE);
        llentregas=(LinearLayout)vista.findViewById(R.id.llfentregas);
        llseguros=(LinearLayout)vista.findViewById(R.id.llseguro);

        btcontinuar5.setBackgroundResource(R.color.grisclaro);
        recepcion_datos();
        return vista;
    }

    void recepcion_datos(){
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        idevento=prefe.getString("idevento","");
        seccion=prefe.getString("zona","");
        fila=prefe.getString("fila","");
        cant_boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        asiento=(prefe.getString("asientos","0"));
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        datoscargos=prefe.getString("datoscargos","");
        String[] datocargo = datoscargos.split(",");
        ptecargo=Double.parseDouble(datocargo[1]);
        imtecargo=Double.parseDouble(datocargo[2]);
        txvseccion.setText(seccion);
        txvasiento.setText(""+asiento);
        txvfila.setText(fila);
        total=precio*cant_boletos;
        CargosServ=((precio*(ptecargo/100))+imtecargo);
        txvprecio.setText("MX $"+df.format(precio)+" x "+cant_boletos);
        txvcserv.setText("MX $"+df.format(CargosServ)+" x "+cant_boletos);
        txvfentr.setText("MX $0.00"+" x "+cant_boletos);
        total+=CargosServ*cant_boletos;
        txvtotal.setText("MX $"+df.format(total));
        consulta_formas_entrega();

    }

    void consulta_formas_entrega(){
        ((DetallesEventos)getActivity()).dialogcarg.show();
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
                                Elementos = new JSONArray(resultado);
                                idtipoentrega= new Double[Elementos.length()];
                                ptecentr= new Double[Elementos.length()];
                                costoentrega = new Double[Elementos.length()];
                                tipoentre= new String[Elementos.length()];
                                txtentre= new String[Elementos.length()];
                                String tipo=""; cant_datos=0;
                                for (int i=0;i<Elementos.length();i++){
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    tipo=datos.getString("Tipo");
                                    if (tipo.equals("FormaEntrega")) {
                                        idtipoentrega[i]=Double.parseDouble(datos.getString("idforma"));
                                        ptecentr[i]=Double.parseDouble(datos.getString("porcentajecargo"));
                                        costoentrega[i]=Double.parseDouble(datos.getString("Costo"));
                                        tipoentre[i]=datos.getString("texto");
                                        txtentre[i]=datos.getString("descripcion");
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void lista_willcall_seguro(){
        int cont=0;
        ptecentr2= new Double[cant_datos];
        costoentrega2 = new Double[cant_datos];
        tipoentre2= new String[cant_datos];
        txtentre2= new String[cant_datos];
        for (int i=0; i<idtipoentrega.length;i++){
            if(idtipoentrega[i]==1.0 || idtipoentrega[i]==4.0 ||idtipoentrega[i]==2.0 ||idtipoentrega[i]==3.0) {
                ptecentr2[cont] = ptecentr[i];
                costoentrega2[cont] = costoentrega[i];
                tipoentre2[cont]=tipoentre[i];
                txtentre2[cont]=txtentre[i];
                cont++;
            }
        }
        willcallyseguro();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    void willcallyseguro() {
        total4=total;
        rgentregas = new RadioGroup(getActivity());
        rbentregas = new RadioButton[cant_datos];
        txventregas = new TextView[cant_datos];
        cbseguros = new CheckBox[cant_datos];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < cant_datos; i++) {
            Log.e("Tipoentreg2",tipoentre2[i]);
            if(tipoentre2[i].equalsIgnoreCase("Will Call")||tipoentre2[i].equalsIgnoreCase("Boleto Electronico")||tipoentre2[i].equalsIgnoreCase("Recibe tu boleto en Casa")) {
                rgentregas.setLayoutParams(lp);
                rbentregas[i] = new RadioButton(getActivity());
                rbentregas[i].setLayoutParams(lp);
                rbentregas[i].setButtonTintList(ColorStateList.valueOf(R.color.azulmboscuro));
                String sourceString = tipoentre2[i]+" - <b>MX $" + costoentrega2[i] + "</b>";
                rbentregas[i].setText(Html.fromHtml(sourceString));
                rbentregas[i].setTextColor(Color.BLACK);
                rbentregas[i].setId(i);
                txventregas[i] = new TextView(getActivity());
                txventregas[i].setLayoutParams(lp);
                txventregas[i].setText(txtentre2[i]);
                txventregas[i].setTextColor(Color.GRAY);
                txventregas[i].setId(100 + i);
                rgentregas.addView(rbentregas[i]);
                rgentregas.addView(txventregas[i]);
            }else{
                cbseguros[i]=new CheckBox(getActivity());
                cbseguros[i].setLayoutParams(lp);
                cbseguros[i].setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.azulmboscuro));
                String sourceString = tipoentre2[i]+" - <b>MX $" + costoentrega2[i] + "</b>";
                cbseguros[i].setText(Html.fromHtml(sourceString));
                cbseguros[i].setTextColor(Color.BLACK);
                cbseguros[i].setId(i);
                cbseguros[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.e("idcb",String.valueOf(buttonView.getId()));
                        if(isChecked){
                            btcontinuar5.setBackgroundResource(R.color.verdemb);
                            suma_seguro_entrega(buttonView.getId());
                        }else{
                            resta_seguro_entrega(buttonView.getId());
                        }
                    }
                });
                txventregas[i] = new TextView(getActivity());
                txventregas[i].setLayoutParams(lp);
                txventregas[i].setText(txtentre2[i]);
                txventregas[i].setTextColor(Color.GRAY);
                txventregas[i].setId(100 + i);
                llseguros.addView(cbseguros[i]);
                llseguros.addView(txventregas[i]);
            }

        }
        llentregas.removeAllViews();
        rgentregas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                suma_cargo_entrega(checkedId);
            }
        });
        llentregas.addView(rgentregas);
        ((DetallesEventos)getActivity()).dialogcarg.dismiss();
        continuar();
    }

    void suma_cargo_entrega(int j){
        total2=total-CargoFEntr-cargoseg;
        if (tipoentre2[j].equalsIgnoreCase("Will Call")||tipoentre2[j].equalsIgnoreCase("Boleto Electronico")||tipoentre2[j].equalsIgnoreCase("Recibe tu boleto en Casa")) {
            Sumafentr=costoentrega2[j]+(total2*(ptecentr2[j]/100));
            btcontinuar5.setBackgroundResource(R.color.verdemb);
            txvfentr.setText("MX $"+df.format(Sumafentr+sumaseg)+" x "+cant_boletos);
            CargoFEntr=(Sumafentr*cant_boletos);
            total=total2+CargoFEntr+cargoseg;
            txvtotal.setText("MX $"+df.format(total));
            fentregas="Will Call";
        }
    }

    void suma_seguro_entrega(int j){
        total2=total;
        if(tipoentre2[j].equalsIgnoreCase("Seguro Boleto")){
            sumaseg=costoentrega2[j]+(total2*(ptecentr2[j]/100));
            cargoseg=sumaseg*cant_boletos;
            total=total2+cargoseg;
            txvtotal.setText("MX $"+df.format(total));
            txvfentr.setText("MX $"+df.format(sumaseg+Sumafentr)+" x "+cant_boletos);
        }
    }

    void resta_seguro_entrega(int j){
        Double total3=total;
        if(tipoentre2[j].equalsIgnoreCase("Seguro Boleto")){
            sumaseg=costoentrega2[j]+(total3*(ptecentr2[j]/100));
            cargoseg=sumaseg*cant_boletos;
            total=total3-cargoseg;
            sumaseg=0.00; cargoseg=0.00;
            txvtotal.setText("MX $"+df.format(total));
            txvfentr.setText("MX $"+df.format(sumaseg+Sumafentr)+" x "+cant_boletos);
            fentregas=tipoentre2[j];
        }
    }

    void continuar(){
        btcontinuar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgentregas.getCheckedRadioButtonId()!=-1) {
                    set_DatosCompra("cargos_servicio", txvcserv.getText().toString());
                    set_DatosCompra("cargos_entrega", txvfentr.getText().toString());
                    set_DatosCompra("fentrega", fentregas);
                    set_DatosCompra("total", df.format(total));
                    ((DetallesEventos) getActivity()).replaceFragment(new RevisionFR());
                }else
                    Toast.makeText(getActivity(),"Debe seleccionar al menos una de las formas de entrega",Toast.LENGTH_LONG).show();
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
