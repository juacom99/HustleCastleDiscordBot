package com.improvisados.hustlecastlediscordbot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.improvisados.hustlecastlediscordbot.commands.WarsList;
import com.improvisados.hustlecastlediscordbot.jobs.WarStartsRemainderJob;
import com.improvisados.hustlecastlediscordbot.commands.ManCommand;
import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import com.improvisados.hustlecastlediscordbot.jobs.WarAboutToBeginJob;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
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
        event.getGuild().getDefaultChannel().sendMessage("Bienvenido ***" + event.getUser().getName() + "*** a " + event.getGuild().getName()).queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        String owner = "368791176796700672";

        try {
            Configuration cfg = Configuration.getInstance();
            owner = cfg.getOwner();
            setUpSchedule(cfg.getWars());
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("!");
        builder.setOwnerId(owner);
        builder.addCommands(new ManCommand());
        builder.addCommand(new WarsList());

        CommandClient client = builder.build();

        jda.addEventListener(client);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getGuild().getDefaultChannel().sendMessage("Good bye ***" + event.getUser().getName() + "***, we are gonna miss you ").queue();
    }


    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();
        setUpRoles(guild);
        setUpTextAdminChat(guild);
    }

    private void setUpRoles(Guild guild) {
        //Set up basic permissions
        ArrayList<Permission> perms = new ArrayList<Permission>();
        perms.add(Permission.NICKNAME_CHANGE);
        perms.add(Permission.VIEW_CHANNEL);
        perms.add(Permission.MESSAGE_EMBED_LINKS);
        perms.add(Permission.MESSAGE_HISTORY);
        perms.add(Permission.MESSAGE_EXT_EMOJI);
        perms.add(Permission.MESSAGE_READ);
        perms.add(Permission.MESSAGE_WRITE);
        perms.add(Permission.MESSAGE_ATTACH_FILES);
        perms.add(Permission.MESSAGE_ADD_REACTION);
        perms.add(Permission.VOICE_CONNECT);
        perms.add(Permission.VOICE_SPEAK);
        perms.add(Permission.VOICE_USE_VAD);

        //Create Roles with basic permissions
        if (guild.getRolesByName("Solder", true).isEmpty()) {
            guild.createRole()
                    .setName("Solder")
                    .setPermissions(perms)
                    .setColor(new Color(52, 152, 219))
                    .setHoisted(true)
                    .complete();
        }

        if (guild.getRolesByName("Advisor", true).isEmpty()) {
            guild.createRole()
                    .setName("Advisor")
                    .setPermissions(perms)
                    .setColor(new Color(26, 188, 156))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.NICKNAME_MANAGE);
        perms.add(Permission.CREATE_INSTANT_INVITE);
        perms.add(Permission.MESSAGE_MENTION_EVERYONE);

        if (guild.getRolesByName("Officer", true).isEmpty()) {
            guild.createRole()
                    .setName("Officer")
                    .setPermissions(perms)
                    .setColor(new Color(32, 102, 148))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.MANAGE_CHANNEL);
        perms.add(Permission.MESSAGE_MANAGE);

        if (guild.getRolesByName("Warlord", true).isEmpty()) {
            guild.createRole()
                    .setName("Warlord")
                    .setPermissions(perms)
                    .setColor(new Color(230, 126, 34))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.MANAGE_CHANNEL);
        perms.add(Permission.KICK_MEMBERS);
        perms.add(Permission.VOICE_MUTE_OTHERS);
        perms.add(Permission.VOICE_DEAF_OTHERS);
        perms.add(Permission.VOICE_MOVE_OTHERS);
        perms.add(Permission.PRIORITY_SPEAKER);
        perms.add(Permission.VIEW_AUDIT_LOGS);

        if (guild.getRolesByName("Deputy  Leader", true).isEmpty()) {
            guild.createRole()
                    .setName("Deputy  Leader")
                    .setPermissions(perms)
                    .setColor(new Color(168, 67, 0))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.BAN_MEMBERS);
        perms.add(Permission.MANAGE_ROLES);

        if (guild.getRolesByName("Leader", true).isEmpty()) {
            guild.createRole()
                    .setName("Leader")
                    .setPermissions(perms)
                    .setColor(new Color(153, 45, 34))
                    .setHoisted(true)
                    .complete();
        }
    }

    private void setUpTextAdminChat(Guild guild) {

        if (guild.getTextChannelsByName("War-Council", true).isEmpty()) {
            ChannelAction<TextChannel> warCouncilChannel = guild.createTextChannel("War-Council");

            ArrayList<Permission> perms = new ArrayList<Permission>();
            perms.add(Permission.VIEW_CHANNEL);
            perms.add(Permission.MESSAGE_READ);
            perms.add(Permission.MESSAGE_WRITE);
            perms.add(Permission.MESSAGE_TTS);
            perms.add(Permission.MESSAGE_HISTORY);
            perms.add(Permission.MESSAGE_EMBED_LINKS);
            perms.add(Permission.MESSAGE_ATTACH_FILES);
            perms.add(Permission.MESSAGE_ADD_REACTION);
            perms.add(Permission.MESSAGE_EXT_EMOJI);

            warCouncilChannel.addPermissionOverride(guild.getRolesByName("Advisor", true).get(0), perms, null);
            warCouncilChannel.addPermissionOverride(guild.getRolesByName("WarLord", true).get(0), perms, null);

            perms.add(Permission.CREATE_INSTANT_INVITE);

            warCouncilChannel.addPermissionOverride(guild.getRolesByName("Deputy  Leader", true).get(0), perms, null);
            warCouncilChannel.addPermissionOverride(guild.getRolesByName("Leader", true).get(0), perms, null);

            perms.add(Permission.MANAGE_CHANNEL);
            perms.add(Permission.MANAGE_PERMISSIONS);
            perms.add(Permission.MANAGE_WEBHOOKS);
            perms.add(Permission.MESSAGE_MANAGE);
            perms.add(Permission.MESSAGE_MENTION_EVERYONE);
            warCouncilChannel.addPermissionOverride(guild.getPublicRole(), null, perms);

            warCouncilChannel.setParent(guild.getDefaultChannel().getParent());

            warCouncilChannel.queue();
        }
    }

    private void setUpSchedule(Iterator<LocalTime> wars) throws FileNotFoundException {
        SchedulerFactory sf = new StdSchedulerFactory();

        try {

            this.scheduler = sf.getScheduler();
            this.scheduler.start();
            LocalTime war;
            int i = 0;
            String cronWarStarts, cronAboutToBegin, warStarGroup;
            while (wars.hasNext()) {
                war = wars.next();

                //Add War about to start schedule
                warStarGroup = "War-start-" + i;
                WarStartsRemainderJob warReminder = new WarStartsRemainderJob();
                Date startTime = DateBuilder.nextGivenSecondDate(null, 10);
                JobDetail jobWarStarts = JobBuilder.newJob(WarStartsRemainderJob.class).withIdentity("Clan War Starts", warStarGroup).build();
                jobWarStarts.getJobDataMap().put("guilds", jda.getGuilds());
                cronWarStarts = "0 " + war.getMinuteOfHour()+" "+war.getHourOfDay()+ " ? * * *";
                CronTrigger crontriggerWarStarts = TriggerBuilder
                        .newTrigger()
                        .withIdentity("Clan War War Starts remainder Job", warStarGroup)
                        .startAt(startTime)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronWarStarts))
                        .build();

                scheduler.scheduleJob(jobWarStarts, crontriggerWarStarts);

                //Add war begins to schedule
                war = war.plusMinutes(45);
                warStarGroup="War-about-to-start"+i;
                cronAboutToBegin = "0 " + war.getMinuteOfHour()+" "+war.getHourOfDay()+ " ? * * *";
                JobDetail jobWarAboutToBegin = JobBuilder.newJob(WarAboutToBeginJob.class).withIdentity("Clan War About to Begin", warStarGroup).build();
                jobWarAboutToBegin.getJobDataMap().put("guilds", jda.getGuilds());
                CronTrigger crontriggerWarAboutToBegin = TriggerBuilder
                        .newTrigger()
                        .withIdentity("Clan War About to Begin remainder Job",warStarGroup)
                        .startAt(startTime)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronAboutToBegin))
                        .build();

                scheduler.scheduleJob(jobWarAboutToBegin, crontriggerWarAboutToBegin);

                i++;
            }
        } catch (SchedulerException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
