/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot;

import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;

/**
 *
 * @author jomartinez
 */
public class Main {

    private static final org.apache.log4j.Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Option conf = new Option("c", true, "path to the configuration file");
        Options opts = new Options();

        opts.addOption(conf);

        CommandLineParser prs = new DefaultParser();

        System.out.println("Present Project Directory : " + new File(".").getAbsolutePath());
        try {
            CommandLine cmdln = prs.parse(opts, args);
            Configuration cfg;
            if (cmdln.hasOption("c")) {
                cfg = Configuration.getInstance(cmdln.getOptionValue("c"));
            } else {
                cfg = Configuration.getInstance();
            }

            if (cfg.getProxy() == null) {
                HustleCastleBot bot = new HustleCastleBot(cfg.getToken(), cfg.getOwner());
            } else {
                HustleCastleBot bot = new HustleCastleBot(cfg.getToken(), cfg.getOwner(), cfg.getProxy());
            }
        } catch (InterruptedException ex) {
            logger.error(ex);
        } catch (LoginException ex) {
            logger.error(ex);
        } catch (FileNotFoundException ex) {
            logger.error("Configuration file (settings.json) not found. Please create a configuration file and run the bot again");
        } catch (IOException ex) {
            logger.error(ex);
        } catch (ParseException ex) {
            logger.error("Invalid switch");
        }         
    }
}
