package org.cokeke.policysim.common;

import org.cokeke.policysim.models.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContextHolder {
    public AuthContextHolder() {}

    public User getCurrentUser() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return u;
    }

    public Long getCurrentUserId() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return u.getId();
    }
}
