package org.cokeke.policysim.dtos;

import org.cokeke.policysim.models.User;
import org.cokeke.policysim.models.AppSession;

public record LoginUserServiceRes(User user, AppSession session) {
    
}
