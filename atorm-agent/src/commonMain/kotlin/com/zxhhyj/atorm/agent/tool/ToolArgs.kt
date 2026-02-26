package com.zxhhyj.atorm.agent.tool

import kotlinx.serialization.Serializable

public interface ToolArgs {
    @Serializable
    public class Empty : ToolArgs
}
