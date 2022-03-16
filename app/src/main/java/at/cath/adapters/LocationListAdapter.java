package at.cath.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.cath.R;
import at.cath.data.LocationEntry;
import at.cath.data.SelectableEntry;
import at.cath.databinding.LocationEntryBinding;
import at.cath.listeners.MultiSelectionHandler;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    private final List<LocationEntry> entries;
    private final MultiSelectionHandler<LocationEntry> displayCalcHandler;
    private final Context context;

    public LocationListAdapter(Context context, MultiSelectionHandler.OnSelectRequiredAmountListener<LocationEntry> onSelect, MultiSelectionHandler.OnDeselect onDeselect) {
        entries = new ArrayList<>();
        this.context = context;
        this.displayCalcHandler = new MultiSelectionHandler<>(onSelect, onDeselect, 2);
    }

    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_entry, parent, false);
        return new LocationListAdapter.ViewHolder(context, view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationEntry entry = entries.get(position);

        holder.binding.removeEntryButton.setOnClickListener((view) -> removeEntry(position));

        View itemView = holder.itemView;

        itemView.setOnLongClickListener(view -> {
            createEditPrompt(entry, position).show();
            return true;
        });

        itemView.setOnClickListener(view -> {
            itemView.setElevation(20);
            displayCalcHandler.onToggleSelect(entry, getSelectedEntries());
            notifyDataSetChanged();
        });

        holder.bind(entry, position);
    }

    private AlertDialog.Builder createEditPrompt(LocationEntry entry, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_Material3_Dark_DialogWhenLarge);

        final EditText editName = new EditText(context);
        alert.setMessage("Name your entry!");
        alert.setTitle("Edit entry #" + (position + 1));

        alert.setView(editName);

        alert.setPositiveButton("Confirm", (dialog, whichButton) -> {
            entry.setName(editName.getText().toString());
            notifyItemChanged(position);
            dialog.cancel();
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.cancel());

        return alert;
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
        private final SharedPreferences preferences;

        private final int highlightCol;
        private final int defaultCol;

        public ViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            this.binding = LocationEntryBinding.bind(view);

            this.labelText = binding.locationLabel;
            this.infoText = binding.infoText;

            this.highlightCol = ContextCompat.getColor(context, R.color.highlight);
            this.defaultCol = ContextCompat.getColor(context, R.color.app_bar);

            this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        public void bind(LocationEntry entry, int position) {
            System.out.println("yo it's "+entry);
            if (preferences.getBoolean("use_names", true)) {
                if (entry.getName() != null) {
                    labelText.setText(entry.getName());
                } else {
                    labelText.setText(Html.fromHtml(context.getString(R.string.loc_entry_name_label, position), Html.FROM_HTML_MODE_LEGACY));
                }
            } else {
                labelText.setText(Html.fromHtml(context.getString(R.string.loc_entry_date_label, entry.getDateTakenFormatted()), Html.FROM_HTML_MODE_LEGACY));
            }

            infoText.setText(entry.toString());
            itemView.setBackgroundColor(entry.isSelected() ? highlightCol : defaultCol);
        }
    }
}