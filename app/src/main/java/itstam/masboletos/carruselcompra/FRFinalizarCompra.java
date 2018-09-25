package itstam.masboletos.carruselcompra;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import itstam.masboletos.R;
import itstam.masboletos.UbicacionAct;
import itstam.masboletos.acciones_perfil.MisEventos;


/**
 * A simple {@link Fragment} subclass.
 */
public class FRFinalizarCompra extends Fragment {

    TextView txvtitulofc,txvntran,txvfolios,txvpuntosventa;
    View vista;
    String titulo,ntransac,folios,fpago,idevento,idfpago;
    SharedPreferences prefe;
    Button btseguir,btmiseventos;
    WebView myWebView;
    ImageView imvqrcode;
    LinearLayout llqr,llinfofinal;


    public FRFinalizarCompra() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_frfinalizar_compra, container, false);
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        txvtitulofc=(TextView)vista.findViewById(R.id.txvtitulofc);
        llinfofinal=vista.findViewById(R.id.rlinfofinal);
        txvntran=(TextView)vista.findViewById(R.id.txvntran);
        txvfolios=(TextView)vista.findViewById(R.id.txvfolios);
        btseguir=(Button)vista.findViewById(R.id.btseguir);
        imvqrcode=vista.findViewById(R.id.imvqrcode);
        myWebView = (WebView) vista.findViewById(R.id.wb1);
        btmiseventos=vista.findViewById(R.id.btmisboletosfincompra);
        txvpuntosventa=vista.findViewById(R.id.txvpuntoventa);
        llqr=vista.findViewById(R.id.llqro); llqr.setVisibility(View.GONE);

        recibir_datos();
        return vista;
    }

    void recibir_datos(){
        titulo="<b>"+prefe.getString("nombreuser","")+"</b>, SU COMPRA SE HA REALIZADO CORRECTAMENTE";
        ntransac=prefe.getString("foliocompra","");
        fpago=prefe.getString("idformapago","");
        idevento=prefe.getString("idevento","0");
        idfpago=prefe.getString("idformaentrega","0");
        if(fpago.equals("5")) {
            txvtitulofc.setText(Html.fromHtml(titulo));
            if(idfpago.equals("2")){
                llqr.setVisibility(View.VISIBLE);
                MultiFormatWriter mfwQR = new MultiFormatWriter();
                BitMatrix bmtxQR = null;
                try {
                    bmtxQR = mfwQR.encode(ntransac, BarcodeFormat.QR_CODE,400,400);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmapqr= barcodeEncoder.createBitmap(bmtxQR);
                    imvqrcode.setImageBitmap(bitmapqr);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            consulta_folios();
        }else if(fpago.equals("2")||fpago.equals("3")){
            llinfofinal.setVisibility(View.GONE);
            if(idevento.equals("0")){
                myWebView.loadData(prefe.getString("URLTC", "www.google.com.mx"),"text/html","BASE64");
            }else {
                myWebView.loadUrl(prefe.getString("URLTC", "www.google.com.mx"));
            }
            myWebView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
        }
        btseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DetallesEventos)getActivity()).finish();
            }
        });
        btmiseventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), MisEventos.class);
                startActivity(mainIntent);
                ((DetallesEventos)getActivity()).finish();
            }
        });
        txvpuntosventa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), UbicacionAct.class);
                startActivity(mainIntent);
                ((DetallesEventos)getActivity()).finish();
            }
        });
    }

    void consulta_folios(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="https://www.masboletos.mx/appMasboletos/getFoliosxTransaccion.php?transaccion="+ntransac;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Resultado actualizacion",response);
                        folios=response; folios=folios.replace(" ","").replace("\n","");
                        txvntran.setText(ntransac);
                        txvfolios.setText(folios);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
