package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

/**
 * A "Tool" is a general term for any universal tools, designated supports, or accommodations. Each Tool respresents a type of tool, such as "Zoom", "American Sign Language", "Masking", and "Calculator" and contains a list of its possible options and dependency rules. Tools can be defined at the assessment-level as well as the segment-level
 *
 * ATTRIBUTES:
 * name: The tool type "name". This is the string identifying the tool type. The tool type name may contain spaces.
 * studentPackageFieldName: The name/identifier of the tool/accommodation/designated supports used by ART.
 * allowChange: A flag indicating whether or not the tool can be changed by the proctor when a student is waiting for approval to enter an assessment or segment
 * required: A flag indicating whether or not an tool selection is required for this type of tool
 * sortOrder: An optional ordering of the type (ascending)
 * disableOnGuest: A flag indicating whether or not an accommodation should be disabled for guest students
 *
 * SUBELEMENTS:
 * The Options for the Tool
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_Tool.Builder.class)
public abstract class Tool {
    Map<String, Tool> TOOL_DEFAULTS_MAP = new ImmutableMap.Builder<String, Tool>()
            .put("American Sign Language",
                    Tool.builder()
                            .setName("American Sign Language")
                            .setStudentPackageFieldName("TDS_Acc-ASL")
                            .setAllowChange(true)
                            .setRequired(false)
//                            .setSelectable(true)
//                            .setVisible(true)
//                            .setStudentControl(false)
//                            .setDependsOnToolType("Language")  -- THIS WILL BE NULL if the tool type is not present in this map
//                            .setFunctional(true) -- THIS WILL BE TRUE if the tool type is not present in this map
                            .build())
//            .put("Audio Playback Controls",
//                    new TestToolDefaults.Builder("Audio Playback Controls")
//                            .withArtFieldName("TDSAcc-AudioPlaybackControls")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Braille Transcript",
//                    new TestToolDefaults.Builder("Braille Transcript")
//                            .withArtFieldName("TDSAcc-BrailleTranscript")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Braille Type",
//                    new TestToolDefaults.Builder("Braille Type")
//                            .withArtFieldName("TDSAcc-BrailleType")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Calculator",
//                    new TestToolDefaults.Builder("Calculator")
//                            .withArtFieldName("TDSAcc-Calculator")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Closed Captioning",
//                    new TestToolDefaults.Builder("Closed Captioning")
//                            .withArtFieldName("TDSACC-NFCLOSEDCAP")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Color Choices",
//                    new TestToolDefaults.Builder("Color Choices")
//                            .withArtFieldName("TDSAcc-ColorChoices")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Dictionary",
//                    new TestToolDefaults.Builder("Dictionary")
//                            .withArtFieldName("TDSAcc-Dictionary")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Emboss",
//                    new TestToolDefaults.Builder("Emboss")
//                            .withArtFieldName("TDSAcc-Emboss")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Braille Type")
//                            .build())
//            .put("Emboss Request Type",
//                    new TestToolDefaults.Builder("Emboss Request Type")
//                            .withArtFieldName("TDSAcc-EmbossRequestType")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Braille Type")
//                            .build())
//            .put("Expandable Passages",
//                    new TestToolDefaults.Builder("Expandable Passages")
//                            .withArtFieldName("TDSAcc-ExpandablePassages")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Font Type",
//                    new TestToolDefaults.Builder("Font Type")
//                            .withArtFieldName("TDSAcc-FontType")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Global Notes",
//                    new TestToolDefaults.Builder("Global Notes")
//                            .withArtFieldName("TDSAcc-GlobalNotes")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Hardware Checks",
//                    new TestToolDefaults.Builder("Hardware Checks")
//                            .withArtFieldName("TDSAcc-HWCheck")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Highlight",
//                    new TestToolDefaults.Builder("Highlight")
//                            .withArtFieldName("TDSAcc-Highlight")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Item Font Size",
//                    new TestToolDefaults.Builder("Item Font Size")
//                            .withArtFieldName("TDSAcc-ItemFontSize")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Item Tools Menu",
//                    new TestToolDefaults.Builder("Item Tools Menu")
//                            .withArtFieldName("TDSAcc-ITM")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Language",
//                    new TestToolDefaults.Builder("Language")
//                            .withArtFieldName("TDSAcc-Language")
//                            .withAllowChange(false)
//                            .withRequired(true)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Mark for Review",
//                    new TestToolDefaults.Builder("Mark for Review")
//                            .withArtFieldName("TDSAcc-MarkForReview")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Masking",
//                    new TestToolDefaults.Builder("Masking")
//                            .withArtFieldName("TDSAcc-Masking")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Mute System Volume",
//                    new TestToolDefaults.Builder("Mute System Volume")
//                            .withArtFieldName("TDSAcc-Mute")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Non-Embedded Accommodations",
//                    new TestToolDefaults.Builder("Non-Embedded Accommodations")
//                            .withArtFieldName("TDSAcc-NonEmbedAcc")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(false)
//                            .withAllowMultipleOptions(true)
//                            .build())
//            .put("Non-Embedded Designated Supports",
//                    new TestToolDefaults.Builder("Non-Embedded Designated Supports")
//                            .withArtFieldName("TDSAcc-DesigSup")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(false)
//                            .withAllowMultipleOptions(true)
//                            .build())
//            .put("Passage Font Size",
//                    new TestToolDefaults.Builder("Passage Font Size")
//                            .withArtFieldName("TDSAcc-FontSize")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Print on Request",
//                    new TestToolDefaults.Builder("Print on Request")
//                            .withArtFieldName("TDSAcc-PrintOnRequest")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Print Size",
//                    new TestToolDefaults.Builder("Print Size")
//                            .withArtFieldName("TDSAcc-PrintSize")
//                            .withAllowChange(true)
//                            .withRequired(true)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Review Screen Layout",
//                    new TestToolDefaults.Builder("Review Screen Layout")
//                            .withArtFieldName("TDSAcc-RvScrn")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Streamlined Mode",
//                    new TestToolDefaults.Builder("Streamlined Mode")
//                            .withArtFieldName("TDSAcc-EAM")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Strikethrough",
//                    new TestToolDefaults.Builder("Strikethrough")
//                            .withArtFieldName("TDSAcc-Strikethrough")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Student Comments",
//                    new TestToolDefaults.Builder("Student Comments")
//                            .withArtFieldName("TDSAcc-StudentComments")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("System Volume Control",
//                    new TestToolDefaults.Builder("System Volume Control")
//                            .withArtFieldName("TDSAcc-SVC")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Test Progress Indicator",
//                    new TestToolDefaults.Builder("Test Progress Indicator")
//                            .withArtFieldName("TDSAcc-TPI")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Test Shell",
//                    new TestToolDefaults.Builder("Test Shell")
//                            .withArtFieldName("TDSAcc-TestShell")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .build())
//            .put("Thesaurus",
//                    new TestToolDefaults.Builder("Thesaurus")
//                            .withArtFieldName("TDSAcc-Thesaurus")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("TTS",
//                    new TestToolDefaults.Builder("TTS")
//                            .withArtFieldName("TDSAcc-TTS")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("TTS Audio Adjustments",
//                    new TestToolDefaults.Builder("TTS Audio Adjustments")
//                            .withArtFieldName("TDSAcc-TTSAdjust")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("TTS Pausing",
//                    new TestToolDefaults.Builder("TTS Pausing")
//                            .withArtFieldName("TDSAcc-TTSPausing")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("TTX Business Rules",
//                    new TestToolDefaults.Builder("TTX Business Rules")
//                            .withArtFieldName("TDSAcc-TTXBusinessRules")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Tutorial",
//                    new TestToolDefaults.Builder("Tutorial")
//                            .withArtFieldName("TDSAcc-Tutorial")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(false)
//                            .withVisible(false)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
//            .put("Word List",
//                    new TestToolDefaults.Builder("Word List")
//                            .withArtFieldName("TDSAcc-WordList")
//                            .withAllowChange(true)
//                            .withRequired(false)
//                            .withSelectable(true)
//                            .withVisible(true)
//                            .withStudentControl(false)
//                            .withFunctional(true)
//                            .withDependsOnToolType("Language")
//                            .build())
            .build();

    public abstract String getName();
    public abstract String getStudentPackageFieldName();
    public abstract Optional<String> getAllowChange();
    public abstract Optional<String> getRequired();
    public abstract Optional<Integer> getSortOrder();
    public abstract Optional<String> getDisableOnGuest();
    public abstract List<Option> getOptions();

    public boolean required() {
        return parseBoolean(getRequired(), false);
    }

    public boolean allowChange() {
        final Optional<Tool> tool = Optional.ofNullable(TOOL_DEFAULTS_MAP.get(getName()));
        return parseBoolean(getAllowChange(), !tool.isPresent() || parseBoolean(tool.get().getAllowChange()));
    }

    public boolean disableOnGuest() {
        return parseBoolean(getDisableOnGuest(), false);
    }

    public static Builder builder() {
        return new AutoValue_Tool.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setStudentPackageFieldName(String newStudentPackageFieldName);

        protected abstract Builder setAllowChange(Optional<String> newAllowChange);

        public Builder setAllowChange(boolean newAllowChange) {
            return setAllowChange(Optional.of(String.valueOf(newAllowChange)));
        }

        protected abstract Builder setRequired(Optional<String> newRequired);

        public Builder setRequired(boolean newRequired) {
            return setRequired(Optional.of(String.valueOf(newRequired)));
        }

        protected abstract Builder setDisableOnGuest(Optional<String> newDisableOnGuest);

        public Builder setDisableOnGuest(boolean newDisableOnGuest) {
            return setDisableOnGuest(Optional.of(String.valueOf(newDisableOnGuest)));
        }

        protected abstract Builder setSortOrder(Optional<Integer> newSortOrder);

        public Builder setSortOrder(int newSortOrder) {
            return setSortOrder(Optional.of(newSortOrder));
        }

        @JacksonXmlProperty(localName = "Options")
        public abstract Builder setOptions(List<Option> newOptions);

        public abstract Tool build();
    }
}
