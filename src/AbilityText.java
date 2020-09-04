import javax.swing.*;

public class AbilityText implements Comparable<AbilityText>{
    JPanel pane;
    JLabel label;

    String title;
    int priority;

    public AbilityText(String title,String description,int priority){
        this.title=title;
        label.setText(description);
        this.priority=priority;

    }

    /**
     * Determines if some other AbilityText has a higher priority than this AbilityText
     * @param o the other AbilityText
     * @return positive if other has higher priority
     */
    @Override
    public int compareTo(AbilityText o) {
        return o.priority-this.priority;
    }
}
