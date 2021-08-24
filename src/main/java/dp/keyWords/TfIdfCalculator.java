package dp.keyWords;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dp.dao.CategoryDAO;
import dp.dao.DocumentDAO;
import dp.dto.Category;
import dp.dto.Document;
import dp.preprocessing.LuceneUtils;
import dp.preprocessing.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TfIdfCalculator implements KeyWorldsCalculator {
    private final DocumentDAO documentDAO;
    private final CategoryDAO categoryDAO;

    @Autowired
    public TfIdfCalculator(DocumentDAO documentDAO, CategoryDAO categoryDAO) {
        this.documentDAO = documentDAO;
        this.categoryDAO = categoryDAO;
    }

    @Override
    public Map<Integer, List<String>> calculateCatKeywords() throws IOException {
        Map<Integer, List<String>> result = Maps.newHashMap();

        int docsCounter = 0;
        Map<String, Set<String>> stemsTokensMap = Maps.newHashMap();
        Map<Integer, List<String>> categoriesTerms = Maps.newHashMap();
        List<Integer> categoryIds = categoryDAO.getCategories().stream().map(Category::getId).collect(Collectors.toList());

        for (Integer categoryId : categoryIds) {
            List<String> docsTexts = documentDAO.getDocumentByCategory(categoryId).stream().map(Document::getText).collect(Collectors.toList());
            docsCounter += docsTexts.size();

            for (String text : docsTexts) {
                Map<String, Set<String>> tokens = Tokenizer.getStemsTerms(text);
                for (Map.Entry<String, Set<String>> entry : tokens.entrySet()) {
                    stemsTokensMap.putIfAbsent(entry.getKey(), Sets.newHashSet());
                    stemsTokensMap.get(entry.getKey()).addAll(entry.getValue());
                }

                categoriesTerms.putIfAbsent(categoryId, Lists.newArrayList());
                categoriesTerms.get(categoryId).addAll(tokens.keySet());
            }
        }

        for (Integer categoryId : categoryIds) {
            List<String> categoryKeywords = getCategoryKeywords(categoryId, categoriesTerms, docsCounter);
            if (categoryKeywords.size() > 5) {
                categoryKeywords = categoryKeywords.subList(0, 5);
            }

            for (String categoryKeyword : categoryKeywords) {
                result.putIfAbsent(categoryId, Lists.newArrayList());
                result.get(categoryId).add(stemsTokensMap.get(categoryKeyword).iterator().next());
            }
        }

        return result;
    }

    private List<String> getCategoryKeywords(Integer categoryId, Map<Integer, List<String>> categoriesTerms, int docsCount) {
        Map<String, Double> termTfIdfMap = Maps.newLinkedHashMap();

        List<String> categoryTerms = categoriesTerms.get(categoryId);
        if(categoryTerms == null){
            return Lists.newArrayList();
        }

        for (String term : categoryTerms) {
            double tf = calculateTermTf(term, categoryTerms);
            double idf = calculateTermIdf(term, categoriesTerms, docsCount);

            Double old = termTfIdfMap.putIfAbsent(term, tf * idf);
            if (old != null) {
                Preconditions.checkArgument(termTfIdfMap.get(term).equals(tf * idf));
            }
        }

        termTfIdfMap = termTfIdfMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return Lists.newArrayList(termTfIdfMap.keySet());
    }

    private double calculateTermTf(String term, List<String> categoryTerms) {
        int frequency = Collections.frequency(categoryTerms, term);

        return frequency / (double) categoryTerms.size();
    }

    private double calculateTermIdf(String term, Map<Integer, List<String>> categoriesTerms, int docsCount) {
        int termInCatsCounter = 0;
        for (Map.Entry<Integer, List<String>> entry : categoriesTerms.entrySet()) {
            int frequency = Collections.frequency(entry.getValue(), term);

            if(frequency > 0) {
                termInCatsCounter++;
            }
        }

        return Math.log(categoriesTerms.size() / (double) termInCatsCounter);
    }
}
