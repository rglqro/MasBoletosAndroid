package itstam.masboletos.carruselcompra;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import itstam.masboletos.R;
import itstam.masboletos.UbicacionAct;
import itstam.masboletos.acciones_perfil.MisEventos;


/**
 * A simple {@link Fragment} subclass.
 */
public class FRFinalizarCompra extends Fragment {

    TextView txvtitulofc,txvntran,txvfolios,txvpuntosventa,txvidoper,txv_codbarra,txvmonto,txvcantbol,txvnevento;
    View vista;
    String titulo,ntransac,folios,fpago,idevento,idfpago,nevento,idoper,num_cod;
    SharedPreferences prefe;
    Button btseguir,btmiseventos,btcaptura;
    WebView myWebView;
    ImageView imvqrcode,imvcod_barra,imv_logooxxo;
    LinearLayout llqr,llinfofinal,lloxxo;
    int alto,ancho;
    NestedScrollView nscfrfin_compra;


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
        imvcod_barra=vista.findViewById(R.id.imvcod_barra);
        llqr=vista.findViewById(R.id.llqro); llqr.setVisibility(View.GONE);
        lloxxo=vista.findViewById(R.id.lloxxo);
        imv_logooxxo=vista.findViewById(R.id.imvlogooxxo);
        txvidoper=vista.findViewById(R.id.txvidop);
        alto=((DetallesEventos)getActivity()).alto;
        ancho=((DetallesEventos)getActivity()).ancho;
        txv_codbarra=vista.findViewById(R.id.txvcodbarra);
        txvmonto=vista.findViewById(R.id.txvmontot);
        txvcantbol=vista.findViewById(R.id.txvCantBolFC);
        txvnevento=vista.findViewById(R.id.txvneventoFC);
        btcaptura=vista.findViewById(R.id.btcaptura);
        nscfrfin_compra=vista.findViewById(R.id.nscFRFinalizarCompra);
        recibir_datos();
        return vista;
    }

    void recibir_datos(){
        titulo="<b>"+prefe.getString("nombreuser","")+"</b>, SU COMPRA SE HA REALIZADO CORRECTAMENTE";
        ntransac=prefe.getString("foliocompra","");
        fpago=prefe.getString("idformapago","");
        idevento=prefe.getString("idevento","0");
        idfpago=prefe.getString("idformaentrega","0");
        nevento=prefe.getString("NombreEvento","");
        if(fpago.equals("5")) {/*si el pago se realizó con paypal se muestra la interfaz de compra finalizada con su transacion y folios de los boletos*/
            txvtitulofc.setText(Html.fromHtml(titulo));
            lloxxo.setVisibility(View.GONE);
            if(idfpago.equals("2")){/*si el id de fentrega es 2 significa que se pagó por boleto electronico lo que procede que se genere un codigo QR*/
                llqr.setVisibility(View.VISIBLE);
                MultiFormatWriter mfwQR = new MultiFormatWriter();
                BitMatrix bmtxQR = null;
                try {
                    bmtxQR = mfwQR.encode(ntransac, BarcodeFormat.QR_CODE, (int) (ancho/1.5),(int) (ancho/1.5));
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmapqr= barcodeEncoder.createBitmap(bmtxQR);
                    imvqrcode.setImageBitmap(bitmapqr);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
            consulta_folios();
        }else if(fpago.equals("2")||fpago.equals("3")){/*si el id de forma de pago es 2 o 3 se procederá a abrir el url que se generó en el fragmento anterior para realizar el pago via web*/
            llinfofinal.setVisibility(View.GONE);
            lloxxo.setVisibility(View.GONE);
            myWebView.setWebChromeClient(new WebChromeClient());
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.setVerticalScrollBarEnabled(true);
            myWebView.setHorizontalScrollBarEnabled(true);
            if(idevento.equals("0")){
                myWebView.loadData(prefe.getString("URLTC", "www.google.com.mx"),"text/html","BASE64");
            }else {
                myWebView.loadData(prefe.getString("URLTC", "www.google.com.mx"),"text/html","BASE64");
            }
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setBuiltInZoomControls(true);
            myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            myWebView.setScrollbarFadingEnabled(false);
        }else if(fpago.equals("4")){
            llinfofinal.setVisibility(View.GONE);
            String[] parts =prefe.getString("URLTC","").replace(" ","").replace("\n","").replaceAll("\\s+","").split(",");
            idoper=parts[0];
            num_cod=parts[1];
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(num_cod, BarcodeFormat.CODE_128,ancho,alto/6);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imvcod_barra.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            txv_codbarra.setText(num_cod);
            txvidoper.setText("ID de operación: "+idoper);
            imv_logooxxo.getLayoutParams().width= (int) (ancho/1.8);
            txvmonto.setText("$"+((DetallesEventos)getActivity()).df.format(Double.parseDouble(parts[2])));
            txvcantbol.setText(parts[3]);
            txvnevento.setText(nevento);
            ((DetallesEventos)getActivity()).mover_alfondo();
        }
        btseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        btmiseventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), MisEventos.class);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });
        txvpuntosventa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent().setClass(getActivity(), UbicacionAct.class);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });

        btcaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = getBitmapFromView(nscfrfin_compra, nscfrfin_compra.getChildAt(0).getHeight(), nscfrfin_compra.getChildAt(0).getWidth());
                storeImage(bitmap);
                ((DetallesEventos)getActivity()).AlertaBoton("Captura de Pantalla","Tu Captura de Pantalla ha sido guardada, busca la carpeta de 'MasBoletos' en tu galería o en la carpeta de 'Descargas'").show();
            }
        });
    }

    private Bitmap getBitmapFromView(View view, int height, int width) {
        btcaptura.setEnabled(false);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("error",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            btcaptura.setEnabled(true);
        } catch (FileNotFoundException e) {
            Log.d("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error", "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/"
                + Environment.DIRECTORY_DOWNLOADS
                + "/MasBoletos");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        /*String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());*/
        File mediaFile;
        String mImageName="OXXO_REF_"+ num_cod +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    void consulta_folios(){/*Este metodo consulta los folios de los boletos que fueron comprados con el folio de transaccion*/
        // Instantiate the RequestQueue.
        ((DetallesEventos)getActivity()).iniciar_cargando();
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
                        ((DetallesEventos)getActivity()).cerrar_cargando();
                        ((DetallesEventos)getActivity()).mover_alfondo();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ((DetallesEventos)getActivity()).cerrar_cargando();
                Snackbar.make(vista,"Error...",Snackbar.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
