package swyp_11.ssubom.global.nickname;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class

VocabularyPool {
    private static final String MODIFIERS_PATH = "static/nickname_words/modifiers.txt";
    private static final String NOUNS_PATH = "static/nickname_words/nouns.txt";
    private static final String ACTIVITIES_PATH = "static/nickname_words/activities.txt";
    private static final String QUALIFIERS_PATH = "static/nickname_words/qualifiers.txt";

    private List<String> modifiers;
    private List<String> nouns;
    private List<String> activities;
    private List<String> qualifiers;

    @PostConstruct
    public void init() {
        try {
            this.modifiers = loadFile(MODIFIERS_PATH);
            this.nouns = loadFile(NOUNS_PATH);
            this.activities = loadFile(ACTIVITIES_PATH);
            this.qualifiers = loadFile(QUALIFIERS_PATH);
            validate();
        } catch (IOException e) {
            throw new IllegalStateException("nickname용 단어들이 로드되지 못함", e);
        }
    }

    private void validate() { // 중복 check
        Set<String> allWords = new HashSet<>();
        allWords.addAll(modifiers);
        allWords.addAll(nouns);
        allWords.addAll(activities);
        allWords.addAll(qualifiers);

        int totalSize = modifiers.size() + nouns.size() +
                activities.size() + qualifiers.size();

        if (allWords.size() != totalSize) {
            throw new IllegalStateException("어휘 풀에 중복된 단어가 있습니다");
        }
    }

    private List<String> loadFile(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream is = classLoader.getResourceAsStream(path)) {

            if (is == null) {
                throw new IOException("Resource file not found: " + path);
            }

            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8); //한글 깨짐 방지
                 BufferedReader reader = new BufferedReader(isr)) {

                return reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isBlank())
                        .toList();
            }
        }
    }

    public String getModifier(int index) {
        return modifiers.get(index % modifiers.size());
    }

    public String getNoun(int index) {
        return nouns.get(index % nouns.size());
    }

    public String getActivity(int index) {
        return activities.get(index % activities.size());
    }

    public String getQualifier(int index) {
        return qualifiers.get(index % qualifiers.size());
    }

    public int getModifierPoolSize() { return modifiers.size(); }
    public int getNounPoolSize() { return nouns.size(); }
    public int getActivityPoolSize() { return activities.size(); }
    public int getQualifierPoolSize() { return qualifiers.size(); }
}
