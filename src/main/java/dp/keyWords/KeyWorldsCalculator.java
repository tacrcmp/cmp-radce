package dp.keyWords;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface KeyWorldsCalculator {
    Map<Integer, List<String>> calculateCatKeywords() throws IOException;
}
