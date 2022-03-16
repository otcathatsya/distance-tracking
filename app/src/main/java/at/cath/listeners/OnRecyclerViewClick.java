package at.cath.listeners;

import java.util.List;

public interface OnRecyclerViewClick<T> {
    void onToggleSelect(T t, List<T> selected);
}