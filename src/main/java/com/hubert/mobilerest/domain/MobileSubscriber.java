package com.hubert.mobilerest.domain;

import com.hubert.mobilerest.domain.converters.LocalDateTimeEpochConverter;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"msisdn"})
@ToString
@Entity
public class MobileSubscriber implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String msisdn;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "CUSTOMER_ID_USER", nullable = false)
    private Customer user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "CUSTOMER_ID_OWNER", nullable = false)
    private Customer owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Convert(converter = LocalDateTimeEpochConverter.class)
    @Column(nullable = false)
    private LocalDateTime serviceStartDate;

    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @Builder
    public MobileSubscriber(Long id, String msisdn, Customer user, Customer owner, ServiceType serviceType, LocalDateTime serviceStartDate) {
        this.id = id;
        this.msisdn = msisdn;
        this.user = user;
        this.owner = owner;
        this.serviceType = serviceType;
        this.serviceStartDate = serviceStartDate;
    }

    public void assignUser(Customer user) {
        if (this.user != null) {
            user.getUsedNumbers().remove(this);
        }
        this.user = user;
        if (user != null) {
            user.getUsedNumbers().add(this);
        }
    }

    public void assignOwner(Customer owner) {
        if (this.owner != null) {
            owner.getOwnedNumbers().remove(this);
        }
        this.owner = owner;
        if (owner != null) {
            owner.getOwnedNumbers().add(this);
        }
    }
}
