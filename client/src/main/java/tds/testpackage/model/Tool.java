package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
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
    public final static Map<String, Tool> TOOL_DEFAULTS_MAP = new ImmutableMap.Builder<String, Tool>()
            .put("American Sign Language",
                    Tool.builder().setName("American Sign Language")
                            .setStudentPackageFieldName("TDS_Acc-ASL")
                            .build())
            .put("Audio Playback Controls",
                    Tool.builder().setName("Audio Playback Controls")
                            .setStudentPackageFieldName("TDSAcc-AudioPlaybackControls")
                            .setVisible(false)
                            .build())
            .put("Braille Transcript",
                    Tool.builder().setName("Braille Transcript")
                            .setStudentPackageFieldName("TDSAcc-BrailleTranscript")
                            .setVisible(false)
                            .build())
            .put("Braille Type",
                    Tool.builder().setName("Braille Type")
                            .setStudentPackageFieldName("TDSAcc-BrailleType")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Calculator",
                    Tool.builder().setName("Calculator")
                            .setStudentPackageFieldName("TDSAcc-Calculator")
                            .build())
            .put("Closed Captioning",
                    Tool.builder().setName("Closed Captioning")
                            .setStudentPackageFieldName("TDSACC-NFCLOSEDCAP")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Color Choices",
                    Tool.builder().setName("Color Choices")
                            .setStudentPackageFieldName("TDSAcc-ColorChoices")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Dictionary",
                    Tool.builder().setName("Dictionary")
                            .setStudentPackageFieldName("TDSAcc-Dictionary")
                            .build())
            .put("Emboss",
                    Tool.builder().setName("Emboss")
                            .setStudentPackageFieldName("TDSAcc-Emboss")
                            .setDependsOnToolType("Braille Type")
                            .build())
            .put("Emboss Request Type",
                    Tool.builder().setName("Emboss Request Type")
                            .setStudentPackageFieldName("TDSAcc-EmbossRequestType")
                            .setDependsOnToolType("Braille Type")
                            .build())
            .put("Expandable Passages",
                    Tool.builder().setName("Expandable Passages")
                            .setStudentPackageFieldName("TDSAcc-ExpandablePassages")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Font Type",
                    Tool.builder().setName("Font Type")
                            .setStudentPackageFieldName("TDSAcc-FontType")
                            .setVisible(false)
                            .build())
            .put("Global Notes",
                    Tool.builder().setName("Global Notes")
                            .setStudentPackageFieldName("TDSAcc-GlobalNotes")
                            .build())
            .put("Hardware Checks",
                    Tool.builder().setName("Hardware Checks")
                            .setStudentPackageFieldName("TDSAcc-HWCheck")
                            .setVisible(false)
                            .build())
            .put("Highlight",
                    Tool.builder().setName("Highlight")
                            .setStudentPackageFieldName("TDSAcc-Highlight")
                            .build())
            .put("Item Font Size",
                    Tool.builder().setName("Item Font Size")
                            .setStudentPackageFieldName("TDSAcc-ItemFontSize")
                            .setVisible(false)
                            .build())
            .put("Item Tools Menu",
                    Tool.builder().setName("Item Tools Menu")
                            .setStudentPackageFieldName("TDSAcc-ITM")
                            .setVisible(false)
                            .build())
            .put("Language",
                    Tool.builder().setName("Language")
                            .setStudentPackageFieldName("TDSAcc-Language")
                            .setAllowChange(false)
                            .setRequired(true)
                            .build())
            .put("Mark for Review",
                    Tool.builder().setName("Mark for Review")
                            .setStudentPackageFieldName("TDSAcc-MarkForReview")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Masking",
                    Tool.builder().setName("Masking")
                            .setStudentPackageFieldName("TDSAcc-Masking")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Mute System Volume",
                    Tool.builder().setName("Mute System Volume")
                            .setStudentPackageFieldName("TDSAcc-Mute")
                            .setVisible(false)
                            .setDependsOnToolType("Language")
                            .build())
            .put("Non-Embedded Accommodations",
                    Tool.builder().setName("Non-Embedded Accommodations")
                            .setStudentPackageFieldName("TDSAcc-NonEmbedAcc")
                            .setFunctional(false)
                            .setAllowMultipleOptions(true)
                            .build())
            .put("Non-Embedded Designated Supports",
                    Tool.builder().setName("Non-Embedded Designated Supports")
                            .setStudentPackageFieldName("TDSAcc-DesigSup")
                            .setFunctional(false)
                            .setAllowMultipleOptions(true)
                            .build())
            .put("Passage Font Size",
                    Tool.builder().setName("Passage Font Size")
                            .setStudentPackageFieldName("TDSAcc-FontSize")
                            .setVisible(false)
                            .build())
            .put("Print on Request",
                    Tool.builder().setName("Print on Request")
                            .setStudentPackageFieldName("TDSAcc-PrintOnRequest")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Print Size",
                    Tool.builder().setName("Print Size")
                            .setStudentPackageFieldName("TDSAcc-PrintSize")
                            .setRequired(true)
                            .build())
            .put("Review Screen Layout",
                    Tool.builder().setName("Review Screen Layout")
                            .setStudentPackageFieldName("TDSAcc-RvScrn")
                            .setVisible(false)
                            .build())
            .put("Streamlined Mode",
                    Tool.builder().setName("Streamlined Mode")
                            .setStudentPackageFieldName("TDSAcc-EAM")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Strikethrough",
                    Tool.builder().setName("Strikethrough")
                            .setStudentPackageFieldName("TDSAcc-Strikethrough")
                            .setDependsOnToolType("Language")
                            .build())
            .put("Student Comments",
                    Tool.builder().setName("Student Comments")
                            .setStudentPackageFieldName("TDSAcc-StudentComments")
                            .build())
            .put("System Volume Control",
                    Tool.builder().setName("System Volume Control")
                            .setStudentPackageFieldName("TDSAcc-SVC")
                            .setVisible(false)
                            .build())
            .put("Test Progress Indicator",
                    Tool.builder().setName("Test Progress Indicator")
                            .setStudentPackageFieldName("TDSAcc-TPI")
                            .setVisible(false)
                            .build())
            .put("Test Shell",
                    Tool.builder().setName("Test Shell")
                            .setStudentPackageFieldName("TDSAcc-TestShell")
                            .setVisible(false)
                            .build())
            .put("Thesaurus",
                    Tool.builder().setName("Thesaurus")
                            .setStudentPackageFieldName("TDSAcc-Thesaurus")
                            .build())
            .put("TTS",
                    Tool.builder().setName("TTS")
                            .setStudentPackageFieldName("TDSAcc-TTS")
                            .setDependsOnToolType("Language")
                            .build())
            .put("TTS Audio Adjustments",
                    Tool.builder().setName("TTS Audio Adjustments")
                            .setStudentPackageFieldName("TDSAcc-TTSAdjust")
                            .setVisible(false)
                            .setDependsOnToolType("Language")
                            .build())
            .put("TTS Pausing",
                    Tool.builder().setName("TTS Pausing")
                            .setStudentPackageFieldName("TDSAcc-TTSPausing")
                            .setVisible(false)
                            .setDependsOnToolType("Language")
                            .build())
            .put("TTX Business Rules",
                    Tool.builder().setName("TTX Business Rules")
                            .setStudentPackageFieldName("TDSAcc-TTXBusinessRules")
                            .setVisible(false)
                            .setDependsOnToolType("Language")
                            .build())
            .put("Tutorial",
                    Tool.builder().setName("Tutorial")
                            .setStudentPackageFieldName("TDSAcc-Tutorial")
                            .setVisible(false)
                            .setDependsOnToolType("Language")
                            .build())
            .put("Word List",
                    Tool.builder().setName("Word List")
                            .setStudentPackageFieldName("TDSAcc-WordList")
                            .setDependsOnToolType("Language")
                            .build())
            .build();

    @XmlAttribute
    public abstract String getName();
    @XmlAttribute
    public abstract Optional<String> getType();
    @XmlAttribute
    public abstract Optional<String> getStudentPackageFieldName();
    @XmlAttribute
    public abstract Optional<String> getAllowChange();
    @XmlAttribute
    public abstract Optional<String> getRequired();
    @XmlAttribute
    public abstract Optional<String> getSelectable();
    @XmlAttribute
    public abstract Optional<String> getVisible();
    @XmlAttribute
    public abstract Optional<String> getStudentControl();
    @XmlAttribute(name = "allowMultiple")
    public abstract Optional<String> getAllowMultipleOptions();
    @XmlAttribute
    public abstract Optional<Integer> getSortOrder();
    @XmlAttribute
    public abstract Optional<String> getDisableOnGuest();

    protected abstract List<Option> getOptions();

    @JsonIgnore
    protected abstract Optional<String> getDependsOnToolType();

    @JsonIgnore
    protected abstract Optional<Boolean> getFunctional();


    protected Optional<Tool> getDefaultTool() {
        return Optional.ofNullable(TOOL_DEFAULTS_MAP.get(getName()));
    }

    public String studentPackageFieldName() {
        return getStudentPackageFieldName().
            orElse(getDefaultTool().flatMap(Tool::getStudentPackageFieldName).
                orElseThrow(() -> new IllegalStateException()));
    }

    public boolean allowChange() {
        return parseBoolean(getAllowChange(), getDefaultTool().flatMap(Tool::getAllowChange), true);
    }

    public boolean required() {
        return parseBoolean(getRequired(), getDefaultTool().flatMap(Tool::getRequired), false);
    }

    public boolean selectable() {
        return parseBoolean(getSelectable(), getDefaultTool().flatMap(Tool::getSelectable), true);
    }

    public boolean visible() {
        return parseBoolean(getVisible(), getDefaultTool().flatMap(Tool::getVisible), true);
    }


    public boolean studentControl() {
        return parseBoolean(getStudentControl(), getDefaultTool().flatMap(Tool::getStudentControl), false);
    }

    public Optional<String> dependsOnToolType() {
        return getDefaultTool().flatMap(Tool::getDependsOnToolType);
    }


    public boolean functional() {
        return getDefaultTool().flatMap(Tool::getFunctional).orElse(true);
    }

    public boolean disableOnGuest() {
        return parseBoolean(getDisableOnGuest(), getDefaultTool().flatMap(Tool::getDisableOnGuest), false);
    }

    public boolean allowMultipleOptions() {
        return parseBoolean(getAllowMultipleOptions(), getDefaultTool().flatMap(Tool::getAllowMultipleOptions), false);
    }
    @JsonProperty(value = "options")
    @XmlElementWrapper(name="Options")
    @XmlElement(name="Option", type=Option.class)
    public List<Option> options() {
        return Optional.ofNullable(getOptions()).orElse(new ArrayList<>());
    }

    public static Builder builder() {
        return new AutoValue_Tool.Builder().setOptions(new ArrayList<>());
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setStudentPackageFieldName(Optional<String> newStudentPackageFieldName);

        @JacksonXmlProperty(localName = "studentPackageFieldName")
        public Builder setStudentPackageFieldName(String newStudentPackageFieldName) {
            return setStudentPackageFieldName(Optional.ofNullable(newStudentPackageFieldName));
        }

        public abstract Builder setType(Optional<String> newType);

        @JacksonXmlProperty(localName = "type")
        public Builder setType(String newType) {
            return setType(Optional.ofNullable(newType));
        }

        protected abstract Builder setSelectable(Optional<String> newSelectable);

        @JacksonXmlProperty(localName = "selectable")
        public Builder setSelectable(boolean newSelectable) {
            return setSelectable(Optional.of(String.valueOf(newSelectable)));
        }

        protected abstract Builder setVisible(Optional<String> newVisible);

        @JacksonXmlProperty(localName = "visible")
        public Builder setVisible(boolean newVisible) {
            return setVisible(Optional.of(String.valueOf(newVisible)));
        }

        protected abstract Builder setStudentControl(Optional<String> newStudentControl);

        @JacksonXmlProperty(localName = "studentControl")
        public Builder setStudentControl(boolean newStudentControl) {
            return setStudentControl(Optional.of(String.valueOf(newStudentControl)));
        }

        protected abstract Builder setAllowChange(Optional<String> newAllowChange);


        @JacksonXmlProperty(localName = "allowChange")
        public Builder setAllowChange(boolean newAllowChange) {
            return setAllowChange(Optional.of(String.valueOf(newAllowChange)));
        }

        protected abstract Builder setRequired(Optional<String> newRequired);

        @JacksonXmlProperty(localName = "required")
        public Builder setRequired(boolean newRequired) {
            return setRequired(Optional.of(String.valueOf(newRequired)));
        }

        protected abstract Builder setDisableOnGuest(Optional<String> newDisableOnGuest);

        @JacksonXmlProperty(localName = "disableOnGuest")
        public Builder setDisableOnGuest(boolean newDisableOnGuest) {
            return setDisableOnGuest(Optional.of(String.valueOf(newDisableOnGuest)));
        }

        protected abstract Builder setSortOrder(Optional<Integer> newSortOrder);

        @JacksonXmlProperty(localName = "sortOrder")
        public Builder setSortOrder(int newSortOrder) {
            return setSortOrder(Optional.of(newSortOrder));
        }

        protected abstract Builder setAllowMultipleOptions(Optional<String> newAllowMultipleOptions);

        @JacksonXmlProperty(localName = "allowMultiple")
        public Builder setAllowMultipleOptions(boolean newAllowMultipleOptions) {
            return setAllowMultipleOptions(Optional.of(String.valueOf(newAllowMultipleOptions)));
        }

        protected abstract Builder setDependsOnToolType(Optional<String> newDependsOnToolType);

        @JacksonXmlProperty(localName = "dependsOnToolType")
        public Builder setDependsOnToolType(String newDependsOnToolType) {
            return setDependsOnToolType(Optional.ofNullable(newDependsOnToolType));
        }

        protected abstract Builder setFunctional(Optional<Boolean> newFunctional);

        @JacksonXmlProperty(localName = "functional")
        public Builder setFunctional(boolean newFunctional) {
            return setFunctional(Optional.of(newFunctional));
        }

        @JacksonXmlProperty(localName = "Options")
        public abstract Builder setOptions(List<Option> newOptions);

        public abstract Tool build();
    }
}
