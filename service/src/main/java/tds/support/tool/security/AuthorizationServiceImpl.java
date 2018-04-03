//package tds.support.tool.security;
//
//
//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Multimap;
//import com.google.common.collect.Multimaps;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Service;
//import tds.support.tool.security.model.Grant;
//import tds.support.tool.security.model.PermissionScope;
//import tds.support.tool.security.permission.PermissionService;
//
//import javax.validation.constraints.NotNull;
//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.google.common.collect.ImmutableSet.toImmutableSet;
//import static com.google.common.collect.Maps.newHashMap;
//
//public class AuthorizationServiceImpl {
//    private static final Logger logger = LoggerFactory.getLogger(DefaultAuthorizationService.class);
//
////    private final ReportingSystemSettings systemSettings;
//    private final PermissionService permissionService;
//    private final OrganizationRepository organizationRepository;
//
//    @Autowired
//    public DefaultAuthorizationService(
////            final ReportingSystemSettings systemSettings,
//            final PermissionService permissionService,
//            final OrganizationRepository organizationRepository) {
////        this.systemSettings = systemSettings;
//        this.permissionService = permissionService;
//        this.organizationRepository = organizationRepository;
//    }
//
//    @Override
//    public Set<Grant> getGrants(final String[] encodedGrants) {
//        final ImmutableSet.Builder<Grant> grants = ImmutableSet.builder();
//        for (final String encodedGrant : encodedGrants) {
//            try {
//                final Grant grant = Grant.fromString(encodedGrant);
//
//                // Only add grants for the application's configured state
//                //TODO: Make this generic
//                if ("CA".equals(grant.getStateId())) {
//                    grants.add(grant);
//                }
//            } catch (final IllegalArgumentException exception) {
//                // in a well-behaved and properly configured ecosystem this should never happen
//                // so log a WARN level message so the log may be monitored
//                logger.warn("invalid grant: {}", encodedGrant);
//            }
//        }
//        return grants.build();
//    }
//
//    // A Grant is a role granted for a state or organization identified by its natural id.
//    // A Permission is a permission and collection of organizations identified by their internal ids.
//    // This method maps the grant roles to the associated permissions, collects and dedups the
//    // organizations that share that permission, and resolves natural ids to internal ids.
//    @Override
//    public Set<Permission> getPermissions(final Collection<Grant> grants) {
//
//        // Groups grants by role to later combine their districts and institutions into a single scope
//        // This is to cover the case of multiple non-intersecting grants being given
//        // (e.g. roleA,stateA,districtA and roleA,stateA,districtB)
//        final Multimap<String, Grant> grantsByRole = Multimaps.index(grants, Grant::getRole);
//        final Map<String, Collection<String>> permissionsByRole = getPermissionsByRole();
//
////        final Map<String, Long> districtGroupIdsByNaturalId = organizationRepository.findAllDistrictGroupIds(grants.stream()
////                .filter(grant -> grant.getLevel() == DISTRICT_GROUP)
////                .map(Grant::getEntityId).collect(Collectors.toSet()));
////        final Map<String, Long> districtIdsByNaturalId = organizationRepository.findAllDistrictIds(grants.stream()
////                .filter(grant -> grant.getLevel() == DISTRICT)
////                .map(Grant::getEntityId).collect(Collectors.toSet()));
////        final Map<String, Long> schoolGroupIdsByNaturalId = organizationRepository.findAllSchoolGroupIds(grants.stream()
////                .filter(grant -> grant.getLevel() == INSTITUTION_GROUP)
////                .map(Grant::getEntityId).collect(Collectors.toSet()));
////        final Map<String, Long> schoolIdsByNaturalId = organizationRepository.findAllSchoolIds(grants.stream()
////                .filter(grant -> grant.getLevel() == INSTITUTION)
////                .map(Grant::getEntityId).collect(Collectors.toSet()));
//
//        // Identify and store the permissions associated with the role
//        final Map<String, PermissionScope.Builder> permissionScopeBuildersByPermissionId = newHashMap();
//        for (Map.Entry<String, Grant> entry : grantsByRole.entries()) {
//
//            final Collection<String> permissionIds = permissionsByRole.get(entry.getKey());
//
//            // The user may be granted roles that are not registered to this application in the permission configuration.
//            // These roles should be ignored as they do not apply to our application
//            if (permissionIds == null) continue;
//
//            final Grant grant = entry.getValue();
//            for (final String permissionId : permissionIds) {
//                final PermissionScope.Builder permissionScopeBuilder = permissionScopeBuildersByPermissionId
//                        .computeIfAbsent(permissionId, (key) -> PermissionScope.builder());
//
//                switch(grant.getLevel()) {
//                    case STATE:
//                        permissionScopeBuilder.statewide(true);
//                        break;
//                    case DISTRICT_GROUP:
//                        addDistrictGroup(permissionScopeBuilder, grant.getEntityId(), districtGroupIdsByNaturalId);
//                        break;
//                    case DISTRICT:
//                        addDistrict(permissionScopeBuilder, grant.getEntityId(), districtIdsByNaturalId);
//                        break;
//                    case INSTITUTION_GROUP:
//                        addSchoolGroup(permissionScopeBuilder, grant.getEntityId(), schoolGroupIdsByNaturalId);
//                        break;
//                    case INSTITUTION:
//                        addSchool(permissionScopeBuilder, grant.getEntityId(), schoolIdsByNaturalId);
//                        break;
//                    case CLIENT:
//                    case STATE_GROUP:
//                    default:
//                        break;
//                }
//            }
//        }
//
//        return permissionScopeBuildersByPermissionId.entrySet().stream()
//                // missing IDs in the natural ID to entity ID map can result in an invalid permission scope builder
//                // that is neither statewide nor containing any schools and districts so this will filter those out
//                .filter(entry -> entry.getValue().isValid())
//                .map(entry -> new Permission(entry.getKey(), entry.getValue().build()))
//                .collect(toImmutableSet());
//    }
//
//    @Override
//    public Set<GrantedAuthority> getAuthorities(final Collection<Grant> grants) {
//        final Map<String, Collection<String>> permissionsByRole = getPermissionsByRole();
//        final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();
//        for (Grant grant : grants) {
//            if (permissionsByRole.containsKey(grant.getRole())) {
//                authorities.add(Authorities.createRole(grant.getRole()));
//                final Collection<String> permissionIds = permissionsByRole.get(grant.getRole());
//                if (permissionIds != null) {
//                    for (String permission : permissionIds) {
//                        authorities.add(Authorities.createPermission(permission));
//                    }
//                }
//            }
//        }
//        return authorities.build();
//    }
//
//    private Map<String, Collection<String>> getPermissionsByRole() {
//        try {
//            return permissionService.getPermissionsByRole();
//        } catch (PermissionServiceException exception) {
//            throw new RuntimeException("Failed to get permissions", exception);
//        }
//    }
//
//    private void addDistrictGroup(final PermissionScope.Builder builder, final String entityId, final Map<String, Long> idsByNaturalId) {
//        final Long id = idsByNaturalId.get(entityId);
//        if (id != null) {
//            builder.addDistrictGroupId(id);
//        } else {
//            logger.warn("grant district group with natural ID \"{}\" not found", entityId);
//        }
//    }
//
//    private void addDistrict(final PermissionScope.Builder builder, final String entityId, final Map<String, Long> idsByNaturalId) {
//        final Long id = idsByNaturalId.get(entityId);
//        if (id != null) {
//            builder.addDistrictId(id);
//        } else {
//            logger.warn("grant district with natural ID \"{}\" not found", entityId);
//        }
//    }
//
//    private void addSchoolGroup(final PermissionScope.Builder builder, final String entityId, final Map<String, Long> idsByNaturalId) {
//        final Long id = idsByNaturalId.get(entityId);
//        if (id != null) {
//            builder.addSchoolGroupId(id);
//        } else {
//            logger.warn("grant school group with natural ID \"{}\" not found", entityId);
//        }
//    }
//
//    private void addSchool(final PermissionScope.Builder builder, final String entityId, final Map<String, Long> idsByNaturalId) {
//        final Long id = idsByNaturalId.get(entityId);
//        if (id != null) {
//            builder.addSchoolId(id);
//        } else {
//            logger.warn("grant school with natural ID \"{}\" not found", entityId);
//        }
//    }
//}
