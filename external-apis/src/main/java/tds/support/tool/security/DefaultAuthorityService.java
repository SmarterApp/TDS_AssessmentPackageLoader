package tds.support.tool.security;

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import tds.support.tool.security.common.OrganizationRepository;
import tds.support.tool.security.common.Permission;
import tds.support.tool.security.common.TenancyChain;
import tds.support.tool.security.model.Grant;
import tds.support.tool.security.model.PermissionScope;
import tds.support.tool.security.permission.PermissionService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.util.StringUtils.hasText;
import static tds.support.tool.security.Authorities.permissionToAuthorityName;
import static tds.support.tool.security.Authorities.roleToAuthorityName;
import static tds.support.tool.security.model.Grant.Level.*;

/**
 * This service is responsible for converting tenancy chain grants to spring authorities.
 */
@Service
class DefaultAuthorityService implements AuthorityService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAuthorityService.class);

    private final PermissionService permissionService;
    private final OrganizationRepository organizationRepository;
    private final String client;
    private final String state;

    @Autowired
    public DefaultAuthorityService(final PermissionService permissionService,
                                   final OrganizationRepository organizationRepository,
                                   final @Value("${support-tool.client:}") String client,
                                   final @Value("${support-tool.progman-tenant:CA}") String state) {
        this.permissionService = permissionService;
        this.organizationRepository = organizationRepository;
        this.client = client;
        this.state = state;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities(final TenancyChain tenancyChain) {
        final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();

        final Map<String, Collection<String>> permissionsByRole = getPermissionsByRole();
        for (final Grant grant : getGrants(tenancyChain)) {
            authorities.add(simpleRoleAuthorityMapper.apply(grant.getRole()));
            for (final String permission : permissionsByRole.get(grant.getRole())) {
                authorities.add(simplePermissionAuthorityMapper.apply(permission));
            }
        }

        return authorities.build();
    }

    @Override
    public Set<Permission> getPermissions(final TenancyChain tenancyChain) {
        final Map<String, PermissionScope.Builder> builders = newHashMap();
        final List<Grant> grants = getGrants(tenancyChain);

        // get reference data needed to map roles to permissions
        final Map<String, Collection<String>> permissionsByRole = getPermissionsByRole();
        final Map<Grant.Level, Map<String, Long>> orgMap = getOrganizationsById(grants);

        Long id;
        for (final Grant grant : grants) {
            for (final String permission : permissionsByRole.get(grant.getRole())) {
                final PermissionScope.Builder builder = builders.computeIfAbsent(permission, p -> PermissionScope.builder());

                switch(grant.getLevel()) {
                    case CLIENT:
                    case STATE_GROUP:
                    case STATE:
                        builder.statewide(true);
                        break;
                    case DISTRICT_GROUP:
                        if ((id = orgMap.get(DISTRICT_GROUP).get(grant.getEntityId())) != null)
                            builder.addDistrictGroupId(id);
                        break;
                    case DISTRICT:
                        if ((id = orgMap.get(DISTRICT).get(grant.getEntityId())) != null)
                            builder.addDistrictId(id);
                        break;
                    case INSTITUTION_GROUP:
                        if ((id = orgMap.get(INSTITUTION_GROUP).get(grant.getEntityId())) != null)
                            builder.addSchoolGroupId(id);
                        break;
                    case INSTITUTION:
                        if ((id = orgMap.get(INSTITUTION).get(grant.getEntityId())) != null)
                            builder.addSchoolId(id);
                        break;
                    default:
                        break;
                }
            }
        }

        return builders.entrySet().stream()
                .filter(entry -> entry.getValue().isValid())
                .map(entry -> new Permission(entry.getKey(), entry.getValue().build()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<Grant> getGrants(final TenancyChain tenancyChain) {
        final List<Grant> grants = newArrayList();

        final Map<String, Collection<String>> permissionsByRole = getPermissionsByRole();
        for (final Grant grant : tenancyChain.getGrants()) {
            // skip grants that don't match client and state
            if ((hasText(client) && hasText(grant.getClientId()) && !client.equalsIgnoreCase(grant.getClientId()))
             || (hasText(state) && hasText(grant.getStateId()) && !state.equalsIgnoreCase(grant.getStateId()))) {
                logger.debug("Skipping grant for other client/state {}", grant.toString());
                continue;
            }

            if (permissionsByRole.containsKey(grant.getRole())) {
                grants.add(grant);
            }
        }

        return grants;
    }

    /**
     * A role mapper that prepends "ROLE_" and creates a {@link SimpleGrantedAuthority}
     */
    private static final Function<String, SimpleGrantedAuthority> simpleRoleAuthorityMapper =
            role -> new SimpleGrantedAuthority(roleToAuthorityName(role));

    /**
     * A permission mapper that prepends "PERM_" and creates a {@link SimpleGrantedAuthority}
     */
    private static final Function<String, SimpleGrantedAuthority> simplePermissionAuthorityMapper =
            permission -> new SimpleGrantedAuthority(permissionToAuthorityName(permission));

    private Map<String, Collection<String>> getPermissionsByRole() {
        try {
            return permissionService.getPermissionsByRole();
        } catch (final UnauthorizedUserException exception) {
            throw new RuntimeException("Failed to get permissions", exception);
        }
    }

    private Map<Grant.Level, Map<String, Long>> getOrganizationsById(final List<Grant> grants) {
        final Map<Grant.Level, Map<String, Long>> orgMap = newHashMap();
        orgMap.put(DISTRICT_GROUP, organizationRepository.findAllDistrictGroupIds(grants.stream()
                .filter(grant -> DISTRICT_GROUP == grant.getLevel()).map(Grant::getEntityId).collect(Collectors.toSet())));
        orgMap.put(DISTRICT, organizationRepository.findAllDistrictIds(grants.stream()
                .filter(grant -> DISTRICT == grant.getLevel()).map(Grant::getEntityId).collect(Collectors.toSet())));
        orgMap.put(INSTITUTION_GROUP, organizationRepository.findAllSchoolGroupIds(grants.stream()
                .filter(grant -> INSTITUTION_GROUP == grant.getLevel()).map(Grant::getEntityId).collect(Collectors.toSet())));
        orgMap.put(INSTITUTION, organizationRepository.findAllSchoolIds(grants.stream()
                .filter(grant -> INSTITUTION == grant.getLevel()).map(Grant::getEntityId).collect(Collectors.toSet())));
        return orgMap;
    }
}
