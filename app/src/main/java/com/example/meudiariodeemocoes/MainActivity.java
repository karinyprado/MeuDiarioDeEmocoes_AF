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

        moodEntryList = new ArrayList<>();
        adapter = new MoodEntryAdapter(this, moodEntryList, this);

        binding.recyclerViewMoodHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMoodHistory.setAdapter(adapter);
        binding.fabAddMood.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddMoodEntryActivity.class)));
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
        firestoreListener = db.collection("mood_entries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        moodEntryList.clear();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            MoodEntry entry = doc.toObject(MoodEntry.class);
                            entry.setEntryId(doc.getId());
                            moodEntryList.add(entry);
                        }
                        adapter.notifyDataSetChanged();

                        if (moodEntryList.isEmpty()) {
                            binding.textViewNoEntries.setVisibility(View.VISIBLE);
                        } else {
                            binding.textViewNoEntries.setVisibility(View.GONE);
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
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, R.string.delete_entry_success, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, getString(R.string.delete_entry_error) + ": " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}