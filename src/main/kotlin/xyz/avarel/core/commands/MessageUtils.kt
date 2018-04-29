package xyz.avarel.core.commands

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.requests.restaction.MessageAction
import java.awt.Color

inline fun MessageChannel.sendEmbed(title: String? = null, url: String? = null, block: EmbedBuilder.() -> Unit): MessageAction {
    return this.sendMessage(EmbedBuilder().apply {
        setTitle(title, url)
        if (this@sendEmbed is TextChannel) {
            setColor(this@sendEmbed.guild.selfMember.colorRaw)
        }
        block()
    }.build())
}

inline fun EmbedBuilder.title(block: () -> String?) {
    this.setTitle(block())
}

inline fun EmbedBuilder.desc(block: () -> String?) {
    this.setDescription(block())
}

inline fun EmbedBuilder.descBuilder(block: StringBuilder.() -> Unit) {
    this.setDescription(buildString(block))
}

inline fun EmbedBuilder.color(block: () -> Color) {
    this.setColor(block())
}

inline fun EmbedBuilder.image(block: () -> String?) {
    this.setImage(block())
}

inline fun EmbedBuilder.thumbnail(block: () -> String?) {
    this.setThumbnail(block())
}

inline fun EmbedBuilder.author(block: () -> String?) {
    this.setAuthor(block())
}

inline fun EmbedBuilder.footer(block: () -> String?) {
    this.setFooter(block(), null)
}

inline fun EmbedBuilder.field(title: String, inline: Boolean = false, block: () -> String) {
    this.addField(title, block(), inline)
}

inline fun EmbedBuilder.fieldBuilder(title: String, inline: Boolean = false, block: StringBuilder.() -> Unit) {
    this.addField(title, buildString(block), inline)
}