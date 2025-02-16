package com.xubop961.niamniamapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.xubop961.niamniamapp.fragments.AddPage;
import com.xubop961.niamniamapp.fragments.FavoritosPage;
import com.xubop961.niamniamapp.fragments.HomePage;
import com.xubop961.niamniamapp.fragments.PerfilPage;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;

                if (itemId == R.id.home) {
                    selectedFragment = new HomePage();
                } else if (itemId == R.id.añadir) {
                    selectedFragment = new AddPage();
                } else if (itemId == R.id.perfil) {
                    selectedFragment = new PerfilPage();
                } else if (itemId == R.id.favoritos) {
                    selectedFragment = new FavoritosPage();
                } else {
                    selectedFragment = new HomePage();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.configuracionButton) {
            Toast toast = Toast.makeText(this, "Botón Configuración", Toast.LENGTH_SHORT);
            toast.show();
        } else if (id == R.id.acercadeButton) {
            Toast toast = Toast.makeText(this, "Botón acerca de", Toast.LENGTH_SHORT);
            toast.show();
        } else if (id == R.id.cerrarSeionButton) {
            Toast toast = Toast.makeText(this, "Botón cerrar sesión", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }
}