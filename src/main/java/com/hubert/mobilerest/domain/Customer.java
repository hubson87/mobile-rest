package com.hubert.mobilerest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"usedNumbers", "ownedNumbers"})
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(4000)", length = 4000)
    protected String address;

    @OneToMany(mappedBy = "user")
    private Set<MobileSubscriber> usedNumbers = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<MobileSubscriber> ownedNumbers = new HashSet<>();

     protected Customer(Long id, String address) {
        this.id = id;
        this.address = address;
    }

     public Customer(Long id) {
        this.id = id;
    }
}
