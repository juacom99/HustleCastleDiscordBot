/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import org.apache.log4j.LogManager;



/**
 *
 * @author jomartinez
 */
public class Main {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        
        
           
            try
            {
            Configuration cfg=Configuration.getInstance();
            
            if(cfg.getProxy()==null)
            {
            HustleCastleBot bot=new HustleCastleBot(cfg.getToken(),cfg.getOwner());
            }
            else
            {
            HustleCastleBot bot=new HustleCastleBot(cfg.getToken(),cfg.getOwner(),cfg.getProxy());
            }
            } catch (InterruptedException ex)
            {
            logger.error(ex);
            } catch (LoginException ex)
            {
            logger.error(ex);
            }
            catch (FileNotFoundException ex)
            {
            logger.error("Configuration file (settings.json) not found. Please create a configuration file and run the bot again");
            }
           
    }
}
