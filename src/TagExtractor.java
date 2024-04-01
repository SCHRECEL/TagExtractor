import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea tagsTextArea;
    private JButton selectFileButton;
    private JButton selectStopWordsButton;
    private JButton extractTagsButton;
    private JButton saveTagsButton;
    private JLabel selectedFileLabel;
    private Map<String, Integer> tagFrequencyMap;
    private Set<String> stopWordsSet;

    public TagExtractor() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tagsTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(tagsTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        selectedFileLabel = new JLabel("Selected File: ");
        topPanel.add(selectedFileLabel);

        JPanel filePanel = new JPanel(new FlowLayout());
        selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        selectStopWordsButton = new JButton("Select Stop Words");
        selectStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStopWordsFile();
            }
        });
        filePanel.add(selectFileButton);
        filePanel.add(selectStopWordsButton);
        topPanel.add(filePanel);
        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        extractTagsButton = new JButton("Extract Tags");
        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });
        saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTagsToFile();
            }
        });
        bottomPanel.add(extractTagsButton);
        bottomPanel.add(saveTagsButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            selectedFileLabel.setText("Selected File: " + file.getName());
        }
    }

    private void selectStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            stopWordsSet = loadStopWords(file);
        }
    }

    private Set<String> loadStopWords(File file) {
        Set<String> stopWords = new TreeSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String word;
            while ((word = br.readLine()) != null) {
                stopWords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void extractTags() {
        String filePath = "C:\\Users\\Earl\\IdeaProjects\\TagExtractor\\src\\CAP.txt";

        File selectedFile = new File(filePath);
        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, "File not found: " + filePath);
            return;
        }

        if (stopWordsSet == null) {
            JOptionPane.showMessageDialog(this, "Please select stop words file.");
            return;
        }
        tagFrequencyMap = new HashMap<>();

        try (Scanner scanner = new Scanner(selectedFile)) {
            while (scanner.hasNext()) {
                String word = scanner.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!stopWordsSet.contains(word)) {
                    tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tagsTextArea.setText(sb.toString());
    }

    private void saveTagsToFile() {
        if (tagFrequencyMap == null) {
            JOptionPane.showMessageDialog(this, "No tags extracted yet.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Tags saved successfully.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TagExtractor().setVisible(true);
            }
        });
    }
}
