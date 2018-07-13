package org.yuefei.axual.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.yuefei.axual.domain.Record;
import org.yuefei.axual.domain.RecordError;

import javax.sql.DataSource;

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

/*    @Bean
    public FlatFileItemWriter<RecordError> writer() {
        DelimitedLineAggregator<RecordError> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<RecordError> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"reference", "description"});
        aggregator.setFieldExtractor(extractor);
        return new FlatFileItemWriterBuilder<RecordError>()
                .name("recordItemWriter")
                .resource(new ClassPathResource("report.csv"))
                .lineAggregator(aggregator)
                .build();
    }*/



    @Bean
    public JdbcBatchItemWriter<RecordError> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<RecordError>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO record_error (reference, description) VALUES (:reference, :description)")
        .dataSource(dataSource)
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
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Record.class);
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
    public Step step1(JdbcBatchItemWriter<RecordError> writer) {
        return stepBuilderFactory.get("step1")
                .<Record, RecordError> chunk(5)
                .reader(csvRead())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Step step2(JdbcBatchItemWriter<RecordError> writer) {
        return stepBuilderFactory.get("step2")
                .<Record, RecordError> chunk(5)
                .reader(xmlRead())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public RecordItemProcessor processor() {
        return new RecordItemProcessor();
    }
}
