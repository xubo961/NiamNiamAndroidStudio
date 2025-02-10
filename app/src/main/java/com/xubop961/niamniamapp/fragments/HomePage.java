package com.xubop961.niamniamapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xubop961.niamniamapp.R;
import com.xubop961.niamniamapp.Recetas;
import com.xubop961.niamniamapp.adapters.FoodAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<Recetas> recetaList;

    public HomePage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recetaList = new ArrayList<>();
        recetaList.add(new Recetas("Pizza Margarita", R.drawable.coche));
        recetaList.add(new Recetas("Hamburguesa", R.drawable.coche));
        recetaList.add(new Recetas("Ensalada César", R.drawable.coche));
        recetaList.add(new Recetas("Tacos al Pastor", R.drawable.coche));
        recetaList.add(new Recetas("Sopa de Tomate", R.drawable.coche));

        adapter = new FoodAdapter(recetaList);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
