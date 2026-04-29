package ru.nsu.ccfit.afeninets;
import java.io.*;
import java.util.*;

class WordStat {
    final String word;
    int count;

    public WordStat(String word) {
        this.word = word;
        this.count = 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WordStat wordStat = (WordStat) obj;
        return Objects.equals(word, wordStat.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    public void increment() {
        this.count++;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Need the name of the input file");
            return;
        }

        String filename = args[0];
        Set<WordStat> uniqueWords = new HashSet<>();

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));
            StringBuilder sb = new StringBuilder();
            int sym;

            while ((sym = reader.read()) != -1) {
                char chr = (char) sym;
                if (Character.isLetterOrDigit(chr)) {
                    sb.append(chr);
                } else {
                    if (!sb.isEmpty()) {
                        String word = sb.toString().toLowerCase();
                        WordStat tempStat = new WordStat(word);
                        boolean found = false;
                        for (WordStat existingStat : uniqueWords) {
                            if (existingStat.equals(tempStat)) {
                                existingStat.increment();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            uniqueWords.add(tempStat);
                        }
                        sb.setLength(0);
                    }
                }
            }
            //обработка последнего слова
            if (!sb.isEmpty()) {
                String word = sb.toString().toLowerCase();
                WordStat tempStat = new WordStat(word);
                boolean found = false;
                for (WordStat existingStat : uniqueWords) {
                    if (existingStat.equals(tempStat)) {
                        existingStat.increment();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    uniqueWords.add(tempStat);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("failed to read file", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        List<WordStat> sorted = new ArrayList<>(uniqueWords);
        sorted.sort(new Comparator<>() {
            @Override
            public int compare(WordStat a, WordStat b) {
                return b.getCount() - a.getCount();
            }
        });

        //вычисляем общее количество слов
        int totalWords = 0;
        for (WordStat ws : sorted) {
            totalWords += ws.getCount();
        }

        //Вывод результатов
        String outputFilename = "output.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(outputFilename))) {
            pw.println("Word,Freq,Percent");
            for (WordStat ws : sorted) {
                double percent = ((double) ws.getCount() / totalWords) * 100;
                pw.printf("%s,  %d,  %.3f%n", ws.getWord(), ws.getCount(), percent);
            }
            System.out.println("Results written to " + outputFilename);
        } catch (IOException e) {
            System.err.println("Error writing CSV file: " + e.getMessage());
        }
    }
}