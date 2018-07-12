package itstam.masboletos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends PagerAdapter {

    private ArrayList<String> URLimagenes,listaImagBoton,IDEvento;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context context, ArrayList<String> URLimagenes, ArrayList<String> listaImagBoton, ArrayList<String> IDEventos) {
        this.context = context;
        this.URLimagenes=URLimagenes;
        this.listaImagBoton=listaImagBoton;
        this.IDEvento=IDEventos;
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
        Picasso.get()
                .load(URLimagenes.get(position))
                .error(R.drawable.ic_inicio)
                .into(myImage);
        view.addView(myImageLayout, 0);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<URLimagenes.size();i++){
                    if(position==i){
                        Intent mainIntent = new Intent().setClass(
                                context, DetallesEventos.class);
                        mainIntent.putExtra("indiceimagen",listaImagBoton.get(i).toString());
                        mainIntent.putExtra("idevento",IDEvento.get(i).toString());
                        context.startActivity(mainIntent);
                    }
                }

            }
        });
        //Log.d("URLIMAGEN Adaptador",URLimagenes[position]);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
