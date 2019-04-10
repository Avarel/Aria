package xyz.avarel.aria.commands.music

import xyz.avarel.aria.utils.MessageContext
import xyz.avarel.core.commands.AnnotatedCommand
import xyz.avarel.core.commands.CommandInfo

@CommandInfo(
        aliases = ["test"],
        visible = false
)
class TestCommand : AnnotatedCommand<MessageContext>() {
    override suspend operator fun invoke(context: MessageContext) {
        context.parse {
            matchInt("Testing") {
                println(it)
            }
            expectError()
        }
    }
}