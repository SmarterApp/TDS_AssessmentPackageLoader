package tds.testpackage.diff;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import tds.teacherhandscoring.model.TeacherHandScoring;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Optional;

import static tds.testpackage.model.XmlUtil.parseBoolean;

@AutoValue
@JsonDeserialize(builder = AutoValue_Item.Builder.class)
public abstract class Item {
    @XmlAttribute
    public abstract String getId();

    protected abstract Optional<String> getDoNotScore();
    @XmlAttribute
    public boolean doNotScore() {
        return parseBoolean(getDoNotScore(), false);
    }

    @XmlElement(name = "TeacherHandScoring")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public abstract Optional<TeacherHandScoring> getTeacherHandScoring();

    public static Builder builder() {
        return new AutoValue_Item.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        @JacksonXmlProperty(localName = "doNotScore")
        public abstract Builder setDoNotScore(Optional<String> newDoNotScore);

        @JacksonXmlProperty(localName = "TeacherHandScoring")
        public abstract Builder setTeacherHandScoring(Optional<TeacherHandScoring> newTeacherHandScoring);

        public abstract Item build();
    }
}
