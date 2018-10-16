package itstam.masboletos.acciones_perfil;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import itstam.masboletos.R;

public class BoletoElectronico extends AppCompatActivity {

    String ntransaccion;
    ProgressDialog dialogcarg; JSONArray Elementos=null;
    String evento,fecha,hora,folios="";
    TextView txvevento,txvfecha,txvhora,txvfolios;
    ImageView imvbolele;
    int alto, ancho;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boleto_electronico);

        txvevento=findViewById(R.id.txvneventobe);
        txvfecha=findViewById(R.id.txvfechabe);
        txvhora=findViewById(R.id.txvhorabe);
        txvfolios=findViewById(R.id.txvboletosbe);
        imvbolele=findViewById(R.id.imvbolele_be);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        alto = displayMetrics.heightPixels;
        ancho = displayMetrics.widthPixels;

        ntransaccion=getIntent().getStringExtra("transaccion");
        consulta_boleto_electronico("https://www.masboletos.mx/appMasboletos.fueralinea/getInfoPorTransaccion.php?transaccion="+ntransaccion);
    }

    void consulta_boleto_electronico(String url){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.e("URL",url);
        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Respuesta Json",response.toString());
                        try {
                            Elementos = response;
                                for (int i = 0; i < Elementos.length(); i++) {
                                    JSONObject datos = Elementos.getJSONObject(i);
                                    evento=datos.getString("evento");
                                    fecha=datos.getString("fecha");
                                    hora=datos.getString("hora");
                                    folios+=datos.getString("folio");
                                    if(i<Elementos.length()-1){
                                        folios+=",";
                                    }
                                }
                                JSONObject datos2= new JSONObject(fecha);
                                fecha=datos2.getString("date");
                                String[] parts = fecha.split(" "); fecha=parts[0];
                                Log.e("fecha",fecha);
                                pintar_detalles_boleto();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        cerrar_cargando();
                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    void pintar_detalles_boleto(){
        txvevento.setText(evento);
        txvfecha.setText(fecha);
        txvhora.setText(hora);
        txvfolios.setText(folios);

        MultiFormatWriter mfwQR = new MultiFormatWriter();
        BitMatrix bmtxQR = null;
        try {
            bmtxQR = mfwQR.encode(ntransaccion, BarcodeFormat.QR_CODE, (int) (ancho/1.5),(int) (ancho/1.5));
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmapqr= barcodeEncoder.createBitmap(bmtxQR);
            imvbolele.setImageBitmap(bitmapqr);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    public void regresar(View view) {
        finish();
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
}
