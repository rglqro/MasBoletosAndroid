package itstam.masboletos;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class FRPagerAdOrder extends FragmentPagerAdapter {
    int mNumOfTabs; Context context; FragmentTransaction ft;

    public FRPagerAdOrder(FragmentManager fm, int tabCount, Context applicationContext) {
        super(fm);
        ft=fm.beginTransaction();
        ft.commit();
        fm.beginTransaction().commit();
        this.mNumOfTabs = tabCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Forma de Pago";
            case 1:
                return "Forma de Entrega";
            case 2:
                return "Revisión";
            case 3:
                return "Usuario";
            case 4:
                return "Cuenta";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FPagoFR fPagoFR = new FPagoFR();
                return fPagoFR;
            case 1:
                FEntregaFr fEntregaFr=new FEntregaFr();
                return fEntregaFr;
            case 2:
                RevisionFR revisionFR=new RevisionFR();
                return revisionFR;
            case 3:
                UsuarioFR usuarioFR=new UsuarioFR();
                return usuarioFR;
            case 4:
                CuentaFR cuentaFR=new CuentaFR();
                return cuentaFR;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
