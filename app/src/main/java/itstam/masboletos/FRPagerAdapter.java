package itstam.masboletos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class FRPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs; Context context; FragmentTransaction ft;
    String nombreEvento, eventogrupo, idevento;

    public FRPagerAdapter(FragmentManager fm, int NumOfTabs, Context context, String nombreEvento, String eventogrupo, String idevento) {
        super(fm);
        this.context=context;
        this.eventogrupo=eventogrupo;
        this.nombreEvento=nombreEvento;
        this.idevento=idevento;
        ft=fm.beginTransaction();
        ft.commit();
        fm.beginTransaction().commit();
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putString("nombreEvento", nombreEvento);
        bundle.putString("eventogrupo",eventogrupo);
        bundle.putString("idevento",idevento);
        switch (position) {
            case 0:
                ComprarBoletoFr comprarBoletoFr = new ComprarBoletoFr();
                comprarBoletoFr.setArguments(bundle);
                return comprarBoletoFr;
            case 1:
                InfoFragment infoFragment= new InfoFragment();
                infoFragment.setArguments(bundle);
                return infoFragment;
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
        }
        return null;
    }
}
