/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.configuration;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.LogManager;
import org.joda.time.LocalTime;
//import org.joda.time.LocalTime;

/**
 *
 * @author Joaquin Martinez <juacom04@gmail.com>
 */
public class Configuration implements Serializable
{
    private String token;
    private String owner;
    private String mysqlHost;
    private String mysqlPort;
    private String mysqlDatabaseName;
    private String mysqlUser;
    private String mysqlPassword;
    private String cronWarStarts;
    private String warStartsMessage;
    private String cronAboutToBegin;
    private String warAboutToBegin;
    private ArrayList<LocalTime> wars;
    private InetAddress proxyIp;
    private int proxyPort;
    private String warAnnouncementChannelName;
    private String administrativeChannelName;
    private int minutesToLastAnnounce;
    private LocalTime questRefreshTime;
    
    private static Configuration instance;

    
    private static final org.apache.log4j.Logger logger = LogManager.getLogger(Configuration.class.getName());
    
    public Configuration()
    {
        this.wars=new ArrayList<LocalTime>();
        this.minutesToLastAnnounce=60;
    }

    public String getMysqlHost()
    {
        return mysqlHost;
    }

    public void setMysqlHost(String mysqlHost)
    {
        this.mysqlHost = mysqlHost;
    }

    public String getMysqlPort()
    {
        return mysqlPort;
    }

    public void setMysqlPort(String mysqlPort)
    {
        this.mysqlPort = mysqlPort;
    }

    public String getMysqlDatabaseName()
    {
        return mysqlDatabaseName;
    }

    public void setMysqlDatabaseName(String mysqlDatabaseName)
    {
        this.mysqlDatabaseName = mysqlDatabaseName;
    }

    public String getMysqlUser()
    {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser)
    {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPassword()
    {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword)
    {
        this.mysqlPassword = mysqlPassword;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getCronWarStarts() {
        return cronWarStarts;
    }

    public void setCronWarStarts(String cronWarStarts) {
        this.cronWarStarts = cronWarStarts;
    }

    public String getCronAboutToBegin() {
        return cronAboutToBegin;
    }

    public void setCronAboutToBegin(String cronAboutToBegin) {
        this.cronAboutToBegin = cronAboutToBegin;
    }

    public String getWarStartsMessage() {
        return warStartsMessage;
    }

    public void setWarStartsMessage(String warMessage) {
        this.warStartsMessage = warMessage;
    }

    public String getWarAboutToBegin() {
        return warAboutToBegin;
    }

    public void setWarAboutToBegin(String warAboutToBegin) {
        this.warAboutToBegin = warAboutToBegin;
    }

     public Proxy getProxy() {
     Proxy ret=null;
     
     if(this.proxyIp!=null && this.proxyPort!=0)
     {
         ret=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxyIp, proxyPort));
     }
     
     return ret;
        
    }

    public LocalTime getQuestRefreshTime()
    {
        return questRefreshTime;
    }

    public void setQuestRefreshTime(LocalTime questRefreshTime)
    {
        this.questRefreshTime = questRefreshTime;
    }
     
     
    
    
    public static Configuration getInstance() throws FileNotFoundException
    {
        if(instance==null)
        {
           Gson gson = Converters.registerLocalTime(new GsonBuilder()).create();
           String configPath="./settings.json";
           logger.debug("Using Confg File "+configPath);
            instance=gson.fromJson(new FileReader(configPath), Configuration.class);
        }
        
        return instance;
    }
    
    public static Configuration getInstance(String configPath) throws FileNotFoundException
    {
        if(instance==null)
        {
           Gson gson = Converters.registerLocalTime(new GsonBuilder()).create();
           logger.debug("Using Confg File "+configPath);
            instance=gson.fromJson(new FileReader(configPath), Configuration.class);
        }
        
        return instance;
    }
    
    public void addWar(LocalTime warTime)
    {
        if(!wars.contains(warTime))
        {
            wars.add(warTime);
        }
    }
    
    
    public void removeWar(LocalTime warTime)
    {
        if(wars.contains(warTime))
        {
            wars.remove(warTime);
        }
    }    
    
    
    public Iterator<LocalTime> getWars()
    {
        return wars.iterator();
    }

    public String getWarAnnouncementChannelName() {
       String ret="war-announcement";
       if(this.warAnnouncementChannelName!=null)
       {
            ret=warAnnouncementChannelName.toLowerCase().replaceAll(" ","-");
       }       
       return ret;
    }

    public String getAdministrativeChannelName() {
    String ret="war-councile";

    if(this.administrativeChannelName!=null)
     {
        ret=administrativeChannelName.toLowerCase().replaceAll(" ","-");
     }     
     return ret;
    }

    public void setWarAnnouncementChannelName(String warAnnouncementChannelName) {
        this.warAnnouncementChannelName = warAnnouncementChannelName;
    }

    public void setAdministrativeChannelName(String administrativeChannelName) {
        this.administrativeChannelName = administrativeChannelName;
    }

    public int getMinutesToLastAnnounce()
    {
        return minutesToLastAnnounce;
    }

    public void setMinutesToLastAnnounce(int minutesToLastAnnounce) {
        this.minutesToLastAnnounce = minutesToLastAnnounce;
    }

    
    
    
}
