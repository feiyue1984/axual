package org.yuefei.axual.configuration;

import org.springframework.batch.item.ItemProcessor;
import org.yuefei.axual.domain.Record;
import org.yuefei.axual.domain.RecordError;


public class RecordItemProcessor implements ItemProcessor<Record, RecordError> {
    @Override
    public RecordError process(final Record item) throws Exception {
        boolean isValid = !Record.REFERENCE_POOL.contains(item.getReference()) && item.getStartBalance().add(item.getMutation())
                                                                                      .compareTo(item.getEndBalance()) == 0;
        if (isValid) {
            Record.REFERENCE_POOL.add(item.getReference());
            return null;
        }
        return new RecordError(item.getReference(), item.getDescription());
    }
}
