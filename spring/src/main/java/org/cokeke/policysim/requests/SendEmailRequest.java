package org.cokeke.policysim.requests;

public record SendEmailRequest(Long userId,
    String senderEmail, String title,
    String subject, String content
) {}