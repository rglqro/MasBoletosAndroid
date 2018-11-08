package itstam.masboletos.carruselcompra;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import itstam.masboletos.DescEntregas;
import itstam.masboletos.R;


public class FEntregaFr extends Fragment {
    View vista;
    SharedPreferences prefe;
    String zona,seccion,fila,ideventopack,idevento="",fentregas="",asiento="",numerado="";
    Double precio=0.00,total=0.00,CargoFEntr=0.00,Sumafentr=0.00;
    Double cargoseg=0.00,sumaseg=0.00;
    int cant_datos=0,cant_boletos;
    TextView txvfila,txvasiento,txvseccion,txvtotal,txvfentr,txvfila2,txvasiento2,txvseccion2;
    DecimalFormat df = new DecimalFormat("#0.00");
    Button btcontinuar5;RadioButton[]rbentregas; RadioGroup rgentregas;
    TextView[]txventregas;
    Button[] btdescentr;
    ArrayList<Double>ptecentr,costoentrega;
    ArrayList<Integer> idtipoentrega;
    ArrayList<String> tipoentre,txtentre;
    LinearLayout llentregas,llseguros;
    Double total2=0.00,total4=0.00;
    JSONArray Elementos;
    CheckBox cbseguros[];
    View lineasep[];

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
        txvseccion2=(TextView)vista.findViewById(R.id.txvSeccionFE2);
        txvasiento2=(TextView)vista.findViewById(R.id.txvAsientosFE2);
        txvfila2=(TextView)vista.findViewById(R.id.txvfilaFE2);
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
        zona=prefe.getString("zona","");
        seccion=prefe.getString("subzona","");
        fila=prefe.getString("fila","");
        cant_boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        asiento=(prefe.getString("asientos","0"));
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        numerado=prefe.getString("valornumerado","0");
        txvseccion2.setText(zona+"/"+seccion+" x "+cant_boletos);

        if(numerado.equals("0")){
            txvfila.setVisibility(View.GONE); txvfila2.setVisibility(View.GONE);
            txvasiento.setVisibility(View.GONE); txvasiento2.setVisibility(View.GONE);
        }

        txvasiento.setText(""+asiento);
        txvfila.setText(fila);
        total=precio*cant_boletos;
        txvfentr.setText("MX $0.00"+" x "+cant_boletos);
        txvseccion.setText("MX $"+df.format(total));
        txvtotal.setText("MX $"+df.format(total));
        if(idevento.equals("0")){
            ideventopack=prefe.getString("ideventopack","0");
            consulta_formas_entrega("https://www.masboletos.mx/appMasboletos/getFormaPagoFormaEntregaPaquete.php?idpaquete="+ideventopack);
        }else{
            consulta_formas_entrega("https://www.masboletos.mx/appMasboletos/getFormaPagoFormaEntrega.php?idevento="+idevento);
        }
    }

    void consulta_formas_entrega(String URL){
        ((DetallesEventos)getActivity()).dialogcarg.show();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        Log.e("URL",URL);
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
                            idtipoentrega= new ArrayList<Integer>();
                            ptecentr= new ArrayList<Double>();
                            costoentrega = new ArrayList<Double>();
                            tipoentre= new ArrayList<String>();
                            txtentre= new ArrayList<String>();
                            String tipo=""; cant_datos=0;
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                tipo=datos.getString("Tipo");
                                if (tipo.equals("FormaEntrega")) {
                                    idtipoentrega.add(Integer.parseInt(datos.getString("idforma")));
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
                        ((DetallesEventos)getActivity()).cerrar_cargando();
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
        btdescentr= new Button[cant_datos];
        cbseguros = new CheckBox[cant_datos];
        lineasep= new View[cant_datos];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RadioGroup.LayoutParams lprg= new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RadioGroup.LayoutParams lprg2= new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rgentregas.setLayoutParams(lp);
        rgentregas.setGravity(Gravity.RIGHT);
        for (int i = 0; i < cant_datos; i++) {
            Log.e("Tipoentreg2",tipoentre.get(i));
            lineasep[i]= new View(getActivity());
            lineasep[i].setBackgroundResource(R.color.grismasclaro);
            lineasep[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3));
            lineasep[i].setId(i);
            if(tipoentre.get(i).equalsIgnoreCase("Will Call")||tipoentre.get(i).equalsIgnoreCase("Boleto Electronico")||tipoentre.get(i).equalsIgnoreCase("Recibe tu boleto en Casa")) {
                rbentregas[i] = new RadioButton(getActivity());
                rbentregas[i].setLayoutParams(lprg);
                rbentregas[i].setButtonTintList(ColorStateList.valueOf(Color.parseColor("#000A3D")));
                String sourceString = tipoentre.get(i)+" - <b>MX $" + costoentrega.get(i) + "</b>";
                rbentregas[i].setText(Html.fromHtml(sourceString));
                rbentregas[i].setTextColor(Color.BLACK);
                rbentregas[i].setId(i);
                btdescentr[i]= new Button(getActivity());
                btdescentr[i].setText("Ver descripción...");
                btdescentr[i].setBackgroundColor(Color.TRANSPARENT);
                btdescentr[i].setTextColor(Color.BLUE);
                btdescentr[i].setLayoutParams(lprg2);
                //btdescentr[i].setGravity(Gravity.END);
                btdescentr[i].setId(i);
                rgentregas.addView(rbentregas[i]);
                rgentregas.addView(btdescentr[i]);
                rgentregas.addView(lineasep[i]);
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
                btdescentr[i]= new Button(getActivity());
                btdescentr[i].setText("Ver descripción...");
                btdescentr[i].setBackgroundColor(Color.TRANSPARENT);
                btdescentr[i].setTextColor(Color.BLUE);
                btdescentr[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                btdescentr[i].setId(i);
                llseguros.setGravity(Gravity.RIGHT);
                llseguros.addView(cbseguros[i]);
                llseguros.addView(btdescentr[i]);
            }
            btdescentr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getActivity(),DescEntregas.class);
                    i.putExtra("descripcionentrega", txtentre.get(v.getId()));
                    i.putExtra("tituloentrega",tipoentre.get(v.getId()));
                    startActivity(i);
                }
            });
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
            set_DatosCompra("idformaentrega", String.valueOf(idtipoentrega.get(j)));
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
                    set_DatosCompra("cargos_servicio", "0.00");
                    set_DatosCompra("cargos_entrega", df.format(sumaseg+Sumafentr));
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
