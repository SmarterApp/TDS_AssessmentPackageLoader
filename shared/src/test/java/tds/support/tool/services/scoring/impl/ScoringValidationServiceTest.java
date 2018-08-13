package tds.support.tool.services.scoring.impl;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import tds.support.job.ScoringValidationReport;
import tds.support.tool.services.scoring.ScoringValidationService;
import tds.trt.model.TDSReport;
import tds.trt.model.TDSReport.Opportunity;
import tds.trt.model.TDSReport.Opportunity.Item;
import tds.trt.model.TDSReport.Opportunity.Score;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Greg Charles on 7/18/18.
 */
public class ScoringValidationServiceTest {

    private ScoringValidationService scoringValidationService;

    @Before
    public void setUp() {
        scoringValidationService = new ScoringValidationServiceImpl();
    }

    @Test
    public void testEmptyReports() {
        TDSReport left = new TDSReport();
        TDSReport right = new TDSReport();

        ScoringValidationReport report = scoringValidationService.validateScoring(null, left, right);

        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void testTestsPresentButEqual() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("Name ABC");

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertThat(report.getJobId(), is("jobId"));
        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void testTestsPresentButEquivalent() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc  ");
        left.getTest().setName(" Name ABC");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("  ABC");
        right.getTest().setName("name ABC  ");

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertThat(report.getJobId(), is("jobId"));
        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void testTestsPresentAndDifferent() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("xyz");
        right.getTest().setName("name XYZ");

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        Map<String, Object> testDiffs = (Map<String, Object>) report.getDifferenceReport().get("test");
        assertFalse(testDiffs.isEmpty());
        Map<String, Object> testIdDiffs = (Map<String, Object>) testDiffs.get("testId");
        assertFalse(testIdDiffs.isEmpty());
        assertThat(testIdDiffs.get("from"), is(left.getTest().getTestId()));
        assertThat(testIdDiffs.get("to"), is(right.getTest().getTestId()));
    }

    @Test
    public void testMatchingOpportunities() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("Name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void testMismatchingOpportunities() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("Name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("789");

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::key::from"),
                is(left.getOpportunity().getKey()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::key::to"),
                is(right.getOpportunity().getKey()));
    }

    @Test
    public void testMatchingItems() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Item item = new Item();
        item.setPosition(1);
        item.setKey(123);
        item.setBankKey(456);
        item.setScore("3.25");
        item.setScoreStatus("SCORED");
        left.getOpportunity().getItem().add(item);

        item = new Item();
        item.setPosition(1);
        item.setKey(123);
        item.setBankKey(456);
        item.setScore("3.25");
        item.setScoreStatus("SCORED");
        right.getOpportunity().getItem().add(item);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void testChangedItems() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Item item1 = new Item();
        item1.setPosition(1);
        item1.setKey(123);
        item1.setBankKey(456);
        item1.setScore(null);
        item1.setScoreStatus("NOT SCORED");
        left.getOpportunity().getItem().add(item1);

        Item item2 = new Item();
        item2.setPosition(1);
        item2.setKey(123);
        item2.setBankKey(456);
        item2.setScore("3.25");
        item2.setScoreStatus("SCORED");
        right.getOpportunity().getItem().add(item2);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::status"),
                is("modified"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::from"),
                is(item1.getScore()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::to"),
                is(item2.getScore()));

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::from"),
                is(item1.getScoreStatus()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::to"),
                is(item2.getScoreStatus()));
    }

    @Test
    public void testAddedItem() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");


        Item itemAdded = new Item();
        itemAdded.setPosition(1);
        itemAdded.setKey(123);
        itemAdded.setBankKey(456);
        itemAdded.setScore("3.25");
        itemAdded.setScoreStatus("SCORED");
        right.getOpportunity().getItem().add(itemAdded);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::status"),
                is("added"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::from"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::to"),
                is(itemAdded.getScore()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::from"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::to"),
                is(itemAdded.getScoreStatus()));
    }

    @Test
    public void testRemovedItem() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Item itemRemoved = new Item();
        itemRemoved.setPosition(1);
        itemRemoved.setKey(123);
        itemRemoved.setBankKey(456);
        itemRemoved.setScore("3.25");
        itemRemoved.setScoreStatus("SCORED");
        left.getOpportunity().getItem().add(itemRemoved);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::status"),
                is("removed"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::from"),
                is(itemRemoved.getScore()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::score::to"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::from"),
                is(itemRemoved.getScoreStatus()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::items::1::scoreStatus::to"),
                is(nullValue()));
    }

    @Test
    public void testMatchingScores() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Score original = new Score();
        original.setMeasureOf("Of ABC");
        original.setMeasureLabel("ABC");
        original.setValue("2.5");
        original.setStandardError("0.55");
        left.getOpportunity().getScore().add(original);

        Score rescore  = new Score();
        rescore.setMeasureOf("Of ABC");
        rescore.setMeasureLabel("ABC");
        rescore.setValue("2.5");
        rescore.setStandardError("0.55");
        right.getOpportunity().getScore().add(rescore);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertTrue(report.getDifferenceReport().isEmpty());
    }

    @Test
    public void tesChangedScores() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Score original = new Score();
        original.setMeasureOf("Of ABC");
        original.setMeasureLabel("ABC");
        original.setValue("4.0");
        original.setStandardError("");
        left.getOpportunity().getScore().add(original);

        Score rescore  = new Score();
        rescore.setMeasureOf("Of ABC");
        rescore.setMeasureLabel("ABC");
        rescore.setValue("2.5");
        rescore.setStandardError("0.55");
        right.getOpportunity().getScore().add(rescore);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::status"),
                is("modified"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::from"),
                is(original.getValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::to"),
                is(rescore.getValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::from"),
                is(original.getStandardError()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::to"),
                is(rescore.getStandardError()));
    }

    @Test
    public void testAddedScore() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Score rescore  = new Score();
        rescore.setMeasureOf("Of ABC");
        rescore.setMeasureLabel("ABC");
        rescore.setValue("2.5");
        rescore.setStandardError("0.55");
        right.getOpportunity().getScore().add(rescore);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::status"),
                is("added"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::from"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::to"),
                is(rescore.getValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::from"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::to"),
                is(rescore.getStandardError()));
    }

    @Test
    public void testRemovedScore() {
        TDSReport left = new TDSReport();
        left.setTest(new TDSReport.Test());
        left.getTest().setTestId("abc");
        left.getTest().setName("Name ABC");
        left.setOpportunity(new Opportunity());
        left.getOpportunity().setKey("123");

        TDSReport right = new TDSReport();
        right.setTest(new TDSReport.Test());
        right.getTest().setTestId("abc");
        right.getTest().setName("name ABC");
        right.setOpportunity(new Opportunity());
        right.getOpportunity().setKey("123");

        Score original  = new Score();
        original.setMeasureOf("Of ABC");
        original.setMeasureLabel("ABC");
        original.setValue("2.5");
        original.setStandardError("0.55");
        left.getOpportunity().getScore().add(original);

        ScoringValidationReport report = scoringValidationService.validateScoring("jobId", left, right);

        assertFalse(report.getDifferenceReport().isEmpty());

        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::status"),
                is("removed"));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::from"),
                is(original.getValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::value::to"),
                is(nullValue()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::from"),
                is(original.getStandardError()));
        assertThat(getFromMap(report.getDifferenceReport(), "opportunity::scores::ABC::standardError::to"),
                is(nullValue()));
    }

    private static Object getFromMap(Map<String, Object> map, String path) {
        Deque<String> pathDeque = new ArrayDeque<>(Arrays.asList(path.split("::")));
        return _getFromMap(map, pathDeque);
    }

    private static Object _getFromMap(Map<String, ?> map, Deque<String> pathDeque) {
        if (pathDeque.isEmpty()) {
            return map;
        }
        if (map == null) {
            throw new NoSuchElementException("Cannot find element in map");
        }

        String key = pathDeque.pop();
        Object child = map.get(key);

        // Special handling for items and scores, which will be lists instead of maps
        if (key.equalsIgnoreCase("items") && child instanceof List) {
            return handleItems(pathDeque, (List<Map<String, Map<String, Object>>>) child);
        }
        if (key.equalsIgnoreCase("scores") && child instanceof List) {
            return handleScores(pathDeque, (List<Map<String, Map<String, Object>>>) child);
        }

        if (pathDeque.isEmpty()) {
            return map.get(key);
        }

        return _getFromMap((Map<String,Object>)map.get(key), pathDeque);
    }

    // Handles finding one particular item in a list. This assumes the "position" field to unique in the test data.
    @Nullable
    private static Object handleItems(Deque<String> pathDeque, List<Map<String, Map<String, Object>>> items) {
        String itemKey = pathDeque.pop();

        return items
                .stream()
                .filter(item -> String.valueOf(item.get("identifier").get("position")).equals(itemKey))
                .findAny()
                .map(item -> _getFromMap(item, pathDeque)).orElse(null);
    }

    // Handles finding one particular score in a list. This assumes the "measureLabel" field to unique in the test data.
    @Nullable
    private static Object handleScores(Deque<String> pathDeque, List<Map<String, Map<String, Object>>> scores) {
        String scoreKey = pathDeque.pop();

        return scores
                .stream()
                .filter(score -> String.valueOf(score.get("identifier").get("measureLabel")).equals(scoreKey))
                .findAny()
                .map(score -> _getFromMap(score, pathDeque)).orElse(null);
    }
}