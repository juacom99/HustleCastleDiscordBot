package com.improvisados.hustlecastlediscordbot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.improvisados.hustlecastlediscordbot.jobs.WarStartsRemainderJob;
import com.improvisados.hustlecastlediscordbot.commands.ManCommand;
import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import com.improvisados.hustlecastlediscordbot.jobs.WarAboutToBeginJob;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
public class HustleCastleBot extends ListenerAdapter {

    private JDA jda;
    private boolean respondeToBots;
    private static final Logger logger = LogManager.getLogger(HustleCastleBot.class.getName());

    private Scheduler scheduler;

    public HustleCastleBot(String token, String owner) throws InterruptedException, LoginException {

        this.jda = new JDABuilder(AccountType.BOT).setToken(token).build();

        respondeToBots = false;
        this.jda.addEventListener(this);
        this.jda.getPresence().setActivity(Activity.playing("Hustle Castle"));

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getGuild().getDefaultChannel().sendMessage("Welcome ***" + event.getUser().getName() + "*** to " + event.getGuild().getName()).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        String owner = "368791176796700672";
        String cronWarStarts = "0 0 10,16,22 ? * * *";
        String cronAboutToBegin = "0 0 10,16,22 ? * * *";
        try {
            Configuration cfg = Configuration.getInstance();
            owner = cfg.getOwner();
            cronWarStarts = cfg.getCronWarStarts();
            cronAboutToBegin = cfg.getCronAboutToBegin();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("!");
        builder.setOwnerId(owner);
        builder.addCommands(new ManCommand());

        CommandClient client = builder.build();

        jda.addEventListener(client);

        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            this.scheduler = sf.getScheduler();

            WarStartsRemainderJob warReminder = new WarStartsRemainderJob();
            Date startTime = DateBuilder.nextGivenSecondDate(null, 10);

            JobDetail jobWarStarts = JobBuilder.newJob(WarStartsRemainderJob.class).withIdentity("Clan War Starts", "group1").build();
            jobWarStarts.getJobDataMap().put("guilds", jda.getGuilds());

            CronTrigger crontriggerWarStarts = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Clan War War Starts remainder Job", "group1")
                    .startAt(startTime)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronWarStarts))
                    .build();

            JobDetail jobWarAboutToBegin = JobBuilder.newJob(WarAboutToBeginJob.class).withIdentity("Clan War About to Begin", "group2").build();
            jobWarAboutToBegin.getJobDataMap().put("guilds", jda.getGuilds());
            CronTrigger crontriggerWarAboutToBegin = TriggerBuilder
                    .newTrigger()
                    .withIdentity("Clan War About to Begin remainder Job", "group2")
                    .startAt(startTime)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronAboutToBegin))
                    .build();

            scheduler.start();
            scheduler.scheduleJob(jobWarStarts, crontriggerWarStarts);
            scheduler.scheduleJob(jobWarAboutToBegin, crontriggerWarAboutToBegin);

        } catch (SchedulerException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getGuild().getDefaultChannel().sendMessage("Good bye ***" + event.getUser().getName() + "***, we are gonna miss you ").queue();
    }

}
