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

        // Elimina el efecto ripple estableciendo el color a null
        bottomNavView.setItemRippleColor(null);

        boolean openRegister = getIntent().getBooleanExtra("open_register", false);

        if (openRegister) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
            bottomNavView.setSelectedItemId(R.id.nav_register); // Selecciona la opción de registro en la barra de navegación
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
            bottomNavView.setSelectedItemId(R.id.nav_login); // Selecciona la opción de login por defecto
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
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
            }
            return true;
        });
    }
}
