package com.example.meudiariodeemocoes;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meudiariodeemocoes.R;
import com.example.meudiariodeemocoes.databinding.ActivityDashboardBinding;
import com.example.meudiariodeemocoes.model.MoodEntry;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarDashboard);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_dashboard));
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadDashboardData();
    } // Fim do método onCreate

    private void loadDashboardData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_LONG).show();
            binding.textViewNoDashboardData.setVisibility(View.VISIBLE);
            binding.progressBarDashboard.setVisibility(View.GONE);
            return;
        }
        String userId = currentUser.getUid();

        binding.progressBarDashboard.setVisibility(View.VISIBLE);
        binding.textViewNoDashboardData.setVisibility(View.GONE);
        binding.pieChartMoodDistribution.setVisibility(View.GONE);
        binding.textViewMostFrequentMood.setVisibility(View.GONE);
        binding.textViewMainReasonSummary.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        long thirtyDaysAgoTimestamp = calendar.getTimeInMillis();

        db.collection("mood_entries")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("timestamp", thirtyDaysAgoTimestamp)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBarDashboard.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<MoodEntry> moodEntries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            moodEntries.add(document.toObject(MoodEntry.class));
                        }

                        if (moodEntries.isEmpty()) {
                            binding.textViewNoDashboardData.setVisibility(View.VISIBLE);
                        } else {
                            binding.pieChartMoodDistribution.setVisibility(View.VISIBLE);
                            binding.textViewMostFrequentMood.setVisibility(View.VISIBLE);
                            processAndDisplayDashboard(moodEntries);
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Toast.makeText(DashboardActivity.this, "Erro ao carregar dados do dashboard: " + errorMessage, Toast.LENGTH_SHORT).show();
                        binding.textViewNoDashboardData.setVisibility(View.VISIBLE);
                    }
                });
    } // Fim do método loadDashboardData

    private void processAndDisplayDashboard(List<MoodEntry> entries) {
        Map<String, Integer> moodCounts = new HashMap<>();
        for (MoodEntry entry : entries) {
            if (entry.getMoodLabel() != null) {
                moodCounts.put(entry.getMoodLabel(), moodCounts.getOrDefault(entry.getMoodLabel(), 0) + 1);
            }
        }

        if (!moodCounts.isEmpty()) {
            setupPieChart(moodCounts);
        } else {
            binding.pieChartMoodDistribution.clear();
            binding.pieChartMoodDistribution.setVisibility(View.GONE);
        }

        String mostFrequentMood = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> moodCount : moodCounts.entrySet()) {
            if (moodCount.getValue() > maxCount) {
                maxCount = moodCount.getValue();
                mostFrequentMood = moodCount.getKey();
            }
        }
        if (!mostFrequentMood.isEmpty()) {
            binding.textViewMostFrequentMood.setText(getString(R.string.most_frequent_mood_is, mostFrequentMood));
            binding.textViewMostFrequentMood.setVisibility(View.VISIBLE);
        } else {
            binding.textViewMostFrequentMood.setText(getString(R.string.no_data_for_dashboard));
            binding.textViewMostFrequentMood.setVisibility(View.VISIBLE);
        }
    } // Fim do método processAndDisplayDashboard

    private void setupPieChart(Map<String, Integer> moodCounts) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> moodCount : moodCounts.entrySet()) {
            pieEntries.add(new PieEntry(moodCount.getValue(), moodCount.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(binding.pieChartMoodDistribution));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        binding.pieChartMoodDistribution.setData(data);
        binding.pieChartMoodDistribution.getDescription().setEnabled(false);
        binding.pieChartMoodDistribution.setUsePercentValues(true);
        binding.pieChartMoodDistribution.setEntryLabelTextSize(10f);
        binding.pieChartMoodDistribution.setEntryLabelColor(Color.BLACK);
        binding.pieChartMoodDistribution.setCenterText("Humores");
        binding.pieChartMoodDistribution.setCenterTextSize(16f);
        binding.pieChartMoodDistribution.setDrawEntryLabels(true);
        binding.pieChartMoodDistribution.setRotationEnabled(true);
        binding.pieChartMoodDistribution.setHighlightPerTapEnabled(true);
        binding.pieChartMoodDistribution.animateY(1400, Easing.EaseInOutQuad);
        binding.pieChartMoodDistribution.invalidate();
        binding.pieChartMoodDistribution.setVisibility(View.VISIBLE);
    } // Fim do método setupPieChart

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    } // Fim do método onOptionsItemSelected

} // Fim da classe DashboardActivity