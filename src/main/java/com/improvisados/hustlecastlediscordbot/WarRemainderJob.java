/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot;

import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Joaquin Martinez <juacom04@gmail.com>
 */
public class WarRemainderJob implements Job
{
    private static final Logger logger = LogManager.getLogger(WarRemainderJob.class.getName());
    

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
       
        JobDataMap data=context.getJobDetail().getJobDataMap();
        
        List<Guild> guilds =(List<Guild>) data.get("guilds");
        
        for(Guild guild:guilds)
        {
            guild.getDefaultChannel().sendMessage("@everyone  el clan esta en guerra. Por favor despligue sus tropas").queue();
        }
    }
    
}
