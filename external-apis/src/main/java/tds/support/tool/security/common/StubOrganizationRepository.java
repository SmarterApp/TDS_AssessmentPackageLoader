package tds.support.tool.security.common;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Greg Charles on 7/27/18.
 */
@Component
public class StubOrganizationRepository implements OrganizationRepository {
    @Override
    public Map<String, Long> findAllDistrictGroupIds(Collection<String> natualIds) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> findAllDistrictIds(Collection<String> natualIds) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> findAllSchoolGroupIds(Collection<String> natualIds) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Long> findAllSchoolIds(Collection<String> natualIds) {
        return Collections.emptyMap();
    }
}
