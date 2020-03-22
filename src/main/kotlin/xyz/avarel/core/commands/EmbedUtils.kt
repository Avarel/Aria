package xyz.avarel.core.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color

/**
 * Builds new embed by populating newly created [EmbedBuilder] using provided [block]
 * and then converting it into a [MessageEmbed].
 */
inline fun buildEmbed(
    title: String? = null,
    url: String? = null,
    block: EmbedBuilder.() -> Unit
): MessageEmbed {
    return EmbedBuilder().setTitle(title, url).apply(block).build()
}

/**
 * Builds new embed by populating newly created [EmbedBuilder] using provided [block]
 * and then creating a [MessageAction] from it.
 */
inline fun MessageChannel.sendEmbed(
    title: String? = null,
    url: String? = null,
    block: EmbedBuilder.() -> Unit
): MessageAction {
    return this.sendMessage(buildEmbed(title, url) {
        if (this@sendEmbed is TextChannel) {
            setColor(this@sendEmbed.guild.selfMember.colorRaw)
        }
        block()
    })
}

/**
 * Builds new embed by populating newly created [EmbedBuilder] using provided [block]
 * and then appending it to this [MessageAction].
 */
inline fun MessageAction.embed(
    title: String? = null,
    url: String? = null,
    block: EmbedBuilder.() -> Unit
): MessageAction {
    return this.embed(buildEmbed(title, url, block))
}

/**
 * Title DSL method for [EmbedBuilder].
 */
inline fun EmbedBuilder.title(block: () -> String?) {
    this.setTitle(block())
}

/**
 * Description DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.desc(block: () -> String?) {
    this.setDescription(block())
}

/**
 * Description DSL method, using [StringBuilder] populated by [block], for [EmbedBuilder].
 */
inline fun EmbedBuilder.descBuilder(block: StringBuilder.() -> Unit) {
    this.setDescription(buildString(block))
}

/**
 * Title DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.color(block: () -> Color) {
    this.setColor(block())
}

/**
 * Image DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.image(block: () -> String?) {
    this.setImage(block())
}

/**
 * Thumbnail DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.thumbnail(block: () -> String?) {
    this.setThumbnail(block())
}

/**
 * Author DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.author(block: () -> String?) {
    this.setAuthor(block())
}

/**
 * Footer DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.footer(block: () -> String?) {
    this.setFooter(block(), null)
}

/**
 * Field DSL method populated by [block] for [EmbedBuilder].
 */
inline fun EmbedBuilder.field(
    title: String,
    inline: Boolean = false,
    block: () -> String
) {
    this.addField(title, block(), inline)
}

/**
 * Field DSL method, using [StringBuilder] populated by [block], for [EmbedBuilder].
 */
inline fun EmbedBuilder.fieldBuilder(
    title: String,
    inline: Boolean = false,
    block: StringBuilder.() -> Unit
) {
    this.addField(title, buildString(block), inline)
}