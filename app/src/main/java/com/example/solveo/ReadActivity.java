package com.example.solveo;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ModuleAdapter adapter;
    private List<Modules> moduleList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        moduleList = new ArrayList<>();
        adapter = new ModuleAdapter(moduleList);
        recyclerView.setAdapter(adapter);

        // Correct database path to match the JSON structure
        databaseReference = FirebaseDatabase.getInstance().getReference("modules");

        fetchModules();
    }

    private void fetchModules() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                moduleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Modules module = dataSnapshot.getValue(Modules.class);
                    if (module != null) {
                        moduleList.add(module);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch data: " + error.getMessage());
            }
        });
    }
}
