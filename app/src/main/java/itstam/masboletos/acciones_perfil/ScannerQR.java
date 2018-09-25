package itstam.masboletos.acciones_perfil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import itstam.masboletos.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_qr);
        mScannerView= new ZXingScannerView(this);

        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this,"Resultado escanner: "+result.getText(),Toast.LENGTH_SHORT).show();
        //mScannerView.resumeCameraPreview(this);
    }

    void validarBoleto(){

    }
}
