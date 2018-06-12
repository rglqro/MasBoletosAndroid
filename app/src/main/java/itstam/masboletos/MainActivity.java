package itstam.masboletos;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BoletosPrin.OnFragmentInteractionListener, Perfil_Fr.OnFragmentInteractionListener{
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    BottomNavigationView menunavegacion;

    BoletosPrin frboletos;
    Perfil_Fr frperfil;

   /* DrawerLayout drawerLayout;
    LinearLayout LYmenu;
    ListView ListaMenu;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniciar_menu_lateral();
        frboletos = new BoletosPrin();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedor,frboletos).commit();

        menunavegacion=(BottomNavigationView)findViewById(R.id.menu_navegacion);

        menunavegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_inicio:
                        frboletos = new BoletosPrin();
                        FragmentTransaction trans1= getSupportFragmentManager().beginTransaction();
                        trans1.replace(R.id.contenedor,frboletos);
                        trans1.commit();
                        break;
                    case R.id.action_perfil:
                        frperfil = new Perfil_Fr();
                        FragmentTransaction trans2= getSupportFragmentManager().beginTransaction();
                        trans2.replace(R.id.contenedor,frperfil);
                        trans2.commit();
                    break;
                    case R.id.action_ubicacion:
                        Intent i=new Intent(getApplicationContext() ,UbicacionAct.class);
                        startActivity(i);
                        frboletos = new BoletosPrin();
                        FragmentTransaction trans3= getSupportFragmentManager().beginTransaction();
                        trans3.replace(R.id.contenedor,frboletos);
                        trans3.commit();
                }

                return true;
            }
        });
    }

    void iniciar_menu_lateral(){

        /*drawerLayout=(DrawerLayout) findViewById(R.id.DRWMenu);
        LYmenu =(LinearLayout) findViewById(R.id.LYmenu);
        ListaMenu=(ListView)findViewById(R.id.listamenu);
        String[] opciones={"Opcion 1","Opción 2","Opción 3"};
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1,opciones);
        ListaMenu.setAdapter(adp);

        ListaMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String opSelecccionada = (String) ListaMenu.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(),opSelecccionada, Toast.LENGTH_SHORT).show();
            }
        });*/
    }



}

