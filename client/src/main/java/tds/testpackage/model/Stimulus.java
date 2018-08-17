package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.springframework.data.annotation.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

@AutoValue
@JsonDeserialize(builder = AutoValue_Stimulus.Builder.class)
public abstract class Stimulus {
    @XmlAttribute
    public abstract String getId();

    @Transient
    @XmlTransient
    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @JsonIgnore
    @XmlTransient
    public String getKey() {
        return String.format("%s-%s", getTestPackage().getBankKey(), getId());
    }

    public static Builder builder() {
        return new AutoValue_Stimulus.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setId(String newId);

        public abstract Stimulus build();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Stimulus stimulus = (Stimulus) o;
        return Objects.equals(stimulus.getId(), getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
