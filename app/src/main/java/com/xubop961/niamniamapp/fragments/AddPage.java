package com.xubop961.niamniamapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xubop961.niamniamapp.R;

import java.util.ArrayList;

public class AddPage extends Fragment {

    CardView cvIngredientes;
    TextView selectIngredientes;
    boolean [] selectedIngredientes;
    ArrayList<Integer> listIngredientes = new ArrayList<>();
    String [] arrayIngredientes = {"Milk", "Egg", "Beef", "Rice", "Chicken", "Potatoes","Pork","Salmon","Spaghetti","Onion","Sausages","Banana","Avocado"};


    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_PERMISSION_STORAGE = 100;

    private ImageView imageViewRecipe;

    public AddPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_page, container, false);

        imageViewRecipe = view.findViewById(R.id.selectImage);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);

        cvIngredientes = view.findViewById(R.id.cvIngredientes);
        selectIngredientes = view.findViewById(R.id.selectIngredientes);
        selectedIngredientes = new boolean[arrayIngredientes.length];

        cvIngredientes.setOnClickListener(v -> {
            showIngredientesDialog();
        });

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

        return view;
    }

    private void showIngredientesDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Selecciona los ingredientes: ");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(arrayIngredientes, selectedIngredientes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            listIngredientes.add(which);
                        } else {
                            listIngredientes.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < listIngredientes.size(); i++) {
                            stringBuilder.append(arrayIngredientes[listIngredientes.get(i)]);

                            if (i != listIngredientes.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        selectIngredientes.setText(stringBuilder.toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Quitar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < selectedIngredientes.length; i++) {
                            selectedIngredientes[i] = false;
                        }
                        listIngredientes.clear();
                        selectIngredientes.setText("");
                        dialog.dismiss();
                    }
                });

        builder.show();
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    imageViewRecipe.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
