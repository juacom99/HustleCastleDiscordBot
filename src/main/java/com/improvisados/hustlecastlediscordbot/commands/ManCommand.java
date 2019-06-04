/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.commands;

import com.improvisados.hustlecastlediscordbot.HustleCastleBot;
import com.improvisados.hustlecastlediscordbot.configuration.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import com.mchange.v2.lang.StringUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.text.html.HTML;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@CommandInfo(
        name =
        {
            "man"
        },
        description = "Read on a topic"
)
@Author("Joaquin Martinez")
public class ManCommand extends Command
{

    private static final Logger logger = LogManager.getLogger(ManCommand.class.getName());
    private PreparedStatement psSelectTopic = null;

    private Connection con;

    public ManCommand()
    {
        this.name = "man";
        this.help = "get the manual for a topic";
        this.guildOnly = true;

        try
        {
            Configuration cfg = Configuration.getInstance();
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + cfg.getMysqlHost() + ":" + cfg.getMysqlPort() + "/" + cfg.getMysqlDatabaseName() + "?useSSL=false", cfg.getMysqlUser(), cfg.getMysqlPassword());
            psSelectTopic = con.prepareStatement("SELECT * FROM TOPICS WHERE topicName LIKE ?");

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void execute(CommandEvent ce)
    {
        if (ce.isFromType(ChannelType.TEXT))
        {
            String args = ce.getArgs();

            if (args != null && !args.trim().equals(""))
            {
                if (con != null)
                {
                    try
                    {
                        psSelectTopic.setString(1, "%" + args + "%");
                        ResultSet topic = psSelectTopic.executeQuery();

                        if (topic.next())
                        {
                            MessageBuilder builder = new MessageBuilder();
                            builder.append("***"+topic.getString("topicName").toUpperCase()+":***\n"+topic.getString("topicContent"));
                            
                            ce.getChannel().sendMessage(builder.build()).queue();
                        } else
                        {
                            ce.reply("Topico no encontrado");
                        }

                    } catch (SQLException ex)
                    {
                        logger.error(ex);
                    }
                } else
                {
                    logger.error("No connection to the database");
                }
            } else
            {
                ce.reply("El tema no puede ser vacio");
            }
        }
    }
}
