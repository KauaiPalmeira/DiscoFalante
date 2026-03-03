package org.discordfalante.service;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.discordfalante.model.StructuredMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageService {
    public CompletableFuture<List<StructuredMessage>> getMessagesAfter(MessageChannel channel, String messageId) {
        return channel.retrieveMessageById(messageId).submit().thenCompose(originalMsg -> {
            CompletableFuture<List<StructuredMessage>> future = new CompletableFuture<>();

            channel.getHistoryAfter(messageId, 100).queue(history -> {
                List<Message> allMessages = new ArrayList<>();
                allMessages.add(originalMsg);
                allMessages.addAll(history.getRetrievedHistory());

                List<StructuredMessage> structuredMessages = new ArrayList<>();
                for (Message msg : allMessages) {
                    if (msg.getAuthor().isBot()) continue;

                    String repliedToAuthor = (msg.getReferencedMessage() != null)
                            ? msg.getReferencedMessage().getAuthor().getName() : null;

                    structuredMessages.add(new StructuredMessage(msg.getAuthor().getName(), msg.getContentDisplay(), repliedToAuthor));
                }
                Collections.reverse(structuredMessages);
                future.complete(structuredMessages);
            }, future::completeExceptionally);

            return future;
        });
    }}