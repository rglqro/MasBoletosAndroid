package itstam.masboletos;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import java.text.DecimalFormat;
import java.util.ArrayList;


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
    ArrayList<Double>idtipoentrega,ptecentr,costoentrega;
    ArrayList<String> tipoentre,txtentre;
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
                            Elementos = response;
                            idtipoentrega= new ArrayList<Double>();
                            ptecentr= new ArrayList<Double>();
                            costoentrega = new ArrayList<Double>();
                            tipoentre= new ArrayList<String>();
                            txtentre= new ArrayList<String>();
                            String tipo=""; cant_datos=0;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                tipo=datos.getString("Tipo");
                                if (tipo.equals("FormaEntrega")) {
                                    idtipoentrega.add(Double.parseDouble(datos.getString("idforma")));
                                    ptecentr.add(Double.parseDouble(datos.getString("porcentajecargo")));
                                    costoentrega.add(Double.parseDouble(datos.getString("Costo")));
                                    tipoentre.add(datos.getString("texto"));
                                    txtentre.add(datos.getString("descripcion"));
                                    cant_datos++;
                                }
                            }
                            willcallyseguro();
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
            Log.e("Tipoentreg2",tipoentre.get(i));
            if(tipoentre.get(i).equalsIgnoreCase("Will Call")||tipoentre.get(i).equalsIgnoreCase("Boleto Electronico")||tipoentre.get(i).equalsIgnoreCase("Recibe tu boleto en Casa")) {
                rgentregas.setLayoutParams(lp);
                rbentregas[i] = new RadioButton(getActivity());
                rbentregas[i].setLayoutParams(lp);
                rbentregas[i].setButtonTintList(ColorStateList.valueOf(R.color.azulmboscuro));
                String sourceString = tipoentre.get(i)+" - <b>MX $" + costoentrega.get(i) + "</b>";
                rbentregas[i].setText(Html.fromHtml(sourceString));
                rbentregas[i].setTextColor(Color.BLACK);
                rbentregas[i].setId(i);
                txventregas[i] = new TextView(getActivity());
                txventregas[i].setLayoutParams(lp);
                txventregas[i].setText(txtentre.get(i));
                txventregas[i].setTextColor(Color.GRAY);
                txventregas[i].setId(100 + i);
                rgentregas.addView(rbentregas[i]);
                rgentregas.addView(txventregas[i]);
            }else{
                cbseguros[i]=new CheckBox(getActivity());
                cbseguros[i].setLayoutParams(lp);
                cbseguros[i].setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.azulmboscuro));
                String sourceString = tipoentre.get(i)+" - <b>MX $" + costoentrega.get(i) + "</b>";
                cbseguros[i].setText(Html.fromHtml(sourceString));
                cbseguros[i].setTextColor(Color.BLACK);
                cbseguros[i].setId(i);
                cbseguros[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.e("idcb",String.valueOf(buttonView.getId()));
                        if(isChecked){
                            suma_seguro_entrega(buttonView.getId());
                        }else{
                            resta_seguro_entrega(buttonView.getId());
                        }
                    }
                });
                txventregas[i] = new TextView(getActivity());
                txventregas[i].setLayoutParams(lp);
                txventregas[i].setText(txtentre.get(i));
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
        if (tipoentre.get(j).equalsIgnoreCase("Will Call")||tipoentre.get(j).equalsIgnoreCase("Boleto Electronico")||tipoentre.get(j).equalsIgnoreCase("Recibe tu boleto en Casa")) {
            Sumafentr=costoentrega.get(j)+(total2*(ptecentr.get(j)/100));
            btcontinuar5.setBackgroundResource(R.color.verdemb);
            txvfentr.setText("MX $"+df.format(Sumafentr+sumaseg)+" x "+cant_boletos);
            CargoFEntr=(Sumafentr*cant_boletos);
            total=total2+CargoFEntr+cargoseg;
            txvtotal.setText("MX $"+df.format(total));
            fentregas=tipoentre.get(j);
        }
    }

    void suma_seguro_entrega(int j){
        total2=total;
        if(tipoentre.get(j).equalsIgnoreCase("Seguro Boleto")){
            sumaseg=costoentrega.get(j)+(total2*(ptecentr.get(j)/100));
            cargoseg=sumaseg*cant_boletos;
            total=total2+cargoseg;
            txvtotal.setText("MX $"+df.format(total));
            txvfentr.setText("MX $"+df.format(sumaseg+Sumafentr)+" x "+cant_boletos);
        }
    }

    void resta_seguro_entrega(int j){
        Double total3=total;
        if(tipoentre.get(j).equalsIgnoreCase("Seguro Boleto")){
            sumaseg=costoentrega.get(j)+(total3*(ptecentr.get(j)/100));
            cargoseg=sumaseg*cant_boletos;
            total=total3-cargoseg;
            sumaseg=0.00; cargoseg=0.00;
            txvtotal.setText("MX $"+df.format(total));
            txvfentr.setText("MX $"+df.format(sumaseg+Sumafentr)+" x "+cant_boletos);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
