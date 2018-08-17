package tds.support.tool.utils;

import org.apache.commons.lang3.tuple.Pair;
import tds.trt.model.TDSReport;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Created by Greg Charles on 7/18/18.
 */
public class ListClassifier<T,U> {
    private final List<T> leftOnly = new ArrayList<>();
    private final List<U> rightOnly = new ArrayList<>();
    private final List<Pair<T,U>> intersections = new ArrayList<>();

    public ListClassifier(List<T> leftList, List<U> rightList, BiFunction<T, U, Boolean> matcher) {

        leftOnly.addAll(leftList);
        rightOnly.addAll(rightList);

        for (T leftElement : leftList) {
            for (U rightElement : rightList) {
                if (matcher.apply(leftElement, rightElement)) {
                    intersections.add(Pair.of(leftElement, rightElement));
                }
            }
        }

        for (Pair<T,U> pair: intersections) {
            leftOnly.remove(pair.getLeft());
            rightOnly.remove(pair.getRight());
        }
    }

    public List<T> getLeftOnly() {
        return leftOnly;
    }

    public List<U> getRightOnly() {
        return rightOnly;
    }

    public List<Pair<T, U>> getIntersections() {
        return intersections;
    }
}
