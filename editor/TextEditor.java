package editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TextEditor extends JFrame {
    final int WIDTH = 720;
    final int HEIGHT = 400;

    final String PROGRAM_NAME = "Text Editor";
    JTextArea textArea;
    Container container;
    JTextField searchField;
    JFileChooser fileChooser;
    File selectedFile;
    JCheckBox regexCheckBox;
    List<Integer> matchedLocations;
    int currentMatched;
    public TextEditor() {
        setTitle(PROGRAM_NAME);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        container = getContentPane();

        textArea = new JTextArea();
        textArea.setName("TextArea");
        textArea.setLineWrap(true);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setName("ScrollPane");
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        container.add(scrollableTextArea, BorderLayout.CENTER);
        container.add(topBar(), BorderLayout.NORTH);

        createMenu();

        fileChooser = new JFileChooser();
        fileChooser.setName("FileChooser");
        container.add(fileChooser);

        container.add(new JLabel(" "), BorderLayout.SOUTH);
        container.add(new JLabel("  "), BorderLayout.WEST);
        container.add(new JLabel("  "), BorderLayout.EAST);
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createSearchMenu());
        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        JMenuItem loadMenu = new JMenuItem("Load");
        loadMenu.setName("MenuOpen");
        loadMenu.addActionListener(actionEvent -> loadFile());

        JMenuItem saveMenu = new JMenuItem("Save");
        saveMenu.setName("MenuSave");
        saveMenu.addActionListener(actionEvent -> saveFile());

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.setName("MenuExit");
        exitMenu.addActionListener(actionEvent -> dispose());
        fileMenu.add(loadMenu);
        fileMenu.add(saveMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenu);

        return fileMenu;
    }

    private JMenu createSearchMenu() {
        JMenu searchMenu = new JMenu(("Search"));
        searchMenu.setName("MenuSearch");

        JMenuItem startSearch = new JMenuItem("Start Search");
        startSearch.setName("MenuStartSearch");
        startSearch.addActionListener(actionEvent -> searchHandler());

        JMenuItem prevMatch = new JMenuItem("Previous match");
        prevMatch.setName("MenuPreviousMatch");
        prevMatch.addActionListener(actionEvent -> prevMatchHandler());

        JMenuItem nextMatch = new JMenuItem("Next match");
        nextMatch.setName("MenuNextMatch");
        nextMatch.addActionListener(actionEvent -> nextMatchHandler());

        JMenuItem regexSearch = new JMenuItem("Use regular expressions");
        regexSearch.setName("MenuUseRegExp");
        regexSearch.addActionListener(actionEvent -> {
            regexCheckBox.setSelected(true);
            searchHandler();
        });

        searchMenu.add(startSearch);
        searchMenu.add(prevMatch);
        searchMenu.add(nextMatch);
        searchMenu.add(regexSearch);

        return searchMenu;
    }
    private void loadFile() {
        try {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                textArea.setText(new String(Files.readAllBytes((Paths.get(
                        selectedFile.getPath()
                )))));
                setTitle(PROGRAM_NAME + ": " + selectedFile.getName());
            }
        } catch (IOException e) {
            textArea.setText("");
            setTitle(PROGRAM_NAME);
            JOptionPane.showMessageDialog(container,
                    "Error! cannot read file: " +
                            searchField.getText());
        }
    }

    private void saveFile() {
        if (selectedFile == null) {
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }
        }
        try (FileWriter writer = new FileWriter(selectedFile)) {
            writer.write(textArea.getText());
            setTitle(PROGRAM_NAME + ": " + selectedFile.getName());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(container,
                    "Error! cannot write file: " +
                            searchField.getText());
        }
    }
    private JPanel topBar() {
        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(30);
        searchField.setName("SearchField");
        searchField.setPreferredSize(new Dimension(50, 32));

        Icon saveImgIcon = loadIcon("save.png");
        JButton saveButton = new JButton(saveImgIcon);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> saveFile());

        Icon loadImgIcon = loadIcon("folder.png");
        JButton loadButton = new JButton(loadImgIcon);
        loadButton.setName("OpenButton");
        loadButton.addActionListener(actionEvent -> loadFile());

        Icon searchImgIcon = loadIcon("search.png");
        JButton searchButton = new JButton(searchImgIcon);
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(actionEvent -> searchHandler());

        Icon prevImgIcon = loadIcon("previous.png");
        JButton prevMatchButton = new JButton(prevImgIcon);
        prevMatchButton.setName("PreviousMatchButton");
        prevMatchButton.addActionListener(actionEvent -> prevMatchHandler());

        Icon nextImgIcon = loadIcon("next.png");
        JButton nextMatchButton = new JButton(nextImgIcon);
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(actionEvent -> nextMatchHandler());

        regexCheckBox = new JCheckBox("Use regex");
        regexCheckBox.setName("UseRegExCheckbox");

        topBar.add(saveButton);
        topBar.add(loadButton);
        topBar.add(searchField);
        topBar.add(searchButton);
        topBar.add(prevMatchButton);
        topBar.add(nextMatchButton);
        topBar.add(regexCheckBox);

        return topBar;
    }

    private void searchHandler() {
        String searchKey = searchField.getText();
        if (! searchKey.isBlank()) {
            if (regexCheckBox.isSelected()) {
                doSearchWithRegex();
            } else {
                doSearch();
            }
            if (matchedLocations.size() > 0) {
                currentMatched = 0;
                int matchedPosition = matchedLocations.get(currentMatched);
                highlightMatchedLocation(matchedPosition,  searchKey.length());
            }
        }
    }
    private void prevMatchHandler() {
        if (matchedLocations != null) {
            String searchKey = searchField.getText();
            int numMatches = matchedLocations.size();
            if (numMatches > 0) {
                currentMatched--;
                if (currentMatched < 0) {
                    currentMatched = numMatches - 1;
                }
                int matchedPosition = matchedLocations.get(currentMatched);
                highlightMatchedLocation(matchedPosition, searchKey.length());
            }
        }
    }
    private void nextMatchHandler() {
        if (matchedLocations != null) {
            String searchKey = searchField.getText();
            int numMatches = matchedLocations.size();
            if (numMatches > 0) {
                currentMatched++;
                if (currentMatched > numMatches - 1) {
                    currentMatched = 0;
                }
                int matchedPosition = matchedLocations.get(currentMatched);
                highlightMatchedLocation(matchedPosition, searchKey.length());
            }
        }
    }
    private Icon loadIcon(String resourceName) {
        System.out.println(resourceName);
        URL url = this.getClass().getResource(resourceName);
        System.out.println(url);
        return (url == null) ? null : new ImageIcon(url);
    }
    private void highlightMatchedLocation(int matchedPosition, int matchedLength) {
        textArea.setCaretPosition(matchedPosition + matchedLength);
        textArea.select(matchedPosition, matchedPosition + matchedLength);
        textArea.grabFocus();
    }
    private void doSearch() {
        Thread thread = new Thread(() -> {
            String searchKey = searchField.getText();
            matchedLocations = new ArrayList<>();
            int fromIndex = 0;
            while (true) {
                int loc = textArea.getText().indexOf(searchKey, fromIndex);
                if (loc == -1) {
                    break;
                } else {
                    matchedLocations.add(loc);
                    fromIndex = loc + searchKey.length();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("search interrupted.");
        }
    }

    private void doSearchWithRegex() {
        Thread thread = new Thread(() -> {
            String searchKey = searchField.getText();
            matchedLocations = new ArrayList<>();
            Pattern pattern = Pattern.compile(searchKey);
            Matcher matcher = pattern.matcher(textArea.getText());
            matchedLocations = new ArrayList<>();
            while (matcher.find()) {
                matchedLocations.add(matcher.start());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("search interrupted.");
        }
    }
}