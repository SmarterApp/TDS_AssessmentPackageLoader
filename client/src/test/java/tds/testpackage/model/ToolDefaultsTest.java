package tds.testpackage.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ToolDefaultsTest {
    @Test
    public void defaultPrintSizeToolShouldBeRequired() {
        Tool printSizeTool = Tool.builder().
            setName("Print Size").build();
        assertThat(printSizeTool.required()).isTrue();
    }

    @Test
    public void defaultPrintSizeToolRequiredCanHaveOverride() {
        Tool printSizeTool = Tool.builder().
            setName("Print Size").
            setRequired(false).build();
        assertThat(printSizeTool.required()).isFalse();
    }

    @Test
    public void wordListShouldHaveDefaultStudentPackageFieldName() {
        Tool wordListTool = Tool.builder().
            setName("Word List").build();
        assertThat(wordListTool.studentPackageFieldName()).isEqualTo("TDSAcc-WordList");
    }

    @Test
    public void toolShouldCallFieldsWithoutError() {
        Tool tool = Tool.builder().
            setName("Print Size").build();
        assertThat(tool.required()).isTrue();
        assertThat(tool.functional()).isTrue();
        assertThat(tool.options().size()).isEqualTo(0);
        assertThat(tool.allowChange()).isTrue();
        assertThat(tool.allowMultipleOptions()).isFalse();
        assertThat(tool.dependsOnToolType()).isEqualTo(Optional.empty());
        assertThat(tool.disableOnGuest()).isFalse();
        assertThat(tool.selectable()).isTrue();
        assertThat(tool.studentControl()).isFalse();
        assertThat(tool.visible()).isTrue();
    }
}
