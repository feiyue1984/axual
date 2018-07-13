package org.yuefei.axual.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecordError {
    private long reference;
    private String description;

    @Override
    public String toString() {
        return "RecordError{" +
        "reference=" + reference +
        ", description='" + description + '\'' +
        '}';
    }
}
