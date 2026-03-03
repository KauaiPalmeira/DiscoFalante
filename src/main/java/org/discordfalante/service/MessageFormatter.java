package org.discordfalante.service;

import org.discordfalante.model.StructuredMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageFormatter {

    public String format(List<StructuredMessage> messages) {
        StringBuilder sb = new StringBuilder();
        String lastAuthor = "";

        for (StructuredMessage msg : messages) {
            if (msg.getContent() == null || msg.getContent().isBlank()) continue;

            String cleanContent = msg.getContent().replace("\n", " ").trim();
            String currentAuthor = msg.getAuthorName();

            if (msg.getRepliedToAuthor() != null) {
                sb.append(currentAuthor).append(" respondeu a ").append(msg.getRepliedToAuthor()).append(": ");
                lastAuthor = currentAuthor;
            } else if (!currentAuthor.equals(lastAuthor)) {
                sb.append(currentAuthor).append(" disse: ");
                lastAuthor = currentAuthor;
            }

            sb.append(cleanContent).append(". ");
        }
        return sb.toString().trim();
    }
}