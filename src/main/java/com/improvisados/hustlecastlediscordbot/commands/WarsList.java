/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.commands;

import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.logging.Level;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@CommandInfo(
        name
        = {
            "warslist"
        },
        description = "List the wars for the clan"
)
@Author("Joaquin Martinez")
public class WarsList extends Command {

    private static final Logger logger = LogManager.getLogger(WarsList.class.getName());

    public WarsList() {
        this.name = "warslist";
        this.help = "List the wars for the clan";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent ce) {
        Configuration cfg;
        try {
            cfg = Configuration.getInstance();
            Iterator<LocalTime> wars = cfg.getWars();
            int c = 0;
            MessageBuilder builder = new MessageBuilder();
            
            EmbedBuilder eb=new EmbedBuilder();
            
            LocalTime war;
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            builder.append("***War List for the clan ***\n");
            
            eb.setTitle("***War List for the clan ***");
            
            while (wars.hasNext()) {
                war = wars.next();
                
                //builder.append("[" + c + "] " +fmt.print(war) +" \n");
                eb.addField("",(c+1)+") "+fmt.print(war),false);
                c++;
            }

            //ce.getChannel().sendMessage(builder.build()).queue();
            ce.getChannel().sendMessage(eb.build()).queue();
            
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(WarsList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
