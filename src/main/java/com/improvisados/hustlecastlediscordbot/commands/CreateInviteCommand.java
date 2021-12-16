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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author jomartinez
 */
/**
 *
 * @author jomartinez
 */
@CommandInfo(
        name
        = {
            "createInvite"
        },
        description = "Create an invitation to the server"
)
@Author("Joaquin Martinez")
public class CreateInviteCommand extends Command {

    private static final Logger logger = LogManager.getLogger(CreateInviteCommand.class.getName());
    private static String[] REQUIRED_ROLES = new String[]{"Warlords", "Deputy Leaders", "Leaders"};

    public CreateInviteCommand() {
        this.name = "createInvite";
        this.help = "Create an invitation to the server";
        this.guildOnly = false;
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent event) {

        if (canExecute(event)) {
            InviteAction inviteAction = event.getGuild().getDefaultChannel().createInvite().setMaxAge(0);

            Invite invite = inviteAction.complete();

            logger.debug("Invitation Created " + invite.getUrl());

            event.reply(invite.getUrl());
        } else {
            event.reply("You don't have de role to execute this command");
        }
    }

    private boolean canExecute(CommandEvent event) {
        boolean out = false;
        Guild guild = event.getGuild();
        final List<Role> roles = new ArrayList<>();

        CompletableFuture<Member> m=guild.retrieveMemberById(event.getAuthor().getId()).submit();
               

        try {
            for (Role role : m.get().getRoles()) {
                
                for (String roleName : REQUIRED_ROLES) {
                    if (role.getName().equalsIgnoreCase(roleName)) {
                        out = true;
                        break;
                    }
                }
            }
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(CreateInviteCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            java.util.logging.Logger.getLogger(CreateInviteCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }
}
