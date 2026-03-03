package org.discordfalante;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.discordfalante.listener.SlashCommandListener;
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
    /**
     TODO:
     Adicionar mais vozes para diferentes pessoas no trecho. <p>
     Ajustar naturalidade da voz. <p>
     Otimizar código. <p>
     Adicionar breakpoints e log. <p>
     Adicionar testes unitários. <p>
     Dockerizar instalação <p>
     {@code @Author:} Kauai Palmeira
     */
    @Bean
    public CommandLineRunner runBot() {
        return args -> {

            var jda = JDABuilder.createDefault(
                            token,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(new SlashCommandListener())
                    .build()
                    .awaitReady();

            SlashCommandListener.registerCommands(jda);
        };
    }
}

