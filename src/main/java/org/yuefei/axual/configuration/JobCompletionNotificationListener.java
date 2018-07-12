package org.yuefei.axual.configuration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.yuefei.axual.AxualApplication;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    @Autowired
    private ApplicationContext ctx;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            SpringApplication.exit(ctx, () -> 0);
        } else {
            SpringApplication.exit(ctx, () -> -1);
        }
    }
}
