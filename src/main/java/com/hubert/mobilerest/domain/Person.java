package com.hubert.mobilerest.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"firstName", "lastName", "documentId"}, callSuper = false)
@ToString
@Entity
public class Person extends Customer implements Serializable {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String documentId;

    @Builder
    public Person(Long id, String address, String firstName, String lastName, String docId) {
        super(id, address);
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = docId;
    }
}
