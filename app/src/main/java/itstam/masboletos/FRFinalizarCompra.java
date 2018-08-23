package itstam.masboletos;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FRFinalizarCompra extends Fragment {

    TextView txvtitulofc,txvmsjfinal;
    View vista;
    String titulo,msjfinal;
    SharedPreferences prefe;
    Button btseguir;

    public FRFinalizarCompra() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista= inflater.inflate(R.layout.fragment_frfinalizar_compra, container, false);
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        txvtitulofc=(TextView)vista.findViewById(R.id.txvtitulofc);
        txvmsjfinal=(TextView)vista.findViewById(R.id.txvmensajefnal);
        btseguir=(Button)vista.findViewById(R.id.btseguir);
        recibir_datos();
        return vista;
    }

    void recibir_datos(){
        titulo="<b>"+prefe.getString("nombreuser","")+"</b>, SU APARATDO SE HA REALIZADO CORRECTAMENTE";

        msjfinal="EL NÚMERO DE REFERENCIA DE SU TRANSACCIÓN ES: <b>"+prefe.getString("foliocompra","")+"</b> <br>" +
                "ACUDA A CUALQUIERA DE NUESTROS PUNTOS DE VENTA CON SU No. DE APARTADO Y UNA IDENTIFICACIÓN OFICIAL <br><br>" +
                "<b>ACUDA POR SUS BOLETOS A LOS PUNTOS DE VENTA DE MAS BOLETOS <br> O BIEN EN TAQUILLAS, 2 HORAS ANTES DEL EVENTO</b> <br><br>" +
                "Sus folios son: <b>1743699,1743700</b><br><br>" +
                "GRACIAS POR HACER USOS DE NUESTROS SERVICIOS <br><br><br><br>" +
                "<a href=\"https://www.masboletos.mx\">masboletos.mx</a>";
        txvtitulofc.setText(Html.fromHtml(titulo));
        txvmsjfinal.setText(Html.fromHtml(msjfinal));
        txvmsjfinal.setClickable(true);
        btseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DetallesEventos)getActivity()).finish();
            }
        });
    }

}
