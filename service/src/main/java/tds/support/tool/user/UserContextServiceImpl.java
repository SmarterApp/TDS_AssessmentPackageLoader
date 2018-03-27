package tds.support.tool.user;

import org.springframework.stereotype.Service;
import tds.support.tool.security.model.User;

import javax.validation.constraints.NotNull;

@Service
public class UserContextServiceImpl implements UserContextSevice {
    @Override
    public UserContext getUserContext(@NotNull final User user) {
        return UserContext.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .permissions(user.getPermissionsById().keySet())
                .build();
    }
}
