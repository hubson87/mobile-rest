package com.hubert.mobilerest.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"companyName", "taxId"}, callSuper = false)
@ToString
@Entity
public class Company extends Customer implements Serializable {

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String taxId;

    @Builder
    public Company(Long id, String companyName, String taxId, String address) {
        super(id, address);
        this.companyName = companyName;
        this.taxId = taxId;
    }
}
