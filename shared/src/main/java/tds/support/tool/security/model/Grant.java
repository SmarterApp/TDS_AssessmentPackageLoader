package tds.support.tool.security.model;
import java.util.Arrays;
import java.util.Objects;

import static tds.support.tool.security.model.Grant.Level.CLIENT;
import static tds.support.tool.security.model.Grant.Level.STATE;

/**
 * A helper class that wraps the field tokens providing named accessors.
 * It also does basic validation between grant level and field data.
 */
public class Grant {

    /**
     * Enum for grant level; values explicitly match ART specification
     */
    public enum Level {
        CLIENT(4),
        STATE_GROUP(6),
        STATE(8),
        DISTRICT_GROUP(10),
        DISTRICT(12),
        INSTITUTION_GROUP(14),
        INSTITUTION(16);

        // index of id field in tenancy chain
        private final int index;

        Level(final int index) {
            this.index = index;
        }

        int idx() {
            return this.index;
        }
    }

    private static final int idxEntityId = 1;
    private static final int idxRole = 2;
    private static final int idxLevel = 3;

    private final String[] tokens;
    private final Level level;  // cache to avoid parsing over and over

    private Grant(final String[] tokens) {
        this.tokens = tokens;
        this.level = Grant.Level.valueOf(tokens[idxLevel].toUpperCase());
    }

    public static Grant fromString(final String value) {
        final String[] tokens = value.split("\\|", -1);
        final Grant.Level level = Grant.Level.valueOf(tokens[idxLevel].toUpperCase());
        // required number of fields based on grant level
        if (tokens.length <= level.idx()) {
            throw new IllegalArgumentException("invalid tenancy chain " + value);
        }
        // to be formally correct, the entity id should match the id of the appropriate field
        if (!tokens[idxEntityId].equals(tokens[level.idx()])) {
            throw new IllegalArgumentException("invalid entity id: " + tokens[idxEntityId]);
        }

        return new Grant(tokens);
    }

    public String toString() {
        return String.join("|", tokens);
    }

    /**
     * Two grants are similar if they have the same role for the same entity. Note that two grants
     * may be similar while not being equal, if the underlying tokens have different levels of
     * completeness. In general, for well-formed tenancy chains, equal grants will be similar and
     * vice versa.
     *
     * @param that the grant to compare to
     * @return true if the grants have the same role and entity
     */
    public boolean similar(final Grant that) {
        return Objects.equals(getEntityId(), that.getEntityId())
                && Objects.equals(getRole(), that.getRole());
    }

    /**
     * Determine if this grant "contains" another grant. This is true if the grants refer to the
     * same role and this grant is at the same or higher level (e.g. DISTRICT is higher than
     * INSTITUTION) and has the same ancestry of the other grant. For example, if this grant is
     * role A for district D1, and that grant is role A for school S1 in district D1, then this
     * grant contains that grant.
     *
     * @param that the grant to check
     * @return true if this grant contains that grant
     */
    public boolean contains(final Grant that) {
        // roles must be the same
        if (!getRole().equals(that.getRole())) return false;

        // if role and entity id are the same, then they are similar and this contains that
        if (getEntityId().equals(that.getEntityId())) return true;

        // if this grant is more specific than that grant, then this can't contain that
        if (getLevel().idx() > that.getLevel().idx()) return false;

        // for this to contain that, they must match at this' level
        // NOTE: this assumes id's are unique across the hierarchy; if not then this needs to be
        // expanded to check all id's in this' ancestry, not just the final one.
        return tokens[level.idx()].equals(that.tokens[level.idx()]);
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        return Arrays.equals(tokens, ((Grant)that).tokens);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tokens);
    }

    /**
     * @param role role to check for
     * @param level level to check
     * @param id id of level, matched against id and name
     * @return true if role is assigned for the given level id
     */
    public boolean hasRoleForLevel(final String role, final Grant.Level level, final String id) {
        return level == getLevel() && role.equalsIgnoreCase(getRole())
                && (id.equalsIgnoreCase(getId()) || id.equalsIgnoreCase(getName()));
    }

    public String getEntityId() {
        return tokens[idxEntityId];
    }

    public String getRole() {
        return tokens[idxRole];
    }

    public Grant.Level getLevel() {
        return level;
    }

    public String getId() {
        return tokens[getLevel().idx()];
    }

    public String getName() {
        return tokens[getLevel().idx()+1];
    }

    public String getClientId() { return tokens[CLIENT.idx()]; }

    public String getStateId() { return tokens[STATE.idx()]; }
}