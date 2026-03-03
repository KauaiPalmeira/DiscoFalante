package org.discordfalante;

import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BotApplication {

    @Value("${discord.bot.token}")
    private String token;

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @Bean
    public CommandLineRunner runBot() {
        return args -> {

            JDABuilder.createDefault(token)
//                    .addEventListeners(new SlashCommandListener())
                    .build();
        };
    }
}