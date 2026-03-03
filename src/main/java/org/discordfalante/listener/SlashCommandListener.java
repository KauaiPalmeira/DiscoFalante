package org.discordfalante.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.JDA;
import org.discordfalante.service.MessageFormatter;
import org.discordfalante.service.MessageService;
import org.discordfalante.service.TTSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;


public class SlashCommandListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandListener.class);
    private final MessageService messageService = new MessageService();
    private final MessageFormatter formatter = new MessageFormatter();
    private final TTSService ttsService = new TTSService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("ler")) {
            String messageId = event.getOption("messageid").getAsString();

            log.info("Comando /ler acionado pelo usuário {} para a mensagem ID: {}", event.getUser().getName(), messageId);

            event.deferReply().queue();

            messageService.getMessagesAfter(event.getChannel(), messageId)
                    .thenAccept(messages -> {
                        log.info("Foram recuperadas {} mensagens. Iniciando formatação de texto...", messages.size());
                        String formattedText = formatter.format(messages);
                        try {
                            log.info("Iniciando geração de áudio via TTS...");
                            File audioFile = ttsService.generateAudio(formattedText);
                            log.info("Áudio gerado com sucesso. Fazendo upload para o Discord...");
                            event.getHook()
                                    .sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(audioFile))
                                    .queue();

                        } catch (Exception e) {
                            log.error("Erro interno durante a geração do áudio para a mensagem ID: {}", messageId, e);
                            event.getHook().sendMessage("Erro ao gerar áudio.").queue();
                        }

                    })
                    .exceptionally(error -> {
                        log.error("Falha na comunicação com a API do Discord ao buscar mensagens (ID: {})", messageId, error);
                        event.getHook().sendMessage("Não foi possível buscar as mensagens").queue();
                        return null;
                    });
        }
    }

    public static void registerCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("ler", "Ler mensagens após um ID")
                        .addOption(
                                net.dv8tion.jda.api.interactions.commands.OptionType.STRING,
                                "messageid",
                                "ID da mensagem inicial",
                                true
                        )
        ).queue();
    }
}