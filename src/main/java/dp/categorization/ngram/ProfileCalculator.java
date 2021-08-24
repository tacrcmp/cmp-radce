package dp.categorization.ngram;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dp.dto.Document;
import dp.preprocessing.LuceneUtils;
import dp.preprocessing.Tokenizer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProfileCalculator {

    public static List<String> calculateDocProfile(Document document) throws IOException {
        Map<String, Integer> profile = calculateNgramsFreqs(document);
        List<String> list = Lists.newArrayList(sortByFreq(profile).keySet());

        //TODO temporary solution - optimize
        return list.size() < 600 ? list.subList(0, list.size()) : list.subList(0, 600);
    }

    private static Map<String, Integer> calculateNgramsFreqs(Document document) throws IOException {
        //create tokens without stop words
        //List<String> tokens = LuceneUtils.analyze(document.getText());
        List<String> tokens = Tokenizer.tokenize(document.getText());

        //create doc's nGrams of length 2-5 and its profile
        List<String> ngrams = NgramGenerator.generateAll(tokens, 2, 5);

        Map<String, Integer> profile = Maps.newHashMap();
        for (String ngram : ngrams) {
            profile.putIfAbsent(ngram, 0);
            profile.put(ngram, profile.get(ngram) + 1);
        }

        return profile;
    }

    private static Map<String, Integer> sortByFreq(Map<String, Integer> profile) {
        return profile.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static List<String> calculateCategoryProfile(List<Document> categoryDocuments) throws IOException {
        Map<String, Integer> result = Maps.newLinkedHashMap();

        for (Document document : categoryDocuments) {
            Map<String, Integer> ngrams = calculateNgramsFreqs(document);

            for (Map.Entry<String, Integer> docEntry : ngrams.entrySet()) {
                result.putIfAbsent(docEntry.getKey(), 0);
                result.put(docEntry.getKey(), result.get(docEntry.getKey()) + docEntry.getValue());
            }
        }

        List<String> list = Lists.newArrayList(sortByFreq(result).keySet());
        //TODO temporary solution - optimize
        return list.size() < 600 ? list.subList(0, list.size()) : list.subList(0, 600);
    }

}
