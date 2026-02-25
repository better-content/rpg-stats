package com.example.rpgstats.common.network.packets

import com.example.rpgstats.common.attribute.StatAttributeProjector
import com.example.rpgstats.common.data.StatsCap
import com.example.rpgstats.common.network.Network
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class C2SResetStats {
    companion object {
        fun encode(msg: C2SResetStats, buf: FriendlyByteBuf) { /* no-op */ }

        fun decode(buf: FriendlyByteBuf): C2SResetStats = C2SResetStats()

        fun handle(msg: C2SResetStats, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val sender: ServerPlayer = context.sender ?: return@enqueueWork
                val stats = StatsCap.get(sender) ?: return@enqueueWork

                val total = stats.totalPointsThisLife()
                stats.allocations.clear()
                stats.unspentPoints = total

                StatAttributeProjector.reapply(sender)
                Network.syncTo(sender)
            }
            context.packetHandled = true
        }
    }
}
