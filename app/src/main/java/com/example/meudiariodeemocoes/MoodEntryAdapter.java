package com.example.meudiariodeemocoes;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meudiariodeemocoes.R;
import com.example.meudiariodeemocoes.databinding.ItemMoodEntryBinding;
import com.example.meudiariodeemocoes.model.MoodEntry;
import java.text.SimpleDateFormat;
import java.util.ArrayList; // Importe ArrayList
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodEntryAdapter extends RecyclerView.Adapter<MoodEntryAdapter.MoodViewHolder> {

    private List<MoodEntry> moodEntries;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemDeleteClick(MoodEntry moodEntry);
    }

    public MoodEntryAdapter(Context context, List<MoodEntry> moodEntries, OnItemClickListener listener) {
        this.context = context;
        // Inicialize a lista para evitar NullPointerException se a lista de entrada for nula
        this.moodEntries = moodEntries != null ? moodEntries : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoodEntryBinding binding = ItemMoodEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        MoodEntry currentEntry = moodEntries.get(position);
        holder.bind(currentEntry);
    }

    @Override
    public int getItemCount() {
        return moodEntries.size();
    }

    public void updateEntries(List<MoodEntry> newEntries) {
        this.moodEntries.clear();
        if (newEntries != null) { // Adicione verificação de nulo
            this.moodEntries.addAll(newEntries);
        }
        notifyDataSetChanged();
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {
        private ItemMoodEntryBinding binding;

        public MoodViewHolder(ItemMoodEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.imageViewDeleteItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemDeleteClick(moodEntries.get(position));
                }
            });
        }

        void bind(MoodEntry entry) {
            binding.textViewMoodLabelItem.setText(entry.getMoodLabel());

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'<y_bin_925>, HH:mm", new Locale("pt", "BR"));
            binding.textViewMoodDateItem.setText(sdf.format(new Date(entry.getTimestamp())));

            if (!TextUtils.isEmpty(entry.getMoodEmojiName())) {
                try {
                    int emojiResId = context.getResources().getIdentifier(entry.getMoodEmojiName(), "drawable", context.getPackageName());
                    if (emojiResId != 0) {
                        binding.imageViewMoodEmojiItem.setImageResource(emojiResId);
                    } else {
                        binding.imageViewMoodEmojiItem.setImageResource(R.mipmap.ic_launcher);
                    }
                } catch (Resources.NotFoundException e) {
                    binding.imageViewMoodEmojiItem.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                binding.imageViewMoodEmojiItem.setImageResource(R.mipmap.ic_launcher);
            }
            binding.imageViewMoodEmojiItem.setContentDescription(entry.getMoodLabel());


            if (entry.getReasons() != null && !entry.getReasons().isEmpty()) {
                binding.textViewMoodReasonsItem.setText("Motivos: " + TextUtils.join(", ", entry.getReasons()));
                binding.textViewMoodReasonsItem.setVisibility(View.VISIBLE);
            } else {
                binding.textViewMoodReasonsItem.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(entry.getDescription())) {
                binding.textViewMoodDescriptionItem.setText(entry.getDescription());
                binding.textViewMoodDescriptionItem.setVisibility(View.VISIBLE);
            } else {
                binding.textViewMoodDescriptionItem.setVisibility(View.GONE);
            }
        }
    }
}
