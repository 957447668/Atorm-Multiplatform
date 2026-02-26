package com.zxhhyj.atorm.clients

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
public annotation class LLMDescription(val description: String)