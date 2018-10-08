package itstam.masboletos.acciones_perfil;



import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.LinearLayout;

import itstam.masboletos.R;

public class acercade extends AppCompatActivity {
    Dialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acercade);
    }

    public void regresar(View view){
        finish();
    }

    public void avisopriv(View v){
        // cuadro de dialogo que se abre si no se envio ningun sms o no hubo respuesta del servidor para ingresar el numero de celular manualmente
                // con este tema personalizado evitamos los bordes por defecto
                customDialog = new Dialog(this);
        //deshabilitamos el título por defecto
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //obligamos al usuario a pulsar los botones para cerrarlo
        //establecemos el contenido de nuestro dialog
        customDialog.setContentView(R.layout.dialog_aviso_priv);
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WebView wvavispriv= customDialog.findViewById(R.id.wvavisopriv);
        wvavispriv.getSettings().setJavaScriptEnabled(true);
        wvavispriv.loadUrl("https://www.masboletos.mx/politicascompra.php");
        customDialog.show();
        Window window = customDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


}
