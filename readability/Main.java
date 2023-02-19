package readability;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    private static final int EASY_INPUT_MAX_LEN = 10;
    private static final int CHAR_IDX = 0;
    private static final int WORD_IDX = 1;
    private static final int SENT_IDX = 2;
    private static final int SYLL_IDX = 3;
    private static final int POLY_SYLL_IDX = 4;
    private static final int NUM_STAT = 5;

    private static void stageTwo() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] sentences = input.split("[?!\\.]");
        int numSentence = sentences.length;
        int numWords = 0;
        for (String sentence : sentences) {
            String[] words = sentence.trim().split(" ");
            numWords += words.length;
        }
        double averageWordsPerSentence = numWords * 1.0 /numSentence;

        if (averageWordsPerSentence > EASY_INPUT_MAX_LEN) {
            System.out.println("HARD");
        } else {
            System.out.println("EASY");
        }
    }
    private static String readInputFile(String fileName) {
        StringBuffer inputText = new StringBuffer();
        try {
            File inputFile = new File(fileName);
            Scanner fileReader = new Scanner(inputFile);
            while (fileReader.hasNextLine()) {
                inputText.append(fileReader.nextLine().trim() + " ");
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputText.toString();
    }

    private static int countSyllables(String word) {
        int numSyllable = 0;
        int i = 0;
        while (i < word.length()) {
            char ch = word.charAt(i);
            if (i == word.length() - 1 && ch == 'e') {
                i++;
                continue;
            }
            boolean found = false;
            switch (ch) {
                case 'a', 'e', 'i', 'o', 'u', 'y':
                    numSyllable++;
                    found = true;
                    break;
                default:
                    break;
            }
            if (found) {
                i += 2;
            } else {
                i += 1;
            }
        }
        if (numSyllable == 0) {
            numSyllable = 1;
        }
        return numSyllable;
    }
    private static int[] analyzeText(String text) {
        int[] wc = new int[NUM_STAT]; // sentence, word, character count
        wc[CHAR_IDX] = text.replace(" ", "").length();
        String[] sentences = text.trim().split("[\\.?!]");
        wc[SENT_IDX] = sentences.length;
        for (String sentence : sentences) {
            String[] words = sentence.trim().split("[ \n\t]");
            wc[WORD_IDX] += words.length;
            for (String word : words) {
                int nSyllables = countSyllables(word.toLowerCase());
                wc[SYLL_IDX] += nSyllables;
                if (nSyllables > 2) {
                    wc[POLY_SYLL_IDX]++;
                }
            }
        }
        return wc;
    }

    private static double automatedReadabilityIndex(int characters, int words, int senetences) {
        return 4.71 * characters / words + 0.5 * words / senetences - 21.43;
    }

    private static double fleschKincaidReadabilityTests(int syllables, int words, int sentences) {
        return 0.39 * words/sentences + 11.8 * syllables/words - 15.59;
    }

    private static double SMOGIndex(int polySyllables, int sentences) {
        return Math.sqrt(polySyllables * 30.0/sentences) + 3.1291;
    }

    private static double colemanLiauIndex(int characters, int words, int sentences) {
        double L = characters * 1.0 / words * 100;
        double S = sentences * 1.0/ words * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    private static int ageBracket(int score) {
        int lower = 5 + (score - 1);
        int upper;
        if (lower < 18) {
            upper = lower + 1;
        } else {
            upper = lower + 4;
        }
        return upper;
    }
    private static int scoreMessage(String indexName, double score) {
        int age = ageBracket((int) Math.ceil(score));
        System.out.printf("%s: %.2f", indexName, score);
        System.out.printf(" (about %d-years-olds).\n", age);
        return age;
    }
    public static void main(String[] args) {
        String fileName = args[0];
        String inputText = readInputFile(fileName);
        int[] wc = analyzeText(inputText);
        System.out.printf("Words: %d\n", wc[WORD_IDX]);
        System.out.printf("Sentences: %d\n", wc[SENT_IDX]);
        System.out.printf("Characters: %d\n", wc[CHAR_IDX]);
        System.out.printf("Syllables: %d\n", wc[SYLL_IDX]);
        System.out.printf("Polysyllables: %d\n", wc[POLY_SYLL_IDX]);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.next();
        int age = 0;
        int numOptions = 0;
        System.out.println();
        if (option.equals("ARI") || option.equals("all")) {
            double score = automatedReadabilityIndex(wc[CHAR_IDX], wc[WORD_IDX], wc[SENT_IDX]);
            age += scoreMessage("Automated Readability Index", score);
            numOptions++;
        }
        if (option.equals("FK") || option.equals("all")) {
            double score = fleschKincaidReadabilityTests(wc[SYLL_IDX], wc[WORD_IDX], wc[SENT_IDX]);
            age += scoreMessage("Flesch–Kincaid readability tests", score);
            numOptions++;
        }
        if (option.equals("SMOG") || option.equals("all")) {
            double score = SMOGIndex(wc[POLY_SYLL_IDX], wc[SENT_IDX]);
            age += scoreMessage("Simple Measure of Gobbledygook", score);
            numOptions++;
        }
        if (option.equals("CL") || option.equals("all")) {
            double score = colemanLiauIndex(wc[CHAR_IDX], wc[WORD_IDX], wc[SENT_IDX]);
            age += scoreMessage("Coleman–Liau index", score);
            numOptions++;
        }
        double averageAge;
        if (option.equals("all")) {
            averageAge = age * 1.0 / numOptions;
        } else {
            averageAge = age;
        }

        System.out.printf("\nThis text should be understood in average by %.2f-year-olds.\n", averageAge);
    }
}
