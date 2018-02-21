package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.springframework.data.annotation.Transient;

@AutoValue
@JsonDeserialize(builder = AutoValue_Stimulus.Builder.class)
public abstract class Stimulus {
    public abstract String getId();

    @Transient
    private TestPackage testPackage;

    public void setTestPackage(TestPackage testPackage) {
        this.testPackage = testPackage;
    }

    @JsonIgnore
    public TestPackage getTestPackage() {
        return this.testPackage;
    }

    @JsonIgnore
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
}
