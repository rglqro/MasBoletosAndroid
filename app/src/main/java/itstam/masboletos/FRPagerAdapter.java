package itstam.masboletos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class FRPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs; Context context; FragmentTransaction ft;
    String nombreEvento, eventogrupo;

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
                return "Información";
            case 2:
                return "Mejor Disponible";
        }
        return null;
    }

}
