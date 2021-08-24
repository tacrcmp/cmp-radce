package dp.categorization.ngram;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

public class NgramGenerator {

    private static List<String> generate(List<String> tokens, int ngramLenght) {
        List<String> ngramsIndexTuples = Lists.newArrayList();

        for (String word : tokens) {
            String markedWord = "_".concat(word).concat("_");
            char[] wordChars = markedWord.toCharArray();

            //markedWord.length() - 1 to not create nGram only with _ in the end of word
            for (int i = 0; i < markedWord.length() - 1; i++) {
                StringBuilder ngram = new StringBuilder();

                for (int j = i; j < i + ngramLenght; j++) {
                    if (j >= markedWord.length()) {
                        ngram.append("_");
                    } else {
                        ngram.append(wordChars[j]);
                    }
                }

                ngramsIndexTuples.add(ngram.toString());
            }
        }

        return ngramsIndexTuples;
    }

    public static List<String> generateAll(List<String> tokens, int minNgramLength, int maxNgramLength) {
        Preconditions.checkArgument(minNgramLength <= maxNgramLength);

        List<String> ngramsIndexTuples = Lists.newArrayList();
        for (int i = minNgramLength; i <= maxNgramLength; i++) {
            ngramsIndexTuples.addAll(generate(tokens, i));
        }

        return ngramsIndexTuples;
    }
}
