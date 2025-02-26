package com.xubop961.niamniamapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xubop961.niamniamapp.fragments.LoginFragment;
import com.xubop961.niamniamapp.fragments.RegisterFragment;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav);

        // Cargar el LoginFragment de forma predeterminada si es el primer arranque
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
            // Si usas BottomNavigationView, puedes seleccionar el ítem correspondiente:
            bottomNavView.setSelectedItemId(R.id.nav_login);
        }

        // Maneja los clics en la barra de navegación
        bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_login) {
                selectedFragment = new LoginFragment();
            } else if (item.getItemId() == R.id.nav_register) {
                selectedFragment = new RegisterFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

}
