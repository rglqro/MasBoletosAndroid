package itstam.masboletos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class RevisionFR extends Fragment {
    View vista;
    SharedPreferences prefe;
    String seccion,fila,asiento,carfentr,fpago,fentregas,carserv="";
    Double precio=0.0,total=0.00;
    TextView txvprecio,txvcarfentr,txvfila,txvasiento,txvseccion,txvtotal,txvcarserv,txvasientos2,txvfpago,txvfentrega,txvcarfentr2,txvtotal2;
    DecimalFormat df = new DecimalFormat("#.00");
    Button btcontinuar6;
    CheckBox cbdeacuerdo;

    public RevisionFR() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_revision_fr, container, false);
        btcontinuar6=(Button)vista.findViewById(R.id.btContinuar6);
        txvseccion=(TextView)vista.findViewById(R.id.txvSeccionFRev);
        txvasiento=(TextView)vista.findViewById(R.id.txvAsientosFRev);
        txvfila=(TextView)vista.findViewById(R.id.txvfilaFRev);
        txvprecio=(TextView)vista.findViewById(R.id.txvprecioFRev);
        txvcarserv=(TextView)vista.findViewById(R.id.txvcservFRev);
        txvcarfentr=(TextView)vista.findViewById(R.id.txvcfentrFRev);
        txvtotal=(TextView)vista.findViewById(R.id.txvtotalFRev);
        txvasientos2=(TextView)vista.findViewById(R.id.txvasientosRev2);
        txvfpago=(TextView)vista.findViewById(R.id.txvfpagoRev2);
        txvfentrega=(TextView)vista.findViewById(R.id.txventregRev2);
        txvcarfentr2=(TextView)vista.findViewById(R.id.txvcfentrFRev2);
        txvtotal2=(TextView)vista.findViewById(R.id.txvtotalFRev2);
        cbdeacuerdo=(CheckBox)vista.findViewById(R.id.cbdeacuerdo);

        recepcion_datos();

        return vista;
    }

    void recepcion_datos(){
        prefe=getActivity().getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        seccion=prefe.getString("zona","");
        fila=prefe.getString("fila","");
        asiento=prefe.getString("asientos","0");
        precio=Double.parseDouble(prefe.getString("precio","0.00"));
        total=Double.parseDouble(prefe.getString("total",""));
        carserv= (prefe.getString("cargos_servicio","0.00"));
        carfentr=prefe.getString("cargos_entrega","");
        fentregas=prefe.getString("fentrega","");
        fpago=prefe.getString("formapago","");
        txvseccion.setText(seccion);
        txvasiento.setText(asiento);
        txvfila.setText(fila);
        txvprecio.setText("MX $"+df.format(precio)+" x "+asiento);
        txvcarserv.setText(""+carserv);
        txvcarfentr.setText(""+carfentr);
        txvtotal.setText("MX $"+total);
        txvasientos2.setText("ASIENTO(S): "+asiento);
        txvfpago.setText("FORMA DE PAGO: "+fpago);
        txvfentrega.setText("ENTREGA: "+fentregas);
        txvcarfentr2.setText("CARGOS POR FORMA ENTREGA:\n"+carfentr);
        txvtotal2.setText("MX $"+total);
        btcontinuar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbdeacuerdo.isChecked()){
                    ((DetallesEventos)getActivity()).replaceFragment(new UsuarioFR());
                }else{
                    Toast.makeText(getActivity(),"Debes marcar que estás de acuerdo",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
