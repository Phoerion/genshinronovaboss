package phoerion.genshinronovaboss.entity.ai;

import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RonovaNavigation extends BirdNavigation {
    public RonovaNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected boolean isAtValidPosition() {
        return true;
    }

    private boolean isTooHigh() {
        double y = entity.getBoundingBox().minY;

        BlockPos oneBelow = BlockPos.ofFloored(entity.getX(), y - 1, entity.getZ());
        BlockPos twoBelow = BlockPos.ofFloored(entity.getX(), y - 2, entity.getZ());
        BlockPos threeBelow = BlockPos.ofFloored(entity.getX(), y - 3, entity.getZ());

        boolean air1 = world.getBlockState(oneBelow).isAir();
        boolean air2 = world.getBlockState(twoBelow).isAir();
        boolean air3 = world.getBlockState(threeBelow).isAir();

        // Если под мобом 3 блока воздуха → он слишком высоко
        return air1 && air2 && air3;
    }
}
