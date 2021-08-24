package dp.preprocessing;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String NAME_REGEX = "(?<!^)(?<!\\.\\s?)\\b[\\p{Lu}][\\p{Ll}]+?\\b";
    private static final String WORD_REGEX = "\\b[\\p{Lu}\\p{Ll}]+\\b";
    private static final String NUMBER_REGEX = "[0-9]*";
    private static final String SPECIAL_CHAR_REGEX = "[^\\p{Lu}\\p{Ll}[0-9].\\s+]";
    private static final String END_SENTENCE_REGEX = "[?!]";
    private static final String DOT_REGEX = "\\.";
    private static final String WHITESPACES_REGEX = "(\\s){2,}";
    private static final Set<String> STOP_WORDS = StopWordsList.getDefaultStopWords();

    public static List<String> tokenize(String text) {
        String filtered = text;
        filtered = filtered.replaceAll(END_SENTENCE_REGEX, ".");
        filtered = filtered.replaceAll(NUMBER_REGEX, "");
        filtered = filtered.replaceAll(SPECIAL_CHAR_REGEX, "");
        filtered = filtered.replaceAll(WHITESPACES_REGEX, " ");
        filtered = filtered.replaceAll(NAME_REGEX, "");
        filtered = filtered.replaceAll(DOT_REGEX, "");
        filtered = filtered.replaceAll(WHITESPACES_REGEX, " ");
        filtered = filtered.toLowerCase();

        List<String> result = Lists.newArrayList();
        Pattern wordPattern = Pattern.compile(WORD_REGEX);
        Matcher match = wordPattern.matcher(filtered);

        while (match.find()) {
            String word = match.group();

            if(!STOP_WORDS.contains(word)) {
                result.add(word);
            }
        }

        return result;
    }

    public static Map<String, Set<String>> getStemsTerms(String text) throws IOException {
        Map<String, Set<String>> result = Maps.newHashMap();

        List<String> tokens = tokenize(text);
        for (String token : tokens) {
            List<String> stem = LuceneUtils.analyze(token, STOP_WORDS);
            Preconditions.checkArgument(stem.size() <=1);

            if (stem.isEmpty()){
                result.putIfAbsent(token, Sets.newHashSet());
                result.get(token).add(token);
            } else {
                result.putIfAbsent(stem.get(0), Sets.newHashSet());
                result.get(stem.get(0)).add(token);
            }
        }

        return result;
    }
}
