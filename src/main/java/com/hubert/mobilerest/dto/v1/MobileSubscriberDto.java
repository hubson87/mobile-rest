package com.hubert.mobilerest.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.stream.Stream;

@ApiModel("Mobile subscriber transfer object")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class MobileSubscriberDto {

    @ApiModelProperty(name = "Mobile number in E164 format (without '+' sign)", required = true)
    @NotNull
    @Pattern(regexp = "^[1-9]\\d{1,14}$", message = "Msdisdn must follow the E.164 format")
    private String msisdn;

    @ApiModelProperty(name = "Mobile number user id", required = true)
    @NotNull
    private Long userId;

    @ApiModelProperty(name = "Mobile number owner id", required = true)
    @NotNull
    private Long ownerId;

    @ApiModelProperty(name = "Service type (MOBILE_PREPAID or MOBILE_POSTPAID)", required = true)
    @NotNull
    @Pattern(regexp = "MOBILE_PREPAID|MOBILE_POSTPAID", message = "Service type value can be only one of MOBILE_PREPAID or MOBILE_POSTPAID")
    private String serviceType;

    @ApiModelProperty(name = "Mobile number creation date")
    private Long serviceStartDate;

    @JsonIgnore
    public boolean isEmpty() {
        return Stream.of(msisdn, userId, ownerId, serviceType, serviceStartDate).allMatch(Objects::isNull);
    }
}
