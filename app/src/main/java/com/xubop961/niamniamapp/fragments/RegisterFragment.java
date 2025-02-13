package com.xubop961.niamniamapp.fragments;

import android.content.Intent;  // Para cambiar de actividad
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
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.MainActivity; // Importa MainActivity para la redirección

public class RegisterFragment extends Fragment {

    private TextInputEditText editName, editEmail, editPassword, editRepeatPassword;
    private Button btnRegister;

    public RegisterFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazar vistas
        editName = view.findViewById(R.id.inputName);
        editEmail = view.findViewById(R.id.inputEmail);
        editPassword = view.findViewById(R.id.inputPassword);
        editRepeatPassword = view.findViewById(R.id.inputRepeatPassword);
        btnRegister = view.findViewById(R.id.buttonRegister);

        // Configurar evento del botón
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String repeatPassword = editRepeatPassword.getText().toString().trim();

        // Validar campos vacíos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(repeatPassword)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lógica de registro (aquí puedes guardar los datos en una base de datos o en preferencias)
        // Si el registro es exitoso, redirigir al usuario a MainActivity
        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

        // Redirigir a MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish(); // Cerrar RegisterActivity
    }
}

