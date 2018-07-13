package org.yuefei.axual.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class Record {
    public static final Set<Long> REFERENCE_POOL = new HashSet<>();

    @XmlAttribute(name = "reference")
    private long reference;
    @XmlElement(name = "accountNumber")
    private String accountNumber;
    @XmlElement(name = "description")
    private String description;
    @XmlElement(name = "startBalance")
    private BigDecimal startBalance;
    @XmlElement(name = "mutation")
    private BigDecimal mutation;
    @XmlElement(name = "endBalance")
    private BigDecimal endBalance;
}
