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
import com.xubop961.niamniamapp.api.RegisterApi;
import com.xubop961.niamniamapp.api.ApiClientBackend;
import com.xubop961.niamniamapp.api.RegisterRequest;
import com.xubop961.niamniamapp.api.RegisterUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Crear el objeto RegisterRequest con los datos
        RegisterRequest request = new RegisterRequest(name, email, password);

        // Obtener la instancia de la API usando Retrofit
        RegisterApi registerApi = ApiClientBackend.getClient().create(RegisterApi.class);

        // Hacer la petición asíncrona para crear el usuario
        Call<RegisterUser> call = registerApi.createUser(request);
        call.enqueue(new Callback<RegisterUser>() {
            @Override
            public void onResponse(Call<RegisterUser> call, Response<RegisterUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    // Redirigir a MainActivity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Error en el registro: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterUser> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
