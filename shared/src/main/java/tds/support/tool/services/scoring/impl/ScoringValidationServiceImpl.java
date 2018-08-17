package tds.support.tool.services.scoring.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import tds.support.job.ScoringValidationReport;
import tds.support.tool.services.scoring.ScoringValidationService;
import tds.support.tool.utils.ListClassifier;
import tds.trt.model.TDSReport;
import tds.trt.model.TDSReport.Opportunity.Item;
import tds.trt.model.TDSReport.Opportunity.Score;

import javax.validation.constraints.NotNull;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static tds.trt.model.TDSReport.*;

/**
 * Compares two TDS Reports and produces a difference report.
 *
 * Created by Greg Charles on 7/17/18.
 */
@Service
public class ScoringValidationServiceImpl implements ScoringValidationService {

    @Override
    public ScoringValidationReport validateScoring(String jobId,
                                                   @NotNull TDSReport original, @NotNull TDSReport rescored) {
        return new ScoringValidationReport(jobId, getDiffs(original, rescored));
    }

    private Map<String, Object> getDiffs(@NotNull TDSReport original, @NotNull TDSReport rescored) {
        Map<String, Object> diffs = new LinkedHashMap<>();

        addDifferences("test", getDiffs(original.getTest(), rescored.getTest()), diffs);
        addDifferences("examinee", getDiffs(original.getExaminee(), rescored.getExaminee()), diffs);
        addDifferences("opportunity", getDiffs(original.getOpportunity(), rescored.getOpportunity()), diffs);

        return diffs;
    }

    private Map<String, Object> getDiffs(Test original, Test rescored) {
        if (original == null || rescored == null) {
            return getDiffMap(original, rescored);
        }

        Map<String, Object> diffs = new LinkedHashMap<>();

        addDifferences("testId", getDiffs(original.getTestId(), rescored.getTestId()), diffs);
        addDifferences("name", getDiffs(original.getName(), rescored.getName()), diffs);

        return diffs;
    }

    private Map<String,Object> getDiffs(Examinee original, Examinee rescored) {
        if (original == null || rescored == null) {
            return getDiffMap(original, rescored);
        }

        Map<String,Object> diffs = new LinkedHashMap<>();
        addDifferences("key", getDiffs(original.getKey(), rescored.getKey()), diffs);
        return diffs;
    }

    private Map<String,Object> getDiffs(Opportunity original, Opportunity rescored) {
        if (original == null || rescored == null) {
            return getDiffMap(original, rescored);
        }

        Map<String, Object> diffs = new LinkedHashMap<>();

        addDifferences("scores", getScoreDiffs(original.getScore(), rescored.getScore()), diffs);
        addDifferences("items", getItemDiffs(original.getItem(), rescored.getItem()), diffs);

        return diffs;
    }

    private List<Map<String, Object>> getScoreDiffs(List<Score> original, List<Score> rescored) {
        List<Map<String,Object>> scoreDiffs = new ArrayList<>();

        ListClassifier<Score, Score> scoreListClassifier = new ListClassifier<>(
                original, rescored, ScoringValidationServiceImpl::match);

        for (Score score : scoreListClassifier.getLeftOnly()) {
            scoreDiffs.add(getDiffs("removed", score, new Score()));
        }

        for (Score score : scoreListClassifier.getRightOnly()) {
            scoreDiffs.add(getDiffs("added", new Score(), score));
        }

        for (Pair<Score, Score> scorePair : scoreListClassifier.getIntersections()) {
            if (changed(scorePair.getLeft(), scorePair.getRight())) {
                scoreDiffs.add(getDiffs("modified", scorePair.getLeft(), scorePair.getRight()));
            }
        }

        return scoreDiffs;
    }

    private Map<String,Object> getDiffs(
            @NotNull final String status, @NotNull final Score original, @NotNull final Score rescore)
    {
        Map<String, Object> diffs = new LinkedHashMap<>();
        diffs.put("status", status);

        // The basis for the identifier fields is the original unless this is an added Score.
        Score basis = status.equals("added") ? rescore : original;

        // Fields we matched on
        Map<String,Object> identifiers = new HashMap<>();
        diffs.put("identifier", identifiers);

        identifiers.put("measureOf", basis.getMeasureOf());
        identifiers.put("measureLabel", basis.getMeasureLabel());


        // Fields we checked for changes
        diffs.put("value", getDiffs(original.getValue(), rescore.getValue()));
        diffs.put("standardError", getDiffs(original.getStandardError(), rescore.getStandardError()));

        return diffs;
    }

    private static boolean match(Score a, Score b) {
        return equivalent(a.getMeasureOf(), b.getMeasureOf()) &&
                equivalent(a.getMeasureLabel(), b.getMeasureLabel());
    }

    private static boolean changed(Score a, Score b) {
        return !equivalent(a.getValue(), b.getValue()) ||
                !equivalent(createDouble(a.getStandardError()), createDouble(b.getStandardError()));
    }


    private List<Map<String, Object>> getItemDiffs(List<Item> original, List<Item> rescored) {
        List<Map<String, Object>> itemDiffs = new ArrayList<>();

        ListClassifier<Item, Item> itemListClassifier = new ListClassifier<>(
                original, rescored, ScoringValidationServiceImpl::match);

        for (Item item : itemListClassifier.getLeftOnly()) {
            itemDiffs.add(getDiffs("removed", item, new Item()));
        }

        for (Item item : itemListClassifier.getRightOnly()) {
            itemDiffs.add(getDiffs("added", new Item(), item));
        }

        for (Pair<Item, Item> itemPair : itemListClassifier.getIntersections()) {
            if (changed(itemPair.getLeft(), itemPair.getRight())) {
                itemDiffs.add(getDiffs("modified", itemPair.getLeft(), itemPair.getRight()));
            }
        }

        return itemDiffs;
    }

    private Map<String,Object> getDiffs(
            @NotNull final String status, @NotNull final Item original, @NotNull final Item rescore)
    {
        Map<String, Object> diffs = new LinkedHashMap<>();
        diffs.put("status", status);

        // The basis for the identifier fields is the original unless this is an added Item.
        Item basis = status.equals("added") ? rescore : original;

        // Fields we matched on
        Map<String,Object> identifiers = new HashMap<>();
        identifiers.put("position", basis.getPosition());
        identifiers.put("key", basis.getKey());
        identifiers.put("bankKey", basis.getBankKey());

        diffs.put("identifier", identifiers);

        // Fields we checked for changes
        diffs.put("score", getDiffs(original.getScore(), rescore.getScore()));
        diffs.put("scoreStatus", getDiffs(original.getScoreStatus(), rescore.getScoreStatus()));

        return diffs;
    }

    private static boolean match(Item a, Item b) {
        return equivalent(a.getPosition(), b.getPosition()) &&
                equivalent(a.getKey(), b.getKey()) &&
                equivalent(a.getBankKey(), b.getBankKey());
    }

    private static boolean changed(Item a, Item b) {
        return !equivalent(createDouble(a.getScore()), createDouble(b.getScore())) ||
                        !equivalent(a.getScoreStatus(), b.getScoreStatus());
    }

    private static boolean equivalent(String a, String b) {
        a = StringUtils.trimToEmpty(a);
        b = StringUtils.trimToEmpty(b);

        return a.equalsIgnoreCase(b);
    }

    private Map<String,Object> getDiffs(String a, String b) {
        if (equivalent(a, b)) {
            return Collections.emptyMap();
        } else {
            return getDiffMap(a, b);
        }
    }

    private static boolean equivalent(Number a, Number b) {
        final double tolerance = 1e-6;

        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        return Math.abs(a.doubleValue() - b.doubleValue()) < tolerance;
    }

    private Map<String,Object> getDiffs(Number a, Number b) {
        if (equivalent(a, b)) {
            return Collections.emptyMap();
        } else {
            return getDiffMap(a, b);
        }
    }

    private Map<String, Object> getDiffMap(Object from, Object to) {
        if (from == null && to == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> diffs = new LinkedHashMap<>();
        diffs.put("from", format(from));
        diffs.put("to", format(to));

        return diffs;
    }

    // Used by diff map, specifically to handle the case a whole structure missing.
    private Object format(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return obj;
        }
        if (obj instanceof Number) {
            return obj;
        }
        if (obj instanceof Map) {
            return obj;
        }

        return "exists";
    }

    // Put differences into the map under the given key if any differences exist
    private static void addDifferences(String key, Map differences, Map<String, Object> map) {
        if (differences != null && !differences.isEmpty()) {
            map.put(key, differences);
        }
    }

    private static void addDifferences(String key, List differences, Map<String, Object> map) {
        if (differences != null && !differences.isEmpty()) {
            map.put(key, differences);
        }
    }

    private static Double createDouble(String s) {
        return org.apache.commons.lang3.math.NumberUtils.createDouble(trimToNull(s));
    }
}

