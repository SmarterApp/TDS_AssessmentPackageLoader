package tds.support.tool.security.model;

import com.google.common.collect.ImmutableSet;
import tds.support.tool.security.permission.client.Permission;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * A permission scope defines the level associated with a granted permission. This level is
 * either statewide, applying to all organizations for the system state (e.g. "CA") or it is a
 * collection of district, school, group ids.
 * <p>
 * The permission scope is used to limit database query results. All of the ids are database ids.
 */
public class PermissionScope implements Serializable {

    /**
     * Statewide permission scope flyweight
     * Since all statewide permission scopes will be identical, we can use the same immutable object instance
     */
    public static final PermissionScope STATEWIDE = new PermissionScope(true);

    /**
     * Empty permission scope flyweight
     */
    public static final PermissionScope EMPTY = new PermissionScope(false);

    private final Set<Long> districtGroupIds;
    private final Set<Long> districtIds;
    private final Set<Long> institutionGroupIds;
    private final Set<Long> institutionIds;
    private final boolean statewide;

    /**
     * Default constructor for serialization
     */
    private PermissionScope() {
        this(false);
    }

    private PermissionScope(final boolean statewide) {
        this.districtGroupIds = ImmutableSet.of();
        this.districtIds = ImmutableSet.of();
        this.institutionGroupIds = ImmutableSet.of();
        this.institutionIds = ImmutableSet.of();
        this.statewide = statewide;
    }

    private PermissionScope(final boolean statewide,
                            final Collection<Long> districtGroupIds,
                            final Collection<Long> districtIds,
                            final Collection<Long> institutionGroupIds,
                            final Collection<Long> institutionIds) {
        this.districtGroupIds = districtGroupIds != null ? ImmutableSet.copyOf(districtGroupIds) : ImmutableSet.of();
        this.districtIds = districtIds != null ? ImmutableSet.copyOf(districtIds) : ImmutableSet.of();
        this.institutionGroupIds = institutionGroupIds != null ? ImmutableSet.copyOf(institutionGroupIds) : ImmutableSet.of();
        this.institutionIds = institutionIds != null ? ImmutableSet.copyOf(institutionIds) : ImmutableSet.of();
        this.statewide = statewide;
    }

    public boolean isStatewide() {
        return this.statewide;
    }

    public Set<Long> getDistrictGroupIds() {
        return districtGroupIds;
    }

    public Set<Long> getDistrictIds() {
        return districtIds;
    }

    public Set<Long> getInstitutionGroupIds() {
        return institutionGroupIds;
    }

    public Set<Long> getInstitutionIds() {
        return institutionIds;
    }

    public Builder copy() {
        return builder().copy(this);
    }

    public static PermissionScope getScopeOrEmpty(final Map<String, Permission> permissionMap, final String permissionId) {
//        return permissionMap.containsKey(permissionId) ? permissionMap.get(permissionId).getScope() : EMPTY;
        return EMPTY;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private boolean statewide;
        private List<Long> districtGroupIds = newArrayList();
        private List<Long> districtIds = newArrayList();
        private List<Long> schoolGroupIds = newArrayList();
        private List<Long> schoolIds = newArrayList();

        public Builder copy(final PermissionScope scope) {
            this.statewide = scope.statewide;
            this.districtGroupIds = newArrayList(scope.districtGroupIds);
            this.districtIds = newArrayList(scope.districtIds);
            this.schoolGroupIds = newArrayList(scope.institutionGroupIds);
            this.schoolIds = newArrayList(scope.institutionIds);
            return this;
        }

        public Builder statewide(final boolean statewide) {
            this.statewide = statewide;
            return this;
        }

        public Builder addDistrictGroupId(final long id) {
            this.districtGroupIds.add(id);
            return this;
        }

        public Builder addDistrictGroupIds(final Collection<Long> ids) {
            this.districtGroupIds.addAll(ids);
            return this;
        }

        public Builder addDistrictId(final long id) {
            this.districtIds.add(id);
            return this;
        }

        public Builder addDistrictIds(final Collection<Long> ids) {
            this.districtIds.addAll(ids);
            return this;
        }

        public Builder addSchoolGroupId(final long id) {
            this.schoolGroupIds.add(id);
            return this;
        }

        public Builder addSchoolGroupIds(final Collection<Long> ids) {
            this.schoolGroupIds.addAll(ids);
            return this;
        }

        public Builder addSchoolId(final long id) {
            this.schoolIds.add(id);
            return this;
        }

        public Builder addSchoolIds(final Collection<Long> ids) {
            this.schoolIds.addAll(ids);
            return this;
        }

        /**
         * @return <code>true</code> if the permission scope is statewide or has any ids
         */
        public boolean isValid() {
            return statewide
                    || !districtGroupIds.isEmpty()
                    || !districtIds.isEmpty()
                    || !schoolGroupIds.isEmpty()
                    || !schoolIds.isEmpty();
        }

        public PermissionScope build() {
            return new PermissionScope(statewide, districtGroupIds, districtIds, schoolGroupIds, schoolIds);
        }
    }
}
