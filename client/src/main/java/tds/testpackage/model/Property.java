package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import javax.xml.bind.annotation.XmlAttribute;

@AutoValue
@JsonDeserialize(builder = AutoValue_Property.Builder.class)
public abstract class Property {
    @XmlAttribute
    public abstract String getName();
    
    @XmlAttribute
    public abstract String getValue();

    public static Builder builder() {
        return new AutoValue_Property.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setName(String newName);

        public abstract Builder setValue(String newValue);

        public abstract Property build();
    }
}
