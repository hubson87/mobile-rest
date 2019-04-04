package com.hubert.mobilerest.dto.v1;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel("Mobile subscriber list transfer object")
@Getter
@Setter
@AllArgsConstructor
public class MobileSubscribersDto {

    @ApiModelProperty(name = "Subscriber list")
    List<MobileSubscriberDto> subscribers;
}
