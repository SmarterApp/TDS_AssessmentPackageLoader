package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

import javax.xml.bind.annotation.XmlAttribute;

@AutoValue
@JsonDeserialize(builder = AutoValue_PoolProperty.Builder.class)
public abstract class PoolProperty {
    @XmlAttribute
    public abstract String getName();
    @XmlAttribute
    public abstract String getValue();


    public static Builder builder() {
        return new AutoValue_PoolProperty.Builder();
    }

    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        @JacksonXmlProperty(localName = "name")
        public abstract Builder setName(String name);

        @JacksonXmlProperty(localName = "value")
        public abstract Builder setValue(String value);

        public abstract PoolProperty build();
    }
}
