package com.xubop961.niamniamapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class RegisterFragment extends Fragment {

    private TextInputEditText editName, editEmail, editPassword;
    private Button btnRegister;

    public RegisterFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.inputName);
        editEmail = view.findViewById(R.id.inputEmail);
        editPassword = view.findViewById(R.id.inputPassword);
        btnRegister = view.findViewById(R.id.buttonRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Accedemos a SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("niamniam_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String key = "user_" + email;

        // Verificar si el usuario ya existe
        if (sharedPreferences.contains(key)) {
            Toast.makeText(getContext(), "El usuario ya existe, por favor inicia sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar los datos del usuario en el formato "nombre;password"
        String userData = name + ";" + password;
        editor.putString(key, userData);

        // Guardar el nombre del usuario para la sesión actual
        editor.putString("logged_in_name", name);
        editor.apply();

        Toast.makeText(getContext(), "Registro exitoso. Bienvenido " + name, Toast.LENGTH_SHORT).show();

        // Redirigir al HomePage (MainActivity)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
