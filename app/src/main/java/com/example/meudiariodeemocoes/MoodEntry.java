package com.example.meudiariodeemocoes; // CORRIGIDO

import com.google.firebase.firestore.Exclude;
import java.util.List;

public class MoodEntry {
    @Exclude
    private String entryId;
    private String userId;
    private long timestamp;
    private String moodEmojiName;
    private String moodLabel;
    private List<String> reasons;
    private String description;

    public MoodEntry() {}

    public MoodEntry(String userId, long timestamp, String moodEmojiName, String moodLabel, List<String> reasons, String description) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.moodEmojiName = moodEmojiName;
        this.moodLabel = moodLabel;
        this.reasons = reasons;
        this.description = description;
    }

    @Exclude
    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getMoodEmojiName() { return moodEmojiName; }
    public void setMoodEmojiName(String moodEmojiName) { this.moodEmojiName = moodEmojiName; }

    public String getMoodLabel() { return moodLabel; }
    public void setMoodLabel(String moodLabel) { this.moodLabel = moodLabel; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}