package bugbusters.everyonecodes.java.usermanagement.service;

import java.util.List;

public final class RatingCalcUtil {

    private RatingCalcUtil() { }

    public static Double calculateRating(List<Integer> ratings) {
        if (ratings.isEmpty()) return null;
        return ratings.stream()
                .mapToDouble(Double::valueOf)
                .sum() / ratings.size();
    }
}
