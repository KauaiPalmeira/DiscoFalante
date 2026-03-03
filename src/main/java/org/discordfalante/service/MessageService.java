package org.discordfalante.service;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.discordfalante.model.StructuredMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    public CompletableFuture<List<StructuredMessage>> getMessagesAfter(MessageChannel channel, String messageId) {
        log.info("Solicitando a mensagem inicial (ID: {}) no canal: {}", messageId, channel.getName());

        return channel.retrieveMessageById(messageId).submit().thenCompose(originalMsg -> {
            CompletableFuture<List<StructuredMessage>> future = new CompletableFuture<>();
            log.info("Mensagem inicial encontrada. Buscando o histórico subsequente...");

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
                log.info("Estruturação concluída. {} mensagens úteis filtradas.", structuredMessages.size());
                future.complete(structuredMessages);

            }, error -> {
                log.error("Falha ao recuperar o histórico após a mensagem ID: {}", messageId, error);
                future.completeExceptionally(error);
            });

            return future;
        });
    }}