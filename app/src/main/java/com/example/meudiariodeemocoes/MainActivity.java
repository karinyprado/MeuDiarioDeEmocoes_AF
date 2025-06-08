package com.example.meudiariodeemocoes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meudiariodeemocoes.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoodEntryAdapter.OnItemClickListener {

    private static final String TAG = "DiaryApp";
    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private MoodEntryAdapter adapter;
    private List<MoodEntry> moodEntryList;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarMain);

        db = FirebaseFirestore.getInstance();

        // A lista é criada aqui e passada para o adapter.
        // Ambos (Activity e Adapter) terão a mesma referência.
        moodEntryList = new ArrayList<>();
        adapter = new MoodEntryAdapter(this, moodEntryList, this);

        binding.recyclerViewMoodHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMoodHistory.setAdapter(adapter);

        binding.fabAddMood.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddMoodEntryActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirestoreListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }

    private void setupFirestoreListener() {
        Log.d(TAG, "Configurando o listener do Firestore...");
        binding.progressBarMain.setVisibility(View.VISIBLE);

        firestoreListener = db.collection("mood_entries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    binding.progressBarMain.setVisibility(View.GONE);

                    if (e != null) {
                        Log.e(TAG, "Erro ao ouvir o Firestore.", e);
                        return;
                    }

                    if (snapshots != null) {
                        Log.d(TAG, "Snapshot recebido com " + snapshots.size() + " documentos.");

                        // Limpamos e preenchemos a lista que o adapter JÁ CONHECE.
                        moodEntryList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            MoodEntry entry = doc.toObject(MoodEntry.class);
                            entry.setEntryId(doc.getId());
                            moodEntryList.add(entry);
                        }

                        // *** A CORREÇÃO PRINCIPAL ESTÁ AQUI ***
                        // Apenas notificamos o adapter que os dados na lista dele mudaram.
                        Log.d(TAG, "Notificando o adapter que os dados mudaram. Novo tamanho: " + moodEntryList.size());
                        adapter.notifyDataSetChanged();

                        // A lógica de visibilidade continua a mesma
                        if (moodEntryList.isEmpty()) {
                            binding.textViewNoEntries.setVisibility(View.VISIBLE);
                            binding.recyclerViewMoodHistory.setVisibility(View.GONE);
                        } else {
                            binding.textViewNoEntries.setVisibility(View.GONE);
                            binding.recyclerViewMoodHistory.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_dashboard) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemDeleteClick(MoodEntry moodEntry) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_entry_dialog_title)
                .setMessage(R.string.delete_entry_dialog_message)
                .setPositiveButton(R.string.dialog_delete, (dialog, which) -> deleteEntryFromFirestore(moodEntry))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .show();
    }

    private void deleteEntryFromFirestore(MoodEntry moodEntry) {
        if (moodEntry.getEntryId() == null || moodEntry.getEntryId().isEmpty()) {
            Toast.makeText(this, "ID do registro inválido.", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("mood_entries").document(moodEntry.getEntryId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, R.string.delete_entry_success, Toast.LENGTH_SHORT).show();
                    // Não precisamos fazer nada aqui, o listener do Firestore cuidará da atualização da lista.
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, getString(R.string.delete_entry_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}