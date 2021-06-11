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
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author jomartinez
 */
/**
 *
 * @author jomartinez
 */
@CommandInfo(
        name =
        {
            "createInvite"
        },
        description = "Create an invitation to the server"
)
@Author("Joaquin Martinez")
public class CreateInviteCommand extends Command 
{
    private static final Logger logger = LogManager.getLogger(CreateInviteCommand.class.getName());
    private static String[] REQUIRED_ROLES=new String[]{"Warlord","Deputy  Leader","Leader"};
    public CreateInviteCommand()
    {
        this.name = "createInvite";
        this.help = "Create an invitation to the server";
        this.guildOnly = false;
        this.cooldown=5;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        
       if(canExecute(event))
       {
       InviteAction inviteAction =event.getGuild().getDefaultChannel().createInvite().setMaxAge(0);
       
       Invite invite=inviteAction.complete();
       
       logger.debug("Invitation Created "+invite.getUrl());
       
       event.reply(invite.getUrl());
       }
       else
       {
           event.reply("You don't have de role to execute this command");
       }
    }
    
    
    private boolean canExecute(CommandEvent event)
    {
        Guild guild= event.getGuild();
        List<Role> roles=guild.getMember(event.getAuthor()).getRoles();
        boolean out=false;
        for(Role role:roles)
        {
            for(String roleName:REQUIRED_ROLES)
            {
                if(role.getName().equalsIgnoreCase(roleName))
                {
                    out=true;
                    break;
                }
            }
        }
        
        return out;
    }
}
