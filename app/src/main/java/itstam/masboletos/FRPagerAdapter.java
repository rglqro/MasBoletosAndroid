package itstam.masboletos;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;


public class FRPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs; Context context;
    String nombreEvento, eventogrupo;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public FRPagerAdapter(FragmentManager fm, int NumOfTabs, Context context, String nombreEvento, String eventogrupo) {
        super(fm);
        this.context=context;
        this.eventogrupo=eventogrupo;
        this.nombreEvento=nombreEvento;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ComprarBoletoFr comprarBoletoFr = new ComprarBoletoFr();
                return comprarBoletoFr;
            case 1:
                SeleccionZonaFR infoFragment= new SeleccionZonaFR();
                return infoFragment;
            case 2:
                FRMejDisp frMejDisp = new FRMejDisp();
                return frMejDisp;
            case 3:
                FPagoFR fPagoFR = new FPagoFR();
                return fPagoFR;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Compra tus Boletos";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

}
