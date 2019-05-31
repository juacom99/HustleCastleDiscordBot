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

@CommandInfo(
    name = {"topic"},
    description = "Read on a topic"
    
)
@Author("Joaquin Martinez")
public class TopicCommand extends Command
{

    public TopicCommand()
    {
        this.name = "topic";
        this.help = "Read on a topic";
        this.guildOnly = true;
    }
    
    

    @Override
    protected void execute(CommandEvent ce)
    {
        String args=ce.getArgs();
        
        if(args!=null && !args.trim().equals(""))
        {
            ce.reply("Hello World!");
        }
        else
        {
            ce.reply("Topico no encontrado");
        }
    }
}
