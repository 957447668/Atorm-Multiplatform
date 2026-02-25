package com.zxhhyj.atorm.openai.api.file

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * File status.
 */
@Serializable
@JvmInline
public value class FileStatus(public val raw: String)
