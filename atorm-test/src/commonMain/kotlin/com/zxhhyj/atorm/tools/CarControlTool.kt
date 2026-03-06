package com.zxhhyj.atorm.tools

import com.zxhhyj.atorm.agent.tool.Tool
import com.zxhhyj.atorm.clients.LLMDescription
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

object CarControlTool : Tool<CarControlTool.Args, String>(
    argsSerializer = Args.serializer(),
    resultSerializer = String.serializer(),
    name = "CarControlTool",
    description = "车载设备控制工具"
) {

    @Serializable
    data class Args(@property:LLMDescription("控制的设备") val device: Device) {
        @Serializable
        enum class Device {
            AIR_CONDITIONER,
            WINDOW,
            HEADLIGHT,
            TAIL_LIGHT,
            WIPER,
            RADIO
        }
    }

    override suspend fun execute(args: Args): String {
        return "已控制: ${args.device.name}"
    }
}