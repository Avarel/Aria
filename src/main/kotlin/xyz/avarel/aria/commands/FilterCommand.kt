//package xyz.avarel.aria.commands
//
//import xyz.avarel.aria.*
//import xyz.avarel.aria.utils.insufficientArgumentsMessage
//import xyz.avarel.aria.utils.invalidArgumentsMessage
//import xyz.avarel.aria.utils.requireMusicControllerMessage
//import xyz.avarel.core.commands.*
//
//@CommandInfo(
//        aliases = ["filter"],
//        description = "Change the music filter."
//)
//class FilterCommand : AnnotatedCommand<MessageContext> {
//    override suspend operator fun invoke(context: MessageContext) {
//        val controller = context.bot.musicManager.getExisting(context.guild.idLong)
//                ?: return requireMusicControllerMessage(context)
//
//        if (context.args.size < 2) {
//            return insufficientArgumentsMessage(context, "`speed` or `pitch` and a numeric value")
//        }
//
//        val number = context.args[1].toDoubleOrNull()?.coerceIn(0.1, 2.0)
//                ?: return invalidArgumentsMessage(context, "numeric value")
//
//        val settings = when (context.args[0].toLowerCase()) {
//            "speed" -> controller.timescaleSettings.copy(speed = number)
//            "pitch" -> controller.timescaleSettings.copy(pitch = number)
//            else -> return invalidArgumentsMessage(context, "FilterCommand property")
//        }
//
//        controller.enableFilter()
//        controller.updateFilter(settings)
//
//        context.channel.sendEmbed("Filter Settings") {
//            desc { "Changed ${context.args[0].toLowerCase()} of the music player to `$number`." }
//        }.queue()
//    }
//}