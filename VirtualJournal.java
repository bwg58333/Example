
/**
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

    public String getJournalEntries() {
        return journalArea.getText();
    }

    public String getLatestJournal() {
        String content = journalArea.getText();
        String[] lines =  content.split("\n");

        return lines.length > 0 ? lines[lines.length - 1] : "";
    }

}
 */

import javax.swing.*;

public class VirtualJournal {
    private JTextArea journalArea;
    private JournalUpdateListener updateListener;

    public VirtualJournal() {
        journalArea = new JTextArea();
    }

    public void appendText(String text) {
        System.out.println("Appending to journal: " + text);
        journalArea.append(text + "\n");
        notifyUpdate(text);
    }

    public void clear() {
        journalArea.setText("");
        notifyUpdate("");
    }

    public JTextArea getJournalArea() {
        return journalArea;
    }

    public String getJournalEntries() {
        return journalArea.getText();
    }

    public String getLatestJournal() {
        String content = journalArea.getText();
        String[] lines = content.split("\n");

        return lines.length > 0 ? lines[lines.length - 1] : "";
    }

    public void notifyUpdate(String update) {
        System.out.println("Update received: " + update);
        if (updateListener != null) {
            updateListener.onJournalUpdate(update);
        }
    }

}