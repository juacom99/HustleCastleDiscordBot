package com.improvisados.hustlecastlediscordbot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.improvisados.hustlecastlediscordbot.commands.TopicCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 *
 * @author joaquin
 */
public class HustleCastleBot extends ListenerAdapter {

    private JDA jda;
    private boolean respondeToBots;
    
    private static final Logger logger = LogManager.getLogger(HustleCastleBot.class.getName());
    

    public HustleCastleBot() throws InterruptedException, LoginException {
        
        respondeToBots = false;
        this.jda.addEventListener(this);
        
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("!");
        builder.setOwnerId("583339878637371412");
        builder.setGame(Game.playing("Huste Castle"));
        builder.addCommands(new TopicCommand());
        
        CommandClient client = builder.build();
        
        this.jda = new JDABuilder(AccountType.BOT).setToken("NTgzMzM5ODc4NjM3MzcxNDEy.XO9Haw.grdkaYgr20mc7Uis7P4HQm63R2E").build();
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        event.getGuild().getDefaultChannel().sendMessage("Welcome ***"+event.getUser().getName()+"*** to "+event.getGuild().getName()).queue();
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        event.getGuild().getDefaultChannel().sendMessage("Good bye ***"+event.getUser().getName()+"***, we are gonna miss you ").queue();
    }
    

   

  

    
    
}
