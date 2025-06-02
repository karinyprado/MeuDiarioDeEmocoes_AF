package com.example.meudiariodeemocoes;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.meudiariodeemocoes.R;
import com.example.meudiariodeemocoes.databinding.ActivityAddMoodEntryBinding;
import com.example.meudiariodeemocoes.model.MoodEntry;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMoodEntryActivity extends AppCompatActivity {

    private ActivityAddMoodEntryBinding binding; // Usando ViewBinding
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<ImageButton> emojiButtons;
    private String selectedMoodEmojiName = "";
    private String selectedMoodLabel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMoodEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarAddMoodEntry); // Usando ViewBinding para acessar a Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_activity_add_mood));
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupEmojiListeners();
        binding.buttonSaveMood.setOnClickListener(v -> saveMoodEntry());
    }

    private void setupEmojiListeners() {
        emojiButtons = new ArrayList<>(Arrays.asList(
                binding.emojiHappy, binding.emojiSad, binding.emojiNeutral,
                binding.emojiAnxious, binding.emojiAngry
        ));

        binding.emojiHappy.setOnClickListener(v -> selectEmoji(binding.emojiHappy, "ic_emoji_happy", getString(R.string.mood_label_happy)));
        binding.emojiSad.setOnClickListener(v -> selectEmoji(binding.emojiSad, "ic_emoji_sad", getString(R.string.mood_label_sad)));
        binding.emojiNeutral.setOnClickListener(v -> selectEmoji(binding.emojiNeutral, "ic_emoji_neutral", getString(R.string.mood_label_neutral)));
        binding.emojiAnxious.setOnClickListener(v -> selectEmoji(binding.emojiAnxious, "ic_emoji_anxious", getString(R.string.mood_label_anxious)));
        binding.emojiAngry.setOnClickListener(v -> selectEmoji(binding.emojiAngry, "ic_emoji_angry", getString(R.string.mood_label_angry)));
    }

    private void selectEmoji(ImageButton selectedButton, String emojiName, String moodLabel) {
        for (ImageButton btn : emojiButtons) {
            btn.setBackgroundColor(ContextCompat.getColor(this, R.color.emoji_default_background));
            btn.setSelected(false);
        }
        selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.emoji_selected_background));
        selectedButton.setSelected(true);
        selectedMoodEmojiName = emojiName;
        selectedMoodLabel = moodLabel;
    }

    private void saveMoodEntry() {
        if (TextUtils.isEmpty(selectedMoodLabel)) {
            Toast.makeText(this, getString(R.string.please_select_mood), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado. Tente reiniciar o app.", Toast.LENGTH_LONG).show();
            return;
        }
        String userId = currentUser.getUid();

        List<String> selectedReasons = new ArrayList<>();
        // Iterar sobre os Chips no ChipGroup
        if (binding.chipFamily.isChecked()) selectedReasons.add(binding.chipFamily.getText().toString());
        if (binding.chipWork.isChecked()) selectedReasons.add(binding.chipWork.getText().toString());
        if (binding.chipFriends.isChecked()) selectedReasons.add(binding.chipFriends.getText().toString());
        if (binding.chipPersonalLife.isChecked()) selectedReasons.add(binding.chipPersonalLife.getText().toString());
        if (binding.chipMoney.isChecked()) selectedReasons.add(binding.chipMoney.getText().toString());
        if (binding.chipHealth.isChecked()) selectedReasons.add(binding.chipHealth.getText().toString());
        if (binding.chipStudies.isChecked()) selectedReasons.add(binding.chipStudies.getText().toString());
        if (binding.chipOther.isChecked()) selectedReasons.add(binding.chipOther.getText().toString());


        String description = binding.editTextDescription.getText().toString().trim();
        long timestamp = System.currentTimeMillis();

        MoodEntry newEntry = new MoodEntry(userId, timestamp, selectedMoodEmojiName, selectedMoodLabel, selectedReasons, description);

        binding.buttonSaveMood.setEnabled(false);

        db.collection("mood_entries")
                .add(newEntry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMoodEntryActivity.this, getString(R.string.mood_saved_success), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMoodEntryActivity.this, getString(R.string.error_saving_mood, e.getMessage()), Toast.LENGTH_LONG).show();
                    binding.buttonSaveMood.setEnabled(true);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
