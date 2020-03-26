package xyz.avarel.aria.commands

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import xyz.avarel.aria.MessageContext
import xyz.avarel.aria.utils.dsl
import xyz.avarel.core.commands.Command
import xyz.avarel.core.commands.CommandInfo

@CommandInfo(
    aliases = ["eval"],
    description = "You're not supposed to see this.",
    visible = false
)
class EvalCommand : Command<MessageContext> {
    private val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine

    override suspend fun invoke(context: MessageContext) {
        context.dsl {
            string("Code Snippet", "Kotlin code snippet to evaluate.") { input ->
                val script = input.trim()

                val bindings = mapOf(
                    "ctx" to context,
                    "jda" to context.jda,
                    "sm" to context.jda.shardManager!!,
                    "bot" to context.bot
                )

                val bindString =
                    bindings.map { "val ${it.key} = bindings[\"${it.key}\"] as ${it.value.javaClass.kotlin.qualifiedName}" }
                        .joinToString("\n")
                val bind = engine.createBindings()
                bind.putAll(bindings)

                try {
                    val result = engine.eval("$bindString\n${script}", bind)
                        ?: return context.addReaction("👌").queue()

                    context.channel.sendMessage("```\n${result.toString().take(1950)}```").queue()
                } catch (e: Exception) {
                    context.channel.sendMessage("An exception occurred.\n```\n${e.localizedMessage}```").queue()
                }
            }
        }
    }
}