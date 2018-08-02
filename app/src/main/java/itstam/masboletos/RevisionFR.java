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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class RevisionFR extends Fragment {
    View vista;
    SharedPreferences prefe;
    String seccion,fila,asiento,carfentr,fpago,fentregas,carserv="",cant_boletos,datoscargos;
    Double precio=0.0,total=0.00,ptecargo=0.00,imtecargo=0.00, CargosServ=0.00;
    TextView txvnevento,txvfechaeve,txvhoraeve,txvfila,txvasiento,txvseccion,txvcarserv,txvfpago,txvfentrega,txvcarfentr2,txvtotal2,txvseccion2;
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
        txvnevento=(TextView)vista.findViewById(R.id.txvnevento);
        txvfechaeve=(TextView)vista.findViewById(R.id.txvfechaeve);
        txvhoraeve=(TextView)vista.findViewById(R.id.txvhoraeve);
        txvseccion2=(TextView)vista.findViewById(R.id.txvSeccionFRev2);
        txvseccion=(TextView)vista.findViewById(R.id.txvSeccionFRev);
        txvasiento=(TextView)vista.findViewById(R.id.txvAsientosFRev);
        txvfila=(TextView)vista.findViewById(R.id.txvfilaFRev);
        txvcarserv=(TextView)vista.findViewById(R.id.txvcservFRev);
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
        datoscargos=prefe.getString("datoscargos","");
        String[] datocargo = datoscargos.split(",");
        ptecargo=Double.parseDouble(datocargo[1]);
        imtecargo=Double.parseDouble(datocargo[2]);
        carserv= (prefe.getString("cargos_servicio","0.00"));
        carfentr=prefe.getString("cargos_entrega","");
        fentregas=prefe.getString("fentrega","");
        fpago=prefe.getString("formapago","");
        cant_boletos=prefe.getString("Cant_boletos","0");

        txvnevento.setText(prefe.getString("NombreEvento",""));
        txvseccion2.setText(seccion+" x "+cant_boletos);
        txvseccion.setText("MX $"+df.format(precio*Integer.parseInt(cant_boletos)));
        CargosServ=((precio*(ptecargo/100))+imtecargo);
        total+=CargosServ*Integer.parseInt(cant_boletos);

        txvasiento.setText(asiento);
        txvfila.setText(fila);
        txvcarserv.setText("MX $"+df.format(CargosServ)+" x "+cant_boletos);
        txvfpago.setText(""+fpago);
        txvfentrega.setText(""+fentregas);
        txvcarfentr2.setText("MX $"+carfentr+" x "+cant_boletos);
        txvtotal2.setText("MX $"+df.format(total));
        btcontinuar6.setBackgroundResource(R.color.gris2);
        cbdeacuerdo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btcontinuar6.setBackgroundResource(R.color.verdemb);
                }else{
                    btcontinuar6.setBackgroundResource(R.color.gris2);
                }
            }
        });
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
