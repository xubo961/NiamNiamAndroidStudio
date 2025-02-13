package com.xubop961.niamniamapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xubop961.niamniamapp.fragments.LoginFragment;
import com.xubop961.niamniamapp.fragments.RegisterFragment;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Asegúrate de tener activity_login.xml

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav);

        // Mostrar el fragmento inicial (Login)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();

        // Manejar clics en la barra de navegación
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