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
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author joaquin
 */
public class HustleCastleBot extends ListenerAdapter
{

    private JDA jda;
    private boolean respondeToBots;
    private static final Logger logger = LogManager.getLogger(HustleCastleBot.class.getName());


    private Scheduler scheduler;

    public HustleCastleBot(String token, String owner) throws InterruptedException, LoginException
    {

        this.jda = new JDABuilder(AccountType.BOT).setToken(token).build();

        respondeToBots = false;
        this.jda.addEventListener(this);

        
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event)
    {
        event.getGuild().getDefaultChannel().sendMessage("Welcome ***" + event.getUser().getName() + "*** to " + event.getGuild().getName()).queue();
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("!");
        builder.setOwnerId("368791176796700672");
        builder.setGame(Game.playing("Huste Castle"));
        builder.addCommands(new TopicCommand());
        

        CommandClient client = builder.build();

       jda.addEventListener(client);

       
         SchedulerFactory sf = new StdSchedulerFactory();
        try
        {
            this.scheduler = sf.getScheduler();

            WarRemainderJob warReminder = new WarRemainderJob();
            Date startTime = DateBuilder.nextGivenSecondDate(null, 10);

            JobDetail job = JobBuilder.newJob(WarRemainderJob.class).withIdentity("Clan War remainder", "group1").build();
            job.getJobDataMap().put("guilds",jda.getGuilds());
            
            //String cron="* * 10,15,20 ? * * *";
            String cron="0/5 * * ? * * *";
            CronTrigger crontrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Clan War remainder Job", "group1")
                    .startAt(startTime)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

           // scheduler.start();
           // scheduler.scheduleJob(job, crontrigger);

        }
        catch (SchedulerException ex)
        {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event)
    {
        event.getGuild().getDefaultChannel().sendMessage("Good bye ***" + event.getUser().getName() + "***, we are gonna miss you ").queue();
    }

}
