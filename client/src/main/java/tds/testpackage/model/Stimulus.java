package tds.testpackage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_Stimulus.Builder.class)
public abstract class Stimulus {
    public abstract String getId();

    private ItemGroup itemGroup;

    public void setItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
    }

    @JsonIgnore
    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    @JsonIgnore
    public String getKey() {
        return String.format("%s-%s", getItemGroup().getSegment().getAssessment().getTestPackage().getBankKey(), getId());
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
