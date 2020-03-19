package xyz.avarel.aria.listener

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.avarel.aria.Bot

class InitialListener(private val bot: Bot) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        event.jda.selfUser.manager.apply {
            setName(bot.name)
        }
    }
}