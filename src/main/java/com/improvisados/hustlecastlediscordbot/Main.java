/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot;

import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import java.io.FileNotFoundException;
import javax.security.auth.login.LoginException;
import org.apache.log4j.LogManager;

/**
 *
 * @author jomartinez
 */
public class Main
{
    private static final org.apache.log4j.Logger logger = LogManager.getLogger(Main.class.getName());
    
    public static void main(String[] args)
    {
        try
        {
            Configuration cfg=Configuration.getInstance();
            HustleCastleBot bot=new HustleCastleBot(cfg.getToken(),cfg.getOwner());
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
