package dp.categorization.ngram;

import java.util.List;

public class DistanceMeasurement {

    public static int getDistance(List<String> profile1, List<String> profile2){
        int result = 0;

        for (int i = 0; i < profile1.size(); i++) {
            String nGram = profile1.get(i);

            int profile2Position = profile2.indexOf(nGram);
            if(profile2Position == -1){
                profile2Position = profile2.size();
            }

            result += Math.abs(i - profile2Position);
        }

        return result;
    }
}
