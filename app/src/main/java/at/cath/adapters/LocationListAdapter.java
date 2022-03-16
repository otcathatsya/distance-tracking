package at.cath.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import at.cath.R;
import at.cath.data.LocationEntry;
import at.cath.data.SelectableEntry;
import at.cath.databinding.LocationEntryBinding;
import at.cath.listeners.LocationDistanceHandler;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    private final List<LocationEntry> entries;
    private final LocationDistanceHandler displayCalcHandler;
    private final Context context;

    public LocationListAdapter(Context context, BiConsumer<LocationEntry, LocationEntry> onSelect, Runnable onDeselect) {
        entries = new ArrayList<>();
        this.context = context;
        this.displayCalcHandler = new LocationDistanceHandler(onSelect, onDeselect);
    }

    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_entry, parent, false);
        return new LocationListAdapter.ViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationEntry entry = entries.get(position);

        holder.binding.removeEntryButton.setOnClickListener((view) -> removeEntry(position));

        View itemView = holder.itemView;

        itemView.setOnLongClickListener(view -> {
            Snackbar.make(view, entry.toString(), Snackbar.LENGTH_SHORT).show();
            return true;
        });

        itemView.setOnClickListener(view -> {
            displayCalcHandler.onToggleSelect(entry, getSelectedEntries());
            notifyDataSetChanged();
        });

        holder.bind(entry);
    }

    public List<LocationEntry> getSelectedEntries() {
        return entries.stream().filter(SelectableEntry::isSelected).collect(Collectors.toList());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void addEntry(LocationEntry locationEntry) {
        entries.add(locationEntry);
        notifyItemInserted(entries.indexOf(locationEntry));
        notifyItemChanged(entries.indexOf(locationEntry));
    }

    public void removeEntry(int position) {
        entries.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, entries.size());
    }

    public List<LocationEntry> getEntries() {
        return this.entries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LocationEntryBinding binding;
        private final TextView labelText;
        private final TextView infoText;
        private final Context context;

        private final int highlightCol;
        private final int resetCol;

        public ViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            this.binding = LocationEntryBinding.bind(view);

            this.labelText = binding.locationLabel;
            this.infoText = binding.infoText;

            this.highlightCol = ContextCompat.getColor(context, R.color.highlight);
            this.resetCol = ContextCompat.getColor(context, R.color.white);
        }

        public void bind(LocationEntry entry) {
            labelText.setText(Html.fromHtml(context.getString(R.string.loc_entry_date_label, entry.getDateTakenFormatted()), Html.FROM_HTML_MODE_LEGACY));
            infoText.setText(entry.toString());

            itemView.setBackgroundColor(entry.isSelected() ? highlightCol : resetCol);
        }
    }
}