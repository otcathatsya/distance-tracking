package at.cath.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import at.cath.data.SelectableEntry;

public class MultiSelectionHandler<T extends SelectableEntry> {

    private final OnSelectRequiredAmountListener<T> onSelect;
    private final OnDeselect onDeselect;
    private final int maxSelections;

    public MultiSelectionHandler(OnSelectRequiredAmountListener<T> onSelect, OnDeselect onDeselect, int maxSelections) {
        this.onSelect = onSelect;
        this.onDeselect = onDeselect;
        this.maxSelections = maxSelections;
    }

    public void onToggleSelect(T entry, List<T> selected) {
        List<T> retSelected = new ArrayList<>(selected);
        if (!entry.isSelected()) {
            if (retSelected.size() == maxSelections) {
                return;
            } else {
                retSelected.add(entry);
                if (retSelected.size() == maxSelections) {
                    onSelect.onSelectRequiredAmount(retSelected);
                }
            }
        } else {
            onDeselect.onDeselect();
        }
        entry.setSelected(!entry.isSelected());
    }

    public interface OnSelectRequiredAmountListener<T> {
        void onSelectRequiredAmount(List<T> elements);
    }

    public interface OnDeselect {
        void onDeselect();
    }
}
