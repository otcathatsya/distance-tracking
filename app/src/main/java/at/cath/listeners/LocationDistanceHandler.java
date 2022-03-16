package at.cath.listeners;

import java.util.List;
import java.util.function.BiConsumer;

import at.cath.data.SelectableEntry;

public class LocationDistanceHandler<T extends SelectableEntry> implements OnRecyclerViewClick<T> {

    private final BiConsumer<T, T> onSelected;
    private final Runnable onDeselected;

    public LocationDistanceHandler(BiConsumer<T, T> onSelected, Runnable onDeselected) {
        this.onSelected = onSelected;
        this.onDeselected = onDeselected;
    }

    @Override
    public void onToggleSelect(T entry, List<T> selected) {
        int selectedCount = selected.size();
        if (!entry.isSelected()) {
            if (selectedCount + 1 > 2)
                return;
            else if (selectedCount + 1 == 2) {
                T other = selected.get(0);
                onSelected.accept(entry, other);
            }
        } else {
            onDeselected.run();
        }
        entry.setSelected(!entry.isSelected());
    }
}
