package tds.support.tool.security.common;

import java.util.Collection;
import java.util.Map;

/**
 * Repository for getting at organizations: districts, schools, groups.
 */
public interface OrganizationRepository {

    /**
     * Returns a map of natural id to system (database) id for district groups.
     *
     * @param natualIds natural ids to look up
     * @return map of natural id to system id
     */
    Map<String, Long> findAllDistrictGroupIds(Collection<String> natualIds);

    /**
     * Returns a map of natural id to system (database) id for districts.
     *
     * @param natualIds natural ids to look up
     * @return map of natural id to system id
     */
    Map<String, Long> findAllDistrictIds(Collection<String> natualIds);

    /**
     * Returns a map of natural id to system (database) id for school groups.
     *
     * @param natualIds natural ids to look up
     * @return map of natural id to system id
     */
    Map<String, Long> findAllSchoolGroupIds(Collection<String> natualIds);

    /**
     * Returns a map of natural id to system (database) id for schools.
     *
     * @param natualIds natural ids to look up
     * @return map of natural id to system id
     */
    Map<String, Long> findAllSchoolIds(Collection<String> natualIds);
}
