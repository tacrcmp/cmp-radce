package dp.categorization.ngram;

import com.google.common.collect.Lists;
import dp.categorization.ngram.NgramGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class NgramGeneratorTest {

    @Test
    public void shouldGenerate() {
        String stringToCXreateNGrams = "TEXT";

        List<String> expected = Lists.newArrayList();
        expected.add("_T");
        expected.add("TE");
        expected.add("EX");
        expected.add("XT");
        expected.add("T_");
        expected.add("_TE");
        expected.add("TEX");
        expected.add("EXT");
        expected.add("XT_");
        expected.add("T__");
        expected.add("_TEX");
        expected.add("TEXT");
        expected.add("EXT_");
        expected.add("XT__");
        expected.add("T___");

        List<String> result = NgramGenerator.generateAll(Lists.newArrayList(stringToCXreateNGrams), 2, 4);

        Assert.assertTrue(expected.containsAll(result) && result.containsAll(expected));
    }
}
