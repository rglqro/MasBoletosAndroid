package itstam.masboletos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import itstam.masboletos.carruselcompra.DetallesEventos;

public class MyAdapter extends PagerAdapter {

    private ArrayList<String> URLimagenes,IDEvento,nombresEvento,eventosGrupo,listaImagBoton;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context context, ArrayList<String> URLimagenes, ArrayList<String> IDEventos, ArrayList<String> nombresEvento, ArrayList<String> eventosGrupo, ArrayList<String> listaImagBoton) {
        this.context = context;
        this.URLimagenes=URLimagenes;
        this.IDEvento=IDEventos;
        this.nombresEvento=nombresEvento;
        this.eventosGrupo=eventosGrupo;
        this.listaImagBoton=listaImagBoton;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return URLimagenes.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageButton myImage = (ImageButton) myImageLayout.findViewById(R.id.image);
        myImage.setAdjustViewBounds(true);
        myImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.get().load(URLimagenes.get(position)).error(R.mipmap.logo_masboletos).into(myImage);
        view.addView(myImageLayout, 0);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<URLimagenes.size();i++){
                    if(position==i){
                        Intent mainIntent = new Intent().setClass(
                                context, DetallesEventos.class);
                        mainIntent.putExtra("indiceimagen",listaImagBoton.get(i).toString());
                        set_DatosCompra("idevento",IDEvento.get(i).toString());
                        set_DatosCompra("NombreEvento",nombresEvento.get(i).toString());
                        set_DatosCompra("eventogrupo",eventosGrupo.get(i).toString());
                        context.startActivity(mainIntent);
                    }
                }

            }
        });
        //Log.d("URLIMAGEN Adaptador",URLimagenes[position]);
        return myImageLayout;
    }

    public void set_DatosCompra(String ndato,String dato){
        SharedPreferences preferencias=context.getSharedPreferences("DatosCompra", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString(ndato, dato);
        editor.commit();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
