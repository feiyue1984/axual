package org.yuefei.axual.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.yuefei.axual.domain.Record;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Record> csvRead() {
        return new FlatFileItemReaderBuilder<Record>()
                .name("csvReader")
                .resource(new ClassPathResource("records.csv"))
                .linesToSkip(1)
                .delimited()
                .names(new String[]{"reference", "accountNumber", "description", "startBalance", "mutation",
                        "endBalance"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Record>(){{
                    setTargetType(Record.class);
                }})
                .build();
    }

    @Bean
    public FlatFileItemWriter<Record> write() {
        return new FlatFileItemWriterBuilder<Record>()
                .name("recordItemWriter")
                .resource(new ClassPathResource("report.csv"))
                .lineAggregator(new RecordLineAggregator<>())
                .shouldDeleteIfExists(true)
                .build();
    }

    @Bean
    public StaxEventItemReader<Record> xmlRead() {
        return new StaxEventItemReaderBuilder<Record>()
               .name("xmlReader")
                .resource(new ClassPathResource("records.xml"))
                .addFragmentRootElements("record")
                .unmarshaller(recordMarshaller())
                .build();
    }

    @Bean
    public Jaxb2Marshaller recordMarshaller() {
        return marshaller;
    }

    @Bean
    public Job validateRecordsJob(JobCompletionNotificationListener listener, Step step1, Step step2) {
        return jobBuilderFactory.get("validateRecordsJob")
                                .incrementer(new RunIdIncrementer())
                                .listener(listener)
                                .start(step1)
                                .next(step2)
                                .build();
    }

    @Bean
    public Step step1(FlatFileItemWriter<Record> writer) {
        return stepBuilderFactory.get("step1")
                .<Record, Record> chunk(100)
                .reader(csvRead())
                .writer(writer)
                .build();
    }

    @Bean
    public Step step2(FlatFileItemWriter<Record> writer) {
        return stepBuilderFactory.get("step2")
                .<Record, Record> chunk(100)
                .reader(xmlRead())
                .writer(writer)
                .build();
    }

}
