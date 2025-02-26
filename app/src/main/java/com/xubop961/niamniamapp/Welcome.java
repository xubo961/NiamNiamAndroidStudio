package com.xubop961.niamniamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Welcome extends AppCompatActivity {

    Button loginButton;
    TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilita el modo EdgeToEdge
        setContentView(R.layout.activity_welcome);

        // Inicializa los componentes
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Listener para el botón de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLogin();
            }
        });

        // Listener para el TextView de sign up
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterInLogin();
            }
        });
    }

    public void launchLogin() {
        Intent intent = new Intent(Welcome.this, Login.class);
        startActivity(intent);
    }

    private void launchRegisterInLogin() {
        Intent intent = new Intent(Welcome.this, Login.class);
        intent.putExtra("open_register", true);
        startActivity(intent);
    }
}
