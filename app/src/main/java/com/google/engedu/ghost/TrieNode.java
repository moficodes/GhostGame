package com.google.engedu.ghost;

import java.util.*;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;
    private String text;
    private Random random = new Random(System.nanoTime());
    private int level;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String word) {
        TrieNode t = this;
        HashMap<String, TrieNode> child = this.children;

        for (int i = 0; i <= word.length(); i++) {
            String s = word.substring(0, i);
            if (child.containsKey(s)) {
                t = child.get(s);
            } else {
                t = new TrieNode();
                t.text = s;
                t.level = i + 1;
                child.put(s, t);
            }
            child = t.children;
        }
        t.isWord = true;
    }

    public boolean isWord(String s) {
        TrieNode t = searchNode(s);

        if (t != null && t.isWord)
            return true;
        else
            return false;
    }

    public String getAnyWordStartingWith(String s) {
        if (s.isEmpty()) {
            return Character.toString((char) (random.nextInt(26) + 'a'));
        }
        TrieNode t = searchNode(s);
        if (t == null) {
            return null;
        }
        String str = null;
        while (!t.isWord) {
            HashMap<String, TrieNode> child = t.children;
            ArrayList<String> keys = new ArrayList<>(child.keySet());
            int index = random.nextInt(keys.size());
            str = keys.get(index);
            t = child.get(str);
        }
        return str;
    }

    private String getText() {
        ArrayList<String> keys = new ArrayList<>(this.children.keySet());
        int index = random.nextInt(keys.size());
        return keys.get(index);
    }


    public String getGoodWordStartingWith(String s) {
        if (s.isEmpty()) {
            return Character.toString((char) (random.nextInt(26) + 'a'));
        }
        String result = s;
        TrieNode t = searchNode(s);
        if (t == null) {
            return null;
        }
//        if(s.length()<3){
//            return getAnyWordStartingWith(s);
//        }

        Queue<TrieNode> toVisit = new LinkedList<>();
        toVisit.add(t);
        while (!toVisit.isEmpty()) {
            TrieNode current = toVisit.remove();
            if (current.isWord && current.level % 2 != s.length() % 2) {
                return current.text;
            }
            for (String str : current.children.keySet()) {
                TrieNode child = current.children.get(str);
                if (child != null && !child.isWord) {
                    toVisit.add(child);
                }
                else {
                    break;
                }
            }
        }

        return getAnyWordStartingWith(s);
    }

    public TrieNode searchNode(String str) {
        HashMap<String, TrieNode> child = this.children;
        TrieNode t = null;
        for (int i = 0; i <= str.length(); i++) {
            String s = str.substring(0, i);
            if (child.containsKey(s)) {
                t = child.get(s);
                child = t.children;
            } else {
                return null;
            }
        }
        return t;
    }
}