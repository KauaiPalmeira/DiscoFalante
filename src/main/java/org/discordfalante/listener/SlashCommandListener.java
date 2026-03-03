package org.discordfalante.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.JDA;
import org.discordfalante.service.MessageFormatter;
import org.discordfalante.service.MessageService;
import org.discordfalante.service.TTSService;
import java.io.File;

public class SlashCommandListener extends ListenerAdapter {

    private final MessageService messageService = new MessageService();

    private final MessageFormatter formatter = new MessageFormatter();

    private final TTSService ttsService = new TTSService();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("ler")) {

            String messageId = event.getOption("messageid").getAsString();

            event.deferReply().queue();

            messageService.getMessagesAfter(event.getChannel(), messageId)
                    .thenAccept(messages -> {

                        String formattedText = formatter.format(messages);
                        try {

                            File audioFile = ttsService.generateAudio(formattedText);

                            event.getHook()
                                    .sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(audioFile))
                                    .queue();

                        } catch (Exception e) {
                            event.getHook().sendMessage("Erro ao gerar áudio.").queue();
                        }

                    })
                    .exceptionally(error -> {
                        event.getHook().sendMessage("Erro ao buscar mensagens.").queue();
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