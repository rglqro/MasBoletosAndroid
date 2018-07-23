package itstam.masboletos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Layout;
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
    public FRMejDisp() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_frmej_disp, container, false);
        TXVSeccionComp=(TextView) vista.findViewById(R.id.txvSeccion);
        TXVAsientos=(TextView)vista.findViewById(R.id.txvAsientos);
        TXVInfoCompra=(TextView)vista.findViewById(R.id.txvInfo);
        TXVTotal=(TextView)vista.findViewById(R.id.txvTotal);
        btComprar=(Button)vista.findViewById(R.id.btComprar);
        RecibirDatos();
        return vista;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void RecibirDatos(){
        ((DetallesEventos)getActivity()).cerrar_cargando();
        SharedPreferences prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        Cant_Boletos=Integer.parseInt(prefe.getString("Cant_boletos","0"));
        comision=Double.parseDouble(prefe.getString("comision","0.00"));
        zona=prefe.getString("zona","");
        llenar_info();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void llenar_info(){
        TXVSeccionComp.setText(zona);
        subtotal=Cant_Boletos*precio;
        subtotal+=comision*Cant_Boletos;
        cargoTC=subtotal*0.03;
        TXVAsientos.setText(String.valueOf(Cant_Boletos));
                String TxTotal="$"+String.valueOf(df.format(subtotal));
        TXVInfoCompra.setText("$"+precio+" x "+Cant_Boletos);
        TXVTotal.setText(TxTotal);
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetallesEventos)getActivity()).replaceFragment(new FPagoFR());
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
        try {

        }catch (Exception e){}
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
