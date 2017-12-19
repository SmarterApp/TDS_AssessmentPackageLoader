package tds.testpackage.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
//@JsonDeserialize(builder = AutoValue_Pool.Builder.class)
public abstract class Pool {
    public abstract List<ItemGroup> getItemGroups();
}
