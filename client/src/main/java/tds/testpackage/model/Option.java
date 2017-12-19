package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_Option.Builder.class)
public abstract class Option {
    public abstract String getCode();
    public abstract int getSortOrder();
    @Nullable
    public abstract List<Dependency> getDependencies();

    public static Builder builder() {
        return new AutoValue_Option.Builder();
    }


    @AutoValue.Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public abstract static class Builder {
        public abstract Builder setCode(String newCode);

        public abstract Builder setSortOrder(int newSortOrder);

        @JacksonXmlProperty(localName = "Dependencies")
        public abstract Builder setDependencies(List<Dependency> newDependencies);

        public abstract Option build();
    }
}
