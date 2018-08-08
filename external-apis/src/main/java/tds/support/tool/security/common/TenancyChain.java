package tds.support.tool.security.common;

import tds.support.tool.security.model.Grant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static tds.support.tool.security.model.Grant.Level.*;


/**
 * Helper for manipulating user tenancy chain<br>
 * Dug up from python code (python code uses only fields 1,7,8,11,15), format of string in sbac chain:<pre>
 0        1    2     3        4      5              6             7       8     9                  10               11         12       13                    14                  15            16
 |EntityId|Role|Level|ClientID|Client|GroupOfStateID|GroupOfStates|StateID|State|GroupOfDistrictsID|GroupOfDistricts|DistrictID|District|GroupOfInstitutionsID|GroupOfInstitutions|InstitutionID|Institution|</pre>
 * where<ul>
 *     <li>EntityId - id of grant entity, e.g. CA; it should match id of the entity of the level in the chain</li>
 *     <li>Role - name of role, e.g. GENERAL, PII, ASMTDATALOAD</li>
 *     <li>Level - grant level, e.g. CLIENT, STATE, DISTRICT, INSTITUTION</li>
 *     <li>ClientID - client id, e.g. CA</li>
 *     <li>Client - client name, e.g. SBAC, CA98765</li>
 *     <li>GroupOfStateID</li>
 *     <li>GroupOfStates</li>
 *     <li>StateID - state id, e.g. CA</li>
 *     <li>State - state name, e.g. CALIFORNIA</li>
 *     <li>GroupOfDistrictsID</li>
 *     <li>GroupOfDistricts</li>
 *     <li>DistrictID - district id, e.g. DISTRICT2, 2ce72d77-1de2-4137-a083-77935831b817</li>
 *     <li>District - district name, e.g. Dealfish Pademelon SD</li>
 *     <li>GroupOfInstitutionsID</li>
 *     <li>GroupOfInstitutions</li>
 *     <li>InstitutionID - school id, e.g. 942, DS9001</li>
 *     <li>Institution - school name, e.g. Daybreak Central High</li>
 * </ul>
 * Multiple chains can be indicated, comma separated.
 */
public class TenancyChain {
    private List<Grant> grants;

    private TenancyChain(final List<Grant> grants) {
        this.grants = grants;
    }

    /**
     * Parses a full tenancy chain string returning a new instance
     *
     * @param value chain to parse
     * @return newly created tenancy chain
     * @see TenancyChain#toString()
     */
    public static TenancyChain fromString(final String value) {
        final ArrayList<Grant> grants = new ArrayList<>();

        if (value == null || value.isEmpty()) {
            return new TenancyChain(grants);
        }

        for (final String grant : value.split(",")) {
            if (grant.isEmpty()) continue;
            grants.add(Grant.fromString(grant));
        }

        return new TenancyChain(grants);
    }

    /**
     * @return tenancy chain as a string
     * @see TenancyChain#fromString(String)
     */
    public String toString() {
        return String.join(",", grants.stream().map(Grant::toString).collect(Collectors.toList()));
    }

    /**
     * @return copy of list of grants
     */
    public List<Grant> getGrants() {
        return new ArrayList<>(grants);
    }

    /**
     * @return true if this chain has no role grants
     */
    public boolean isEmpty() {
        return grants.isEmpty();
    }

    /**
     * This method returns true if the role is granted at any level.<br>
     * This should go away since it is not specific enough; perhaps replaced by
     * hasRoleForClient or hasRoleForState; but it is currently being used by
     * exam service.
     *
     * @param roleName role name, e.g. "ASMTDATALOAD"; case is ignored
     * @return true if role is granted at any level
     */
    public boolean hasRole(final String roleName) {
        return grants.stream().map(Grant::getRole).anyMatch(roleName::equalsIgnoreCase);
    }

    /**
     * Return true if the role is granted for any of the indicated school, district, or school.
     * The logical use is to provide the hierarchy for the school and this will check whether
     * the role is granted at any level. If the school is null, it will apply the same logic at
     * the district level, returning true if role is granted at either district or state level.
     *
     * @param role role id or name, e.g. "ASMTDATALOAD"
     * @param state state id or name
     * @param district district id or name
     * @param school school id or name
     * @return true if role is granted for hierarchy
     */
    public boolean hasRole(final String role, final String state, final String district, final String school) {
        return hasRoleForState(role, state)
                || (district != null && hasRoleForDistrict(role, district))
                || (school != null && hasRoleForSchool(role, school));
    }

    /**
     * Return true if the role is granted for the client.
     *
     * @param role role id or name
     * @param client client id or name
     * @return true if role is granted for given client
     */
    public boolean hasRoleForClient(final String role, final String client) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, CLIENT, client));
    }

    /**
     * Return true if the role is granted for the state.
     *
     * @param role role id or name
     * @param state state id or name
     * @return true if role is granted for given state
     */
    public boolean hasRoleForState(final String role, final String state) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, STATE, state));
    }

    /**
     * Return true if the role is granted for the district.
     *
     * @param role role id or name
     * @param districtGroup district id or name
     * @return true if role is granted for given district
     */
    public boolean hasRoleForDistrictGroup(final String role, final String districtGroup) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, DISTRICT_GROUP, districtGroup));
    }

    /**
     * Return true if the role is granted for the district.
     *
     * @param role role id or name
     * @param district district id or name
     * @return true if role is granted for given district
     */
    public boolean hasRoleForDistrict(final String role, final String district) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, DISTRICT, district));
    }

    /**
     * Return true if the role is granted for the school group.
     *
     * @param role role id or name
     * @param schoolGroup school group id or name
     * @return true if role is granted for given school group
     */
    public boolean hasRoleForSchoolGroup(final String role, final String schoolGroup) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, INSTITUTION_GROUP, schoolGroup));
    }

    /**
     * Return true if the role is granted for the school.
     *
     * @param role role id or name
     * @param school school id or name
     * @return true if role is granted for given school
     */
    public boolean hasRoleForSchool(final String role, final String school) {
        return grants.stream().anyMatch(grant -> grant.hasRoleForLevel(role, INSTITUTION, school));
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        return Objects.equals(grants, ((TenancyChain)that).grants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grants);
    }
}
