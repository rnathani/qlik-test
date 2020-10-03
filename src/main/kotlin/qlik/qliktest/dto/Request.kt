package qlik.qliktest.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel
class Request(@ApiModelProperty("Unique message Id") val messageId: String, @ApiModelProperty("a text message") val text: String)