package dp.preprocessing;

import com.google.common.collect.Sets;
import dp.categorization.ngram.NgramCategorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Component
public class StopWordsList {
    private static final Set<String> DEFAULT_STOP_WORDS = Sets.newHashSet("a", "v", "se", "na", "je", "že", "o",
            "s", "z", "do", "i", "to", "k", "ve", "pro", "za", "by", "ale", "si", "po", "jako", "podle", "od", "jsem",
            "tak", "jsou", "které", "který", "jeho", "však", "bude", "nebo", "už", "jen", "byl", "jak", "u", "co",
            "při", "až", "aby", "má", "když", "než", "ze", "která", "před", "být", "také", "bylo", "jsme", "není",
            "jejich", "ještě", "ani", "mezi", "byla", "své", "již", "pak", "kteří", "další", "proti", "tím", "může",
            "řekl", "tom", "kde", "či", "tedy", "pouze", "te", "tu", "ten", "tam", "té", "kdy", "asi", "ho", "abych",
            "on", "ona", "ono", "mu", "jemu", "mnou", "mě", "mně", "ke", "byli", "tyto", "svou", "my", "vy", "oni",
            "já", "ty", "jimi", "jim", "tento", "tato", "toto", "tito", "kam", "protože", "bych", "bychom", "teď", "opravdu",
            "takže", "no", "tady", "ji", "třeba", "toho", "vlastně", "mám", "máme", "máte", "ta", "ti", "to", "mi", "dát",
            "zase", "právě", "zatím", "tuhle");

    private static final Logger LOGGER = LoggerFactory.getLogger(StopWordsList.class);

    private static Set<String> stopWords;

    private static final String PROPERTY_STOP_WORDS_PATH = "stopwords.path";

    /*private static Properties properties;

    static {
        properties = new Properties();
        ClassLoader loader = StopWordsList.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream("application.properties");
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Value("${stopwords.path}")
    private String stopWordsPath;

    private static String STOP_WORDS_PATH;

    @Value("${stopwords.path}")
    public void setStopWordsPathStatic(String stopWordsPath){STOP_WORDS_PATH = stopWordsPath;
    }

    public static Set<String> getDefaultStopWords() {
        if (stopWords != null) {
            LOGGER.info("Using custom pre-loaded stop words list.");
            return stopWords;
        }

        //String stopWordsPath = properties.getProperty(PROPERTY_STOP_WORDS_PATH);
        if (STOP_WORDS_PATH != null && !STOP_WORDS_PATH.isEmpty()) {
            LOGGER.info("Attempting to load stop words from: {}", STOP_WORDS_PATH);
            try (BufferedReader br = new BufferedReader(new FileReader(STOP_WORDS_PATH))) {
                String line;
                stopWords = new HashSet<>();
                while ((line = br.readLine()) != null) {
                    String stopWord = line.toLowerCase().trim();
                    if (stopWord.length() > 0)
                    {
                        stopWords.add(line.toLowerCase().trim());
                    }
                }
                LOGGER.info("Returning custom stop words ({}).", stopWords.size());
                return stopWords;
            } catch (IOException e) {
                LOGGER.error("Reading stop words from file {} failed, returning default stop words.", STOP_WORDS_PATH, e);
                stopWords = null;
                return DEFAULT_STOP_WORDS;
            }
        }

        LOGGER.info("No stop words file specified. Returning default stop words list.");
        return DEFAULT_STOP_WORDS;

    }

}
