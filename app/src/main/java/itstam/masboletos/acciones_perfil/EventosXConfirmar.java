package itstam.masboletos.acciones_perfil;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import itstam.masboletos.R;
import uk.co.senab.photoview.PhotoView;

public class EventosXConfirmar extends AppCompatActivity {
    SharedPreferences prefeuser;
    int alto,ancho;
    String idcliente;
    Boolean validasesion;
    ArrayList<String> eventosXconf_list;
    ProgressDialog dialogcarg;
    JSONArray Elementos;
    TableRow fila;
    TableLayout tblevtoXconf,tblevtoXconfEnc;
    TextView[][]txvinfopaq;
    Dialog customDialog;
    String hora="",fecha="",fechahora="",transaccion_oxxo="0",nticket="",foliocontrol="",sucursal="",montopago="",datomal="",msj="";
    Boolean resp=false;
    /*Elementos del Dialog*/
    Button btcerrardialog,btconfirmar;
    ImageButton imbfecha,imbhora;
    EditText edtnticket,edtfolioc,edtfechap,edthorap,edtsucursal,edtmontop;
    TextView txvconf_compra;
    Dialog cdticket;

    DecimalFormat dfrel = new DecimalFormat("#00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_xconfirmar);
        tblevtoXconf=findViewById(R.id.tblevtoXconf);
        tblevtoXconfEnc=findViewById(R.id.tblevtoXconfEnc);
        prefeuser= getSharedPreferences("datos_sesion", Context.MODE_PRIVATE);
        idcliente=prefeuser.getString("id_cliente","0");
        validasesion=prefeuser.getBoolean("validasesion",false);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;
        tblevtoXconf.getLayoutParams().width= (int) (ancho*1.5);
        tblevtoXconfEnc.getLayoutParams().width= (int) (ancho*1.5);
        if(validasesion){
            iniciar_cargando();
            consulta_miseventos("https://www.masboletos.mx/appMasboletos/getVentaConfirmar.php?idcliente="+idcliente);
        }else{
            AlertaBoton("Inicio de Sesión","Debe iniciar sesion para poder ver este contenido");
        }
    }

    void consulta_miseventos(String URL){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Log.e("URL",URL);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                            eventosXconf_list = new ArrayList<>();
                            for (int i = 0; i < Elementos.length(); i++) {
                                JSONObject datos = Elementos.getJSONObject(i);
                                eventosXconf_list.add(datos.getString("fechaevento")+","+datos.getString("cantidad")+","+datos.getString("evento")+","+datos.getString("estatus")+
                                        ","+datos.getString("transaccion")+","+datos.getString("hora")+","+datos.getString("fechaventa")+
                                        ","+datos.getString("TipoPago")+","+datos.getString("formaentrega"));
                            }
                            cerrar_cargando();
                            pinta_eventos();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Snackbar.make(findViewById(R.id.llevtoXconf),"Error...",Snackbar.LENGTH_LONG).show();
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }
    void pinta_eventos(){
        if(tblevtoXconf!=null)
            tblevtoXconf.removeAllViews();
        txvinfopaq= new TextView[eventosXconf_list.size()][10];
        int j;
        for (int i=0;i<eventosXconf_list.size();i++) {
            fila = new TableRow(this);
            j = 0;
            if (i % 2 == 0)
                fila.setBackgroundResource(R.color.grismasclaro);
            fila.setGravity(Gravity.CENTER);
            String [] parts=eventosXconf_list.get(i).split(",");
            creadortxv(i, j, "Confirmar");
            j++;
            creadortxv(i, j, parts[4]);
            j++;
            creadortxv(i, j, parts[1]);
            j++;
            creadortxv(i, j, parts[2]);
            j++;
            creadortxv(i, j, parts[0]);
            j++;
            creadortxv(i, j, parts[5]);
            j++;
            creadortxv(i, j, parts[6]);
            j++;
            creadortxv(i, j, parts[3]);
            j++;
            creadortxv(i, j, parts[7]);
            j++;
            creadortxv(i, j, parts[8]);
            tblevtoXconf.addView(fila);
        }
    }

    void creadortxv(int i,int j,String txt){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        txvinfopaq[i][j]=new TextView(this);
        txvinfopaq[i][j].setText(txt);
        txvinfopaq[i][j].setId(i);
        txvinfopaq[i][j].setTextColor(Color.BLACK);
        txvinfopaq[i][j].setLayoutParams(lp);
        if(j==0) {
            txvinfopaq[i][j].setTextColor(ContextCompat.getColor(this, R.color.azul_link));
            txvinfopaq[i][j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    transaccion_oxxo=txvinfopaq[view.getId()][1].getText().toString();
                    ver_confirmar();
                }
            });
        }
        txvinfopaq[i][j].setGravity(Gravity.CENTER);
        txvinfopaq[i][j].setPadding(20,0,0,0);
        fila.addView(txvinfopaq[i][j]);
    }

    void ver_confirmar(){
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialogo_evtoxconf);
        customDialog.show();
        customDialog.setCancelable(false);
        Window window = customDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        txvconf_compra=customDialog.findViewById(R.id.txvconf_compra);
        btcerrardialog=customDialog.findViewById(R.id.btcerrardialconf);
        btconfirmar=customDialog.findViewById(R.id.btconf_evto);
        edtnticket=customDialog.findViewById(R.id.edtnticket);
        edtfolioc=customDialog.findViewById(R.id.edtfolioct);
        edtfechap=customDialog.findViewById(R.id.edtfecha);
        edthorap=customDialog.findViewById(R.id.edthora);
        edtsucursal=customDialog.findViewById(R.id.edtsucursal);
        edtmontop=customDialog.findViewById(R.id.edtmontopago);
        imbfecha=customDialog.findViewById(R.id.imbfecha);
        imbhora=customDialog.findViewById(R.id.imbhora);

        txvconf_compra.setText("Confirma tu compra con #"+transaccion_oxxo+" de trasacción");

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MMM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                String myFormat2 = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.getDefault());

                edtfechap.setText(sdf.format(myCalendar.getTime()));
                fecha=sdf2.format(myCalendar.getTime());
            }

        };

        imbfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EventosXConfirmar.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        imbhora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EventosXConfirmar.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        edthorap.setText(dfrel.format(selectedHour) +":"+ dfrel.format(selectedMinute));
                        hora= String.valueOf(dfrel.format(selectedHour) +":"+ dfrel.format(selectedMinute));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Selecciona la hora");
                mTimePicker.show();
            }
        });

        btcerrardialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.cancel();
            }
        });
        btconfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fechahora=fecha+" "+hora+":00";
                nticket=edtnticket.getText().toString().trim();
                foliocontrol=edtfolioc.getText().toString().trim();
                sucursal=edtsucursal.getText().toString().trim();
                montopago=edtmontop.getText().toString().trim();
                if(validacion_campo())
                    mandar_confirmacion("https://www.masboletos.mx/appMasboletos/getActualizaventasOxxo.php?" +
                            "transaccion="+transaccion_oxxo+"&numticket="+nticket+"&folio="+foliocontrol+
                            "&fechahorapago="+fechahora+"&sucursal="+sucursal+"&montopago="+montopago+"&idcliente="+idcliente);
                else
                    Toast.makeText(getApplicationContext(),datomal,Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validacion_campo(){
        boolean val=true;
        if(nticket.length()<=1) {
            datomal="Verifique su número de ticket de pago";
            val=false;
        }else if(foliocontrol.length()<=1) {
            datomal="Verifique su folio de control";
            val=false;
        }else if(fecha.length()<=1) {
            datomal="Seleccione una fecha";
            val=false;
        }else if(hora.length()<=1) {
            datomal="Seleccione una hora";
            val=false;
        }else if(sucursal.length()<=1) {
            datomal="Verifique su Sucursal";
            val=false;
        }else if(montopago.length()<=1) {
            datomal="Verifique su monto de pago";
            val=false;
        }

        return val;
    }

    void mandar_confirmacion(String url){
        iniciar_cargando();
        // Instantiate the RequestQueue.
        url.replace(" ","%20");
        //Log.e("url",url);
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Resultado actualizacion",response);
                        try {
                            Elementos = new JSONArray(response);
                            for (int i=0;i<Elementos.length();i++){
                                JSONObject datos = Elementos.getJSONObject(i);
                                resp=datos.getBoolean("respuesta");
                                msj=datos.getString("mensaje");
                            }
                            if(resp){
                                AlertaBoton("Confirmación","Tu movimiento ha sido aceptado ya puedes pasar a nuestros puntos de venta para recoger el boleto con el número de transacción "+transaccion_oxxo+", de igual forma se te hizo llegar un correo a tu cuenta, indicando el número de transacción.");
                                consulta_miseventos("https://www.masboletos.mx/appMasboletos/getVentaConfirmar.php?idcliente="+idcliente);
                                customDialog.cancel();
                            }else{
                                Toast.makeText(getApplicationContext(),msj+"\nVerifica tus datos",Toast.LENGTH_LONG).show();
                            }
                            cerrar_cargando();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cerrar_cargando();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cerrar_cargando();
                Toast.makeText(getApplicationContext(),"Ha ocurrido un error al enviar la información, intente de nuevo o favor de comunicarse a MasBoletos",Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void AlertaBoton(String titu,String msj){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titu);
        builder.setMessage(msj);

        // add the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(validasesion)
                    dialogInterface.dismiss();
                else
                    finish();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void abrir_ticket_ej(View v){
        cdticket = new Dialog(this);
        //deshabilitamos el título por defecto
        cdticket.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        //establecemos el contenido de nuestro dialog
        cdticket.setContentView(R.layout.dialogo_ticket_oxxo);
        ImageButton imbcerrar_to=cdticket.findViewById(R.id.imbcerrar_to);
        PhotoView photoView = cdticket.findViewById(R.id.phvtoxxo);
        photoView.setAdjustViewBounds(true);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        cdticket.show();
        Window window = cdticket.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imbcerrar_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdticket.cancel();
            }
        });
        Toast.makeText(getApplicationContext(),"Puedas hacer zoom en la imagen",Toast.LENGTH_SHORT).show();
    }

    public void iniciar_cargando(){
        dialogcarg= new ProgressDialog(this,R.style.ProgressDialogStyle);
        dialogcarg.setTitle("Cargando información");
        dialogcarg.setMessage("  Espere...");
        dialogcarg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialogcarg.setCancelable(false);
        dialogcarg.show();
    }

    public void cerrar_cargando(){
        dialogcarg.dismiss();
    }

    public void regresar(View view) {
        finish();
    }
}
