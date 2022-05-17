package com.improvisados.hustlecastlediscordbot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.improvisados.hustlecastlediscordbot.commands.CreateInviteCommand;
import com.improvisados.hustlecastlediscordbot.commands.InvetemeCommand;
import com.improvisados.hustlecastlediscordbot.commands.WarsList;
import com.improvisados.hustlecastlediscordbot.jobs.WarStartsRemainderJob;
import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import com.improvisados.hustlecastlediscordbot.jobs.QuestRefreshJob;
import com.improvisados.hustlecastlediscordbot.jobs.WarAboutToBeginJob;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import java.awt.Color;
import java.io.FileNotFoundException;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import org.joda.time.LocalTime;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author joaquin
 */
public class HustleCastleBot extends ListenerAdapter {

    private JDA jda;
    private boolean respondeToBots;
    private static final Logger logger = LogManager.getLogger(HustleCastleBot.class.getName());
    private CommandClient commands;

    private Scheduler scheduler;

    public HustleCastleBot(String token, String owner) throws InterruptedException, LoginException {

        //this.jda = new JDABuilder(AccountType.BOT).setToken(token).build();
        this.jda = JDABuilder.createDefault(token).addEventListeners(this).setActivity(Activity.playing("Hustle Castle")).build();
        respondeToBots = false;
    }

    public HustleCastleBot(String token, String owner, Proxy proxy) throws InterruptedException, LoginException {

        OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxy).proxyAuthenticator(Authenticator.NONE);

        OkHttpClient cli = builder.proxy(proxy).proxyAuthenticator(Authenticator.NONE).connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();
        this.jda = JDABuilder.createDefault(token).setHttpClientBuilder(builder).setHttpClient(cli).addEventListeners(this).setActivity(Activity.playing("Hustle Castle")).build();
        //this.jda = new JDABuilder(AccountType.BOT).setHttpClientBuilder(builder).setHttpClient(cli).setToken(token).build();
        respondeToBots = false;

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
            setUpSchedule(cfg.getWars(),cfg.getQuestRefreshTime());
            LocalTime questRefreshTime=cfg.getQuestRefreshTime();
            
            
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("!");
        builder.setOwnerId(owner);
        builder.addCommand(new WarsList());
        builder.addCommand(new InvetemeCommand("583339878637371412", 536346103));
        builder.addCommand(new CreateInviteCommand());
        commands = builder.build();
        jda.addEventListener(commands);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event)
    {
        event.getGuild().getDefaultChannel().sendMessage("Nos vemos ***" + event.getUser().getName() + "***").queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {

        try {
            doGuildSetup(event.getGuild());
        }
        catch (FileNotFoundException ex) {

        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event)
    {
        try {
            doGuildSetup(event.getGuild());
        }
        catch (FileNotFoundException ex) {

        }
    }
    
    private void doGuildSetup(Guild guild) throws FileNotFoundException
    {
        logger.info(guild.getName() + " Ready");
        logger.info("   Setting up roles");
        setUpRoles(guild);
        logger.info("   Setting up Admins Text Chat");
        setUpTextAdminChat(guild);
        logger.info("   Setting up War Annoucement Text Chat");
        setUpWarAnnouncementTextChat(guild);        
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
        if (guild.getRolesByName("Soldiers", true).isEmpty()) {
            guild.createRole()
                    .setName("Soldiers")
                    .setPermissions(perms)
                    .setColor(new Color(52, 152, 219))
                    .setHoisted(true)
                    .complete();
        }

        if (guild.getRolesByName("Advisors", true).isEmpty()) {
            guild.createRole()
                    .setName("Advisors")
                    .setPermissions(perms)
                    .setColor(new Color(26, 188, 156))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.NICKNAME_MANAGE);
        perms.add(Permission.CREATE_INSTANT_INVITE);
        perms.add(Permission.MESSAGE_MENTION_EVERYONE);

        if (guild.getRolesByName("Officers", true).isEmpty()) {
            guild.createRole()
                    .setName("Officers")
                    .setPermissions(perms)
                    .setColor(new Color(32, 102, 148))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.MANAGE_CHANNEL);
        perms.add(Permission.MESSAGE_MANAGE);

        if (guild.getRolesByName("Warlords", true).isEmpty()) {
            guild.createRole()
                    .setName("Warlords")
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

        if (guild.getRolesByName("Deputy Leaders", true).isEmpty()) {
            guild.createRole()
                    .setName("Deputy Leaders")
                    .setPermissions(perms)
                    .setColor(new Color(168, 67, 0))
                    .setHoisted(true)
                    .complete();
        }

        perms.add(Permission.BAN_MEMBERS);
        perms.add(Permission.MANAGE_ROLES);

        if (guild.getRolesByName("Leaders", true).isEmpty()) {
            guild.createRole()
                    .setName("Leader")
                    .setPermissions(perms)
                    .setColor(new Color(153, 45, 34))
                    .setHoisted(true)
                    .complete();
        }
    }

    private void setUpTextAdminChat(Guild guild) throws FileNotFoundException {
        
        Configuration cfg = Configuration.getInstance();
        String channelName=cfg.getAdministrativeChannelName();
        
        if (guild.getTextChannelsByName(channelName, true).isEmpty()) {
            ChannelAction<TextChannel> warCouncilChannel = guild.createTextChannel(channelName);
            
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
            //perms.add(Permission.MANAGE_PERMISSIONS);
            //System.out.println("******************************** "+Permission.MANAGE_PERMISSIONS.getOffset()+"*****************************");
            perms.add(Permission.MANAGE_WEBHOOKS);
            perms.add(Permission.MESSAGE_MANAGE);
            perms.add(Permission.MESSAGE_MENTION_EVERYONE);
            
            warCouncilChannel.addPermissionOverride(guild.getPublicRole(), null, perms);

            warCouncilChannel.setParent(guild.getDefaultChannel().getParent());

            warCouncilChannel.queue();
        }
    }

    private void setUpWarAnnouncementTextChat(Guild guild) throws FileNotFoundException{
        Configuration cfg = Configuration.getInstance();
        //get War announcement name from configuration
        String channelName=cfg.getWarAnnouncementChannelName();
        if (guild.getTextChannelsByName(channelName, true).isEmpty()) 
        {
            //if the chanel don't exists create it
            ChannelAction<TextChannel> warCouncilChannel = guild.createTextChannel(channelName);
            
            //get the bot asociated role
            Role botRole=guild.getRolesByName("Hustle Castel Bot", true).get(0);
            
            //get the parent caregory
            Category parent=guild.getDefaultChannel().getParent();
            
            //Set Parent for new Chat equals to Default
            warCouncilChannel.setParent(parent);
            
            
            warCouncilChannel.addRolePermissionOverride(guild.getRolesByName("Solder", true).get(0).getIdLong(), 328768, 537065489);
            warCouncilChannel.addRolePermissionOverride(guild.getRolesByName("Officer", true).get(0).getIdLong(), 328768, 537065489);
            warCouncilChannel.addRolePermissionOverride(guild.getRolesByName("Warlord", true).get(0).getIdLong(), 328768, 537065489);
            warCouncilChannel.addRolePermissionOverride(guild.getRolesByName("Deputy  Leader", true).get(0).getIdLong(), 328768, 537065489);
            warCouncilChannel.addRolePermissionOverride(guild.getRolesByName("Leader", true).get(0).getIdLong(), 328768, 537065489);
            
           Collection<Permission> a=new ArrayList<Permission>();
           a.add(Permission.VIEW_CHANNEL);
           
            Collection<Permission> d=new ArrayList<Permission>();
            d.add(Permission.MANAGE_CHANNEL);
            //d.add(Permission.MANAGE_PERMISSIONS);
            d.add(Permission.MANAGE_WEBHOOKS);
            d.add(Permission.CREATE_INSTANT_INVITE);
            d.add(Permission.MESSAGE_WRITE);
            d.add(Permission.MESSAGE_EMBED_LINKS);
            d.add(Permission.MESSAGE_ATTACH_FILES);
            d.add(Permission.MESSAGE_ADD_REACTION);
            d.add(Permission.MESSAGE_EXT_EMOJI);
            d.add(Permission.MESSAGE_MENTION_EVERYONE);
            d.add(Permission.MESSAGE_MANAGE);
            d.add(Permission.MESSAGE_HISTORY);
            d.add(Permission.MESSAGE_TTS);
            d.add(Permission.USE_SLASH_COMMANDS);          
            
            
          
            warCouncilChannel.addRolePermissionOverride(guild.getPublicRole().getIdLong(),a,d);           
            warCouncilChannel.queue();
        }
    }

    private void setUpSchedule(Iterator<LocalTime> wars,LocalTime questRefreshTime) throws FileNotFoundException {
        SchedulerFactory sf = new StdSchedulerFactory();
       

        try {

            this.scheduler = sf.getScheduler();
            this.scheduler.start();
            LocalTime war;
            int i = 0;
            String cronWarStarts, cronAboutToBegin, warStarGroup;
            while (wars.hasNext()) {
                war = wars.next();
                
                setUpScheduleJob(new WarStartsRemainderJob(), war,"Clan War Starts","War-start-" + i);
                 war = war.plusMinutes(105);
                 setUpScheduleJob(new WarAboutToBeginJob(), war,"Clan War About to Begin", "Clan-War-about-to-begine-"+i);
                i++;
            }
            
            setUpScheduleJob(new QuestRefreshJob(),questRefreshTime,"Quest Refresh","Quest-refresh-0");
                
            
        } catch (SchedulerException ex) {
            java.util.logging.Logger.getLogger(HustleCastleBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    private void setUpScheduleJob(Job job, LocalTime time,String name, String group) throws SchedulerException
    {
        
                    //Add War about to start schedule
                Date startTime = DateBuilder.nextGivenSecondDate(null, 10);
                JobDetail jobWarStarts = JobBuilder.newJob(job.getClass()).withIdentity(name, group).build();
                jobWarStarts.getJobDataMap().put("guilds", jda.getGuilds());
                String cronWarStarts = "0 " + time.getMinuteOfHour() + " " + time.getHourOfDay() + " ? * * *";
                CronTrigger crontriggerWarStarts = TriggerBuilder
                        .newTrigger()
                        .withIdentity(name, group)
                        .startAt(startTime)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronWarStarts))
                        .build();

                scheduler.scheduleJob(jobWarStarts, crontriggerWarStarts);
    }
}
