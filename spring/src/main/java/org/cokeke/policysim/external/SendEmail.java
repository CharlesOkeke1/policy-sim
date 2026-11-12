package org.cokeke.policysim.external;

import java.util.List;

import org.cokeke.policysim.common.EnvConfig;
import org.cokeke.policysim.exceptions.EmailSendException;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;

public class SendEmail {
    private static final String TOKEN = EnvConfig.get("MAILTRAP_API_TOKEN");
    private static final String SUPPORT_EMAIL = EnvConfig.get("SUPPORT_EMAIL");
    private static final MailtrapConfig config = new MailtrapConfig.Builder()
            .token(TOKEN)
            .build();
    private static final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

    public static void contactSupoort(
        String senderEmail, 
        String title,
        String subject,
        String content
    ) {        
        final MailtrapMail mail = MailtrapMail.builder()
            .from(new Address(SUPPORT_EMAIL, "PolicySim Support"))
            .to(List.of(new Address(SUPPORT_EMAIL)))
            .replyTo(new Address(senderEmail, title))
            .subject(subject)
            .text(content)
            .category("Support")
            .build();

        try {
            System.out.println(client.send(mail));
        } catch (Exception e) {
            System.out.println(e);
            throw new EmailSendException(e.getMessage());
        }
    
    }        
}