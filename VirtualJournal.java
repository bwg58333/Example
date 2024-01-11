import javax.swing.*;

public class VirtualJournal {
    private JTextArea journalArea;

    public VirtualJournal() {
        journalArea = new JTextArea();
    }

    public void appendText(String text) {
        System.out.println("Appending to journal: " + text);
        journalArea.append(text + "\n");
    }

    public void clear() {
        journalArea.setText("");
    }

    public JTextArea getJournalArea() {
        return journalArea;
    }

}
