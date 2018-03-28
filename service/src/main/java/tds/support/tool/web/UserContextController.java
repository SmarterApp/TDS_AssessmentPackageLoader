package tds.support.tool.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tds.support.tool.security.model.User;
import tds.support.tool.user.UserContext;
import tds.support.tool.user.UserContextService;

@RestController
@RequestMapping("/api/user")
class UserContextController {

    private final UserContextService service;

    UserContextController(final UserContextService service) {
        this.service = service;
    }

    @GetMapping
    public UserContext getUserContext(@AuthenticationPrincipal final User user) {
        return service.getUserContext(user);
    }

}
