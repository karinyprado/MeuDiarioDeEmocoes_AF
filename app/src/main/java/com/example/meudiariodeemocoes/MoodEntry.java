package com.example.meudiariodeemocoes;

import com.google.firebase.firestore.Exclude; // Importante para o ID do documento
import java.util.List;

public class MoodEntry {
    @Exclude // Para não salvar este campo diretamente no Firestore, pois ele é o ID do documento
    private String entryId;

    private String userId;
    private long timestamp;
    private String moodEmojiName; // Nome do recurso drawable do emoji (ex: "ic_emoji_happy")
    private String moodLabel;     // Texto do humor (ex: "Feliz")
    private List<String> reasons;
    private String description;

    // Construtor vazio necessário para o Firebase Firestore
    public MoodEntry() {}

    public MoodEntry(String userId, long timestamp, String moodEmojiName, String moodLabel, List<String> reasons, String description) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.moodEmojiName = moodEmojiName;
        this.moodLabel = moodLabel;
        this.reasons = reasons;
        this.description = description;
    }

    // Getters
    @Exclude
    public String getEntryId() { return entryId; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public String getMoodEmojiName() { return moodEmojiName; }
    public String getMoodLabel() { return moodLabel; }
    public List<String> getReasons() { return reasons; }
    public String getDescription() { return description; }

    // Setters
    public void setEntryId(String entryId) { this.entryId = entryId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setMoodEmojiName(String moodEmojiName) { this.moodEmojiName = moodEmojiName; }
    public void setMoodLabel(String moodLabel) { this.moodLabel = moodLabel; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public void setDescription(String description) { this.description = description; }
}
