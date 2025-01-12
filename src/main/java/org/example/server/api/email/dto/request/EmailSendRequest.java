package org.example.server.api.email.dto.request;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.List;
import lombok.Builder;

@Builder
public record EmailSendRequest(
        String from,
        List<String> to,
        String subject,
        String content
) {
    public SendEmailRequest toSendEmailRequest() {
        return new SendEmailRequest()
                .withSource(from)
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(createMessage());
    }

    private Message createMessage() {
        return new Message()
                .withSubject(createContent(subject))
                .withBody(new Body().withHtml(createContent(content)));
    }

    private Content createContent(String text) {
        return new Content().withCharset("UTF-8").withData(text);
    }
}
