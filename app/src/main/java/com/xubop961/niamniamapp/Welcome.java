package com.xubop961.niamniamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


//Lo que está comentado es para una prueba, de cambiar a esta actividad a una diferente
//pero da un error que hay que solucionar :D
public class Welcome extends AppCompatActivity {

    Button loginButton =findViewById(R.id.loginButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


      loginButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              launchLogin();
          }
      });


}

  public void launchLogin() {
      Intent intent = new Intent(Welcome.this, Login.class);
      startActivity(intent);


  }
}
