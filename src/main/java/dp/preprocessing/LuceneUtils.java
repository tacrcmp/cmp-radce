package dp.preprocessing;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class LuceneUtils {

    public static List<String> analyze(String text) throws IOException {
        try(CzechAnalyzer analyzer = new CzechAnalyzer(CzechAnalyzer.getDefaultStopSet())) {
            return process(analyzer, text);
        }
    }

    public static List<String> analyze(String text, Set<String> stopWords) throws IOException {
        try(CzechAnalyzer analyzer = new CzechAnalyzer(new CharArraySet(stopWords, true))) {
            return process(analyzer, text);
        }
    }

    private static List<String> process(CzechAnalyzer analyzer, String text) throws IOException {
        List<String> tokens = Lists.newArrayList();

        TokenStream tokenStream = analyzer.tokenStream("doc", text);
        tokenStream = new LowerCaseFilter(tokenStream);
        tokenStream = new StopFilter(tokenStream, CharArraySet.copy(analyzer.getStopwordSet()));
        tokenStream = new PatternReplaceFilter(tokenStream, Pattern.compile("[0-9]*"), "", true);
        CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while(tokenStream.incrementToken()){
            String token = charTermAttribute.toString();

            if(!token.isEmpty()) {
                tokens.add(token);
            }
        }

        tokenStream.end();
        tokenStream.close();

        return tokens;
    }
}
