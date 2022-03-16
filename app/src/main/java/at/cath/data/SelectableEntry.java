package at.cath.data;

public abstract class SelectableEntry {

    protected boolean selected;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
