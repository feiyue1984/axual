package org.yuefei.axual.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.yuefei.axual.domain.RecordError;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public JobCompletionNotificationListener(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            jdbcTemplate.query("SELECT reference, description FROM record_error",
                               (rs, row) -> new RecordError(
                               rs.getLong(1),
                               rs.getString(2))
            ).forEach(error -> log.error("Found <" + error + "> in the database."));
        }
    }
}
