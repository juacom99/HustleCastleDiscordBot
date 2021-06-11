/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.jobs;

import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jomartinez
 */
public class QuestRefreshJob implements Job
{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            JobDataMap data=context.getJobDetail().getJobDataMap();
            
            List<Guild> guilds =(List<Guild>) data.get("guilds");
            Configuration cfg=Configuration.getInstance();
            for(Guild guild:guilds)
            {
                guild.getTextChannelsByName(cfg.getWarAnnouncementChannelName(), true).get(0).sendMessage("Atención @everyone, Hay nuevas misiones en el territorio del clan").queue();
            }
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(QuestRefreshJob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
