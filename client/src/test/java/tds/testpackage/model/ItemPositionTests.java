package tds.testpackage.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ItemPositionTests {
    ItemScoreDimension itemScoreDimension = ItemScoreDimension.builder()
        .setScorePoints(1)
        .setMeasurementModel("IRT3PLn")
        .setWeight(1)
        .setItemScoreParameters(new ArrayList<>())
        .build();

    Item createItem(final String id) {
        return Item.builder()
            .setPresentations(new ArrayList<>())
            .setBlueprintReferences(new ArrayList<>())
            .setId(id)
            .setType("type")
            .setItemScoreDimension(itemScoreDimension)
            .build();
    }

    ItemGroup createItemGroup(final List<Item> items) {
        ItemGroup itemGroup = ItemGroup.builder()
            .setId("id")
            .setItems(items)
            .build();
        items.forEach(item -> item.setItemGroup(itemGroup));
        return itemGroup;
    }

    ItemGroup createItemGroup(final int count) {
        List<Item> items = IntStream.range(0, count).
            mapToObj(i -> createItem(String.format("%s", i))).
            collect(Collectors.toList());
        return createItemGroup(items);
    }

    @Test
    public void singleItemShouldHavePositionOne() {
        Item item = createItem("single");

        createItemGroup(Arrays.asList(item));

        assertThat(item.position()).isEqualTo(1);
    }

    @Test
    public void secondItemShouldHavePositionTwo() {
        Item item1 = createItem("one");
        Item item2 = createItem("two");

        createItemGroup(Arrays.asList(item1, item2));

        assertThat(item2.position()).isEqualTo(2);
    }

    @Test
    public void twoItemGroupsWithSecondItemShouldHavePositionTwo() {
        ItemGroup itemGroup1 = createItemGroup(5);

        Item item1 = createItem("one");
        Item item2 = createItem("two");

        ItemGroup itemGroup2 = createItemGroup(Arrays.asList(item1, item2));

        Segment segment = Segment.builder()
            .setId("SBAC-IRP-Perf-MATH-11")
            .setPosition(1)
            .setAlgorithmType("fixedform")
            .setAlgorithmImplementation("FAIRWAY ROUNDROBIN")
            .setPool(Arrays.asList(itemGroup1, itemGroup2))
            .build();


        assertThat(item2.position()).isEqualTo(2);
    }

    @Test
    public void segmentFormWithTwoItemGroupsWithSecondItemShouldHavePositionFour() {
        ItemGroup itemGroup1 =  createItemGroup(2);

        Item item1 = createItem("one");
        Item item2 = createItem("two");
        List<Item> items2 = Arrays.asList(item1, item2);

        ItemGroup itemGroup2 = createItemGroup(items2);

        Presentation englishPresentation = Presentation.builder()
            .setCode("ENU")
            .setLabel("English")
            .build();

        SegmentForm segmentForm = SegmentForm.builder()
            .setId("id")
            .setCohort("Cohort")
            .setPresentations(Arrays.asList(englishPresentation))
            .setItemGroups(Arrays.asList(itemGroup1, itemGroup2))
            .build();

        itemGroup1.items().forEach(item -> item.setSegmentForm(segmentForm));
        items2.forEach(item -> item.setSegmentForm(segmentForm));

        assertThat(item2.position()).isEqualTo(4);
    }
}
