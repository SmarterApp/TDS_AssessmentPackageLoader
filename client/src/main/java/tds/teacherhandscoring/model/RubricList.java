package tds.teacherhandscoring.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

@AutoValue
@JsonDeserialize(using = RubricListDeserializer.class, builder = AutoValue_RubricList.Builder.class)
public abstract class RubricList {

    public abstract List<Object> rubricOrSamplelist();

    public static Builder builder() {
        return new AutoValue_RubricList.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {

        @JacksonXmlElementWrapper(useWrapping = false)
        public abstract Builder rubricOrSamplelist(List<Object> rubricOrSamplelist);

        public abstract RubricList build();
    }
}
