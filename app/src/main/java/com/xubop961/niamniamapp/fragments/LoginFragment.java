package com.xubop961.niamniamapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.xubop961.niamniamapp.MainActivity;
import com.xubop961.niamniamapp.R;


public class LoginFragment extends Fragment {

    private TextInputEditText editEmail, editPassword;
    private Button btnLogin;

    public LoginFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazar vistas
        editEmail = view.findViewById(R.id.inputEmail);
        editPassword = view.findViewById(R.id.inputPassword);
        btnLogin = view.findViewById(R.id.botonLogin);

        // Configurar evento del botón
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si las credenciales son correctas
        if (email.equals("admin@gmail.com") && password.equals("123456")) {
            Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            // Redirigir a MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish(); // Finaliza LoginActivity para que no pueda volver al login con el botón de retroceso

        } else {
            Toast.makeText(getContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
