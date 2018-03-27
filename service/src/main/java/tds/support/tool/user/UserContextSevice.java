package tds.support.tool.user;

import tds.support.tool.security.model.User;

import javax.validation.constraints.NotNull;

public interface UserContextSevice {
    UserContext getUserContext(@NotNull User user);
}
