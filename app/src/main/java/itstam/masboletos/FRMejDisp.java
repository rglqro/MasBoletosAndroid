package itstam.masboletos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class FRMejDisp extends Fragment {

    Double precio, subtotal, Total,comision, cargoTC;
    int Cant_Boletos;
    String zona;
    View vista;
    TextView TXVSeccionComp,TXVAsientos,TXVInfoCompra,TXVTotal;
    Button btComprar;
    DecimalFormat df = new DecimalFormat("#.00");
    Continuar_compra continuar_compra;
    public FRMejDisp() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_frmej_disp, container, false);
        TXVSeccionComp=(TextView) vista.findViewById(R.id.txvSeccion);
        TXVAsientos=(TextView)vista.findViewById(R.id.txvAsientos);
        TXVInfoCompra=(TextView)vista.findViewById(R.id.txvInfo);
        TXVTotal=(TextView)vista.findViewById(R.id.txvTotal);
        btComprar=(Button)vista.findViewById(R.id.btComprar);
        return vista;
    }

    void RecibirDatos(){
        Toast.makeText(getActivity(),"Mejor Disponible Abierto",Toast.LENGTH_SHORT).show();
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        Cant_Boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        comision=Double.parseDouble(prefe.getString("comision","0.00"));
        zona=prefe.getString("zona","");
        llenar_info();
    }

    void llenar_info(){
        TXVSeccionComp.setText("Sección: "+zona+" , $ "+precio+" c/u");
        subtotal=Cant_Boletos*precio;
        subtotal+=comision*Cant_Boletos;
        cargoTC=subtotal*0.03;
        TXVAsientos.setText("Asientos: "+Cant_Boletos);
                String TxTotal="Total: $"+String.valueOf(df.format(subtotal+cargoTC));
        TXVInfoCompra.setText("PRECIO: $"+precio+" x "+Cant_Boletos+"\n"
                +"CARGOS POR SERVICIO: $"+comision+" x "+Cant_Boletos+"\n"
                +"SUBTOTAL: $"+df.format(subtotal)+"\n"
                +"CARGO POR TARJETA DE CREDITO: $"+String.valueOf(df.format(cargoTC)));
        TXVTotal.setText(TxTotal);
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continuar_compra.seguir_compra();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        try {
            continuar_compra=(Continuar_compra)context;
        }catch (Exception e){}
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface Continuar_compra{
        void seguir_compra();
    }

}
