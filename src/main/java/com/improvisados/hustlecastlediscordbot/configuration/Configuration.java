/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;

/**
 *
 * @author Joaquin Martinez <juacom04@gmail.com>
 */
public class Configuration implements Serializable
{
    private String token;
    private String owner;
    
    private static Configuration instance;

    public Configuration(String token, String owner)
    {
        this.token = token;
        this.owner = owner;
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
    
    
    
    public static Configuration getInstance() throws FileNotFoundException
    {
        if(instance==null)
        {
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            
            instance=gson.fromJson(new FileReader("./settings.json"), Configuration.class);
        }
        
        return instance;
    }
    
    
    
    
    

}
