/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.improvisados.hustlecastlediscordbot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Joaquin Martinez <juacom04@gmail.com>
 */
public class WarRemainderJob implements Job
{
    
    

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException
    {
        System.out.println("Do something");
    }
    
}
