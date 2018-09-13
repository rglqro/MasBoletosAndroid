package itstam.masboletos.carruselcompra;


import android.content.Context;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FRFinalizarCompra extends Fragment {

    TextView txvtitulofc,txvmsjfinal,txvntran,txvfolios;
    View vista;
    String titulo,msjfinal,ntransac,folios,fpago,idevento,idfpago;
    SharedPreferences prefe;
    Button btseguir;
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
        txvmsjfinal=(TextView)vista.findViewById(R.id.txvmensajefnal);
        txvntran=(TextView)vista.findViewById(R.id.txvntran);
        txvfolios=(TextView)vista.findViewById(R.id.txvfolios);
        btseguir=(Button)vista.findViewById(R.id.btseguir);
        imvqrcode=vista.findViewById(R.id.imvqrcode);
        myWebView = (WebView) vista.findViewById(R.id.wb1);
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
            msjfinal = "<br>" +
                    "GRACIAS POR HACER USOS DE NUESTROS SERVICIOS <br><br><br>" +
                    "<a href=\"https://www.masboletos.mx\">masboletos.mx</a>";
            txvtitulofc.setText(Html.fromHtml(titulo));
            txvmsjfinal.setText(Html.fromHtml(msjfinal));
            txvmsjfinal.setClickable(true);
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
    }

    void consulta_folios(){
        Thread tr=new Thread(){
            @Override
            public void run() {
                final String resultado = inserta("https://www.masboletos.mx/appMasboletos/getFoliosxTransaccion.php?transaccion="+ntransac);  //para que la variable sea reconocida en todos los metodos
                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        int r = validadatos(resultado); // checa si la pagina devolvio algo
                        if (r>0) {
                            Log.e("Resultado actualizacion",resultado);
                            folios=resultado;
                            txvntran.setText(ntransac);
                            txvfolios.setText(folios);
                        }
                    }});  //permite trabajar con la interfaz grafica
            }};
        tr.start();
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

}
