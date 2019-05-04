package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;
    private Random random = new Random(System.currentTimeMillis());

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(word);
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix.isEmpty()) {
            return Character.toString((char) (random.nextInt(26) + 'a'));
        }
        int index = binarySearch(words, 0, words.size() - 1, prefix);
        return index >= 0 ? words.get(index) : null;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        if (prefix.isEmpty()) {
            return words.get(random.nextInt(words.size()));
        }
        ArrayList<String> even = new ArrayList<>();
        ArrayList<String> odd = new ArrayList<>();

        int index = binarySearch(words, 0, words.size() - 1, prefix);
        if (index < 0) {
            return null;
        } else {
            String word;
            int positive = index;
            while (positive != words.size()) {
                word = words.get(positive);
                if (word.startsWith(prefix)) {
                    if (word.length() % 2 == 0) {
                        even.add(word);
                    } else {
                        odd.add(word);
                    }
                } else {
                    break;
                }
                positive++;
            }
            int negative = index - 1;
            while (negative > 0) {
                word = words.get(negative);
                if (word.startsWith(prefix)) {
                    if (word.length() % 2 == 0) {
                        even.add(word);
                    } else {
                        odd.add(word);
                    }
                } else {
                    break;
                }
                negative--;
            }
        }


        if (!even.isEmpty() && !odd.isEmpty()) {
            if (prefix.length() % 2 == 0) {
                return even.get(random.nextInt(even.size()));
            } else {
                return odd.get(random.nextInt(odd.size()));
            }
        } else if (even.isEmpty()) {
            return odd.get(random.nextInt(odd.size()));
        } else {
            return even.get(random.nextInt(even.size()));
        }
    }

    public int binarySearch(ArrayList<String> words, int left, int right, String prefix) {
        if (left == right) {
            return -1;
        }
        int mid = (left + right) >>> 1;

        if (words.get(mid).startsWith(prefix)) {
            return (mid);
        }
        if (prefix.compareTo(words.get(mid)) < 0) {
            return binarySearch(words, left, mid, prefix);
        } else if (prefix.compareTo(words.get(mid)) > 0) {
            return binarySearch(words, mid + 1, right, prefix);
        } else {
            return (mid);
        }
    }

}