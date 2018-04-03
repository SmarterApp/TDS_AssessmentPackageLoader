package tds.support.tool.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import tds.support.tool.security.model.User;

import javax.validation.constraints.NotNull;
import java.util.stream.Collectors;

@Service
public class UserContextServiceImpl implements UserContextService {
    @Override
    public UserContext getUserContext(@NotNull final User user) {
        return UserContext.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .permissions(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .build();
    }
}
