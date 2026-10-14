package net.nightmare.mod.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.nightmare.mod.NightmareMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

@Mod.EventBusSubscriber(modid = NightmareMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEvents {

    private static final Random RANDOM = new Random();

    // M1攻撃時の「黒閃 (KOKUSEN)」判定 ＆ 紫の衝撃波エフェクト
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            if (target.level() instanceof ServerLevel level) {
                float baseDamage = event.getAmount();

                // 禍々しい打撃エフェクト
                level.sendParticles(ParticleTypes.DRAGON_BREATH, target.getX(), target.getY() + 1.0, target.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                level.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + 1.0, target.getZ(), 2, 0.1, 0.1, 0.1, 0.0);

                // M1確率「黒閃 (KOKUSEN)」判定 (20%の確率でダメージ5倍＋重低音)
                if (RANDOM.nextFloat() < 0.20f) {
                    event.setAmount(baseDamage * 5.0f);
                    level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getY() + 1.0, target.getZ(), 50, 0.8, 0.8, 0.8, 0.2);
                    level.playSound(null, target.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 0.5f);
                }
            }
        }
    }

    // モブ撃破時の「全アイテムガチャ」＆「超ジャックポット」
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof Player player) {
            ServerLevel level = (ServerLevel) event.getEntity().level();
            LivingEntity victim = event.getEntity();

            // レジストリ内の全アイテムからランダムドロップ
            var allItems = ForgeRegistries.ITEMS.getValues().stream().toList();
            if (!allItems.isEmpty()) {
                ItemStack randomDrop = new ItemStack(allItems.get(RANDOM.nextInt(allItems.size())));
                level.addFreshEntity(new ItemEntity(level, victim.getX(), victim.getY(), victim.getZ(), randomDrop));
            }

            // 5%の確率でジャックポット発動（大量のネザライト・ダイヤ噴出）
            if (RANDOM.nextFloat() < 0.05f) {
                for (int i = 0; i < 20; i++) {
                    ItemStack jackpotItem = new ItemStack(RANDOM.nextBoolean() ? Items.NETHERITE_INGOT : Items.DIAMOND_BLOCK);
                    level.addFreshEntity(new ItemEntity(level, victim.getX() + (RANDOM.nextDouble() - 0.5) * 3, victim.getY() + 2, victim.getZ() + (RANDOM.nextDouble() - 0.5) * 3, jackpotItem));
                }
                level.playSound(null, victim.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.5f, 1.0f);
            }
        }
    }
}
