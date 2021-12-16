/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jomartinez
 */
@CommandInfo(
        name =
        {
            "inviteme"
        },
        description = "invite this bot to your server"
)
@Author("Joaquin Martinez")
public class InvetemeCommand extends Command {

    private static final Logger logger = LogManager.getLogger(InvetemeCommand.class.getName());
    private String botId;
    private long permision;

    public InvetemeCommand(String botId, long permision) {
        this.botId = botId;
        this.permision = permision;
        this.name = "inviteme";
        this.help = "invite this bot to your server";
        this.guildOnly = false;
        this.cooldown=600;
    }
    
    
    
    
    @Override
    protected void execute(CommandEvent ce) {
        
        EmbedBuilder builder=new EmbedBuilder();
        
        
        
        builder.setAuthor("Invite me to your Server","https://discordapp.com/oauth2/authorize?client_id=583339878637371412&scope=bot&permissions=2110782967");
        builder.setThumbnail("https://cdn.discordapp.com/app-icons/583339878637371412/cd2d213b310bc982e6a3073318ac0740.png?size=64");
        builder.setDescription("Invite this awesome bot to your server ");
        ce.getChannel().sendMessage(builder.build()).queue();
    }
}
