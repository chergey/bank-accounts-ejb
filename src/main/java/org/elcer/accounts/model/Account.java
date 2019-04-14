package org.elcer.accounts.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.ejb.Lock;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Accessors(chain = true)
@Cacheable(false)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ApacheDB don't work well with IDENTITY (https://issues.apache.org/jira/browse/DERBY-5151)
    private long id;

    private String name;
    private BigDecimal balance;


    public Account(long id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
    }

    public Account(BigDecimal balance) {
        this.balance = balance;
    }



}