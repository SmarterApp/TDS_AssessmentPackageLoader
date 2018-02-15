package tds.testpackage.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
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
}
