package net.nightmare.mod.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class NightmareVegitoEntity extends Monster {

    public NightmareVegitoEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    // ボスが出現した瞬間に「ナイトメア崩壊アリーナ（お城風の闘技場）」を自動生成！
    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            generateNightmareArena(serverLevel, this.blockPosition());
        }
    }

    private void generateNightmareArena(ServerLevel level, BlockPos center) {
        int radius = 15;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 5; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    
                    // 床の作成（黒曜石と泣く黒曜石）
                    if (y == -1) {
                        level.setBlockAndUpdate(pos, (x + z) % 2 == 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.CRYING_OBSIDIAN.defaultBlockState());
                    } 
                    // 外枠の壁と柱
                    else if ((Math.abs(x) == radius || Math.abs(z) == radius) && y < 4) {
                        level.setBlockAndUpdate(pos, Blocks.NETHER_BRICKS.defaultBlockState());
                    }
                    // 中の空間をクリア
                    else if (y >= 0 && y < 4) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }
}
