package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.xubop961.niamniamapp.MainActivity;
import com.xubop961.niamniamapp.R;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
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
            Toast.makeText(getContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Acceder a SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("niamniam_preferences", Context.MODE_PRIVATE);
        String key = "user_" + email;

        // Comprobar si el usuario existe
        if (!sharedPreferences.contains(key)) {
            Toast.makeText(getContext(), "User does not exist, please register", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recuperar los datos del usuario; se asume que se guardaron en el formato "nombre;password"
        String userData = sharedPreferences.getString(key, "");
        if (userData.isEmpty()) {
            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] parts = userData.split(";");
        if (parts.length < 2) {
            Toast.makeText(getContext(), "Corrupted user data", Toast.LENGTH_SHORT).show();
            return;
        }

        String storedName = parts[0];
        String storedPassword = parts[1];

        if (storedPassword.equals(password)) {
            Toast.makeText(getContext(), "Login successful. Welcome " + storedName, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Login successful: " + storedName + ", Email: " + email);

            //Metodo para guardar el nombre para poder yusarlo en las otras pantallas
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("logged_in_name", storedName);
            editor.apply();

            // Redirigir a MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish(); // Evita volver al login con el botón de retroceso
        } else {
            Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
        }
    }
}
