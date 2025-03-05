package com.xubop961.niamniamapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.api.Recipe;
import com.xubop961.niamniamapp.api.RecipePreferences;

import java.util.ArrayList;

public class AddPage extends Fragment {

    // Variables para la selección de ingredientes
    private CardView cvIngredientes;
    private TextView selectIngredientes;
    private boolean[] selectedIngredientes;
    private ArrayList<Integer> listIngredientes = new ArrayList<>();
    private String[] arrayIngredientes = {"Milk", "Egg", "Beef", "Rice", "Chicken", "Potatoes", "Pork", "Salmon", "Spaghetti", "Onion", "Sausages", "Banana", "Avocado"};

    // Código de permisos
    private static final int REQUEST_PERMISSION_STORAGE = 100;

    // Variables para la imagen
    private ImageView imageViewRecipe;
    private Uri selectedImageUri;

    // EditText para nombre e instrucciones
    private EditText editTextRecetaName, editTextInstrucciones;

    // ActivityResultLauncher para seleccionar imagen (usando registerForActivityResult)
    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        imageViewRecipe.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public AddPage() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_add_page, container, false);

        // Inicializamos las vistas
        imageViewRecipe = view.findViewById(R.id.selectImage);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        cvIngredientes = view.findViewById(R.id.cvIngredientes);
        selectIngredientes = view.findViewById(R.id.selectIngredientes);
        selectedIngredientes = new boolean[arrayIngredientes.length];

        editTextRecetaName = view.findViewById(R.id.textRecetaName);
        editTextInstrucciones = view.findViewById(R.id.tetxInstrucciones);
        Button btnGuardarReceta = view.findViewById(R.id.btnGuardarReceta);

        // Configuramos la selección de ingredientes mediante diálogo
        cvIngredientes.setOnClickListener(v -> showIngredientesDialog());

        // Configuramos el botón para seleccionar imagen
        btnSelectImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_PERMISSION_STORAGE);
                } else {
                    openImagePicker();
                }
            } else {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_STORAGE);
                } else {
                    openImagePicker();
                }
            }
        });

        // Configuramos el botón para guardar la receta (con SharedPreferences)
        btnGuardarReceta.setOnClickListener(v -> {
            String name = editTextRecetaName.getText().toString().trim();
            String instructions = editTextInstrucciones.getText().toString().trim();
            String ingredients = selectIngredientes.getText().toString().trim();
            String imageUriString = (selectedImageUri != null) ? selectedImageUri.toString() : "";

            if (name.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Recipe recipe = new Recipe(name, ingredients, instructions, imageUriString);
                RecipePreferences.saveRecipe(getContext(), recipe);
                Toast.makeText(getContext(), "Receta guardada", Toast.LENGTH_SHORT).show();
                // Opcional: limpiar los campos luego de guardar
                editTextRecetaName.setText("");
                editTextInstrucciones.setText("");
                selectIngredientes.setText("");
                imageViewRecipe.setImageResource(R.drawable.coche); // Imagen por defecto
                selectedImageUri = null;
                // Reiniciamos la selección de ingredientes
                for (int i = 0; i < selectedIngredientes.length; i++) {
                    selectedIngredientes[i] = false;
                }
                listIngredientes.clear();
            }
        });

        return view;
    }

    // Función para abrir el selector de imagen
    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    // Función para mostrar el diálogo de selección de ingredientes
    private void showIngredientesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona los ingredientes:")
                .setCancelable(false)
                .setMultiChoiceItems(arrayIngredientes, selectedIngredientes, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        listIngredientes.add(which);
                    } else {
                        listIngredientes.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < listIngredientes.size(); i++) {
                        stringBuilder.append(arrayIngredientes[listIngredientes.get(i)]);
                        if (i != listIngredientes.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    selectIngredientes.setText(stringBuilder.toString());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Quitar", (dialog, which) -> {
                    for (int i = 0; i < selectedIngredientes.length; i++) {
                        selectedIngredientes[i] = false;
                    }
                    listIngredientes.clear();
                    selectIngredientes.setText("");
                    dialog.dismiss();
                });
        builder.show();
    }
}
