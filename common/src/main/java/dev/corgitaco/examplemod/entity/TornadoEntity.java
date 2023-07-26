package dev.corgitaco.examplemod.entity;


import com.google.common.annotations.VisibleForTesting;
import dev.corgitaco.examplemod.core.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class TornadoEntity extends Entity {
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<BlockPos>> START = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> NODE_IDX = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);

    private int life;

    @Nullable
    private NoiseWormPathGenerator pathGenerator;


    public TornadoEntity(EntityType<? extends TornadoEntity> $$0, Level level) {
        super($$0, level);
        this.setMaxUpStep(5);

    }


    @Override
    protected void defineSynchedData() {
        entityData.define(SIZE, 1F);
        entityData.define(START, Optional.empty());
        entityData.define(NODE_IDX, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {

        if (SIZE.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(entityDataAccessor);
        if (START.equals(entityDataAccessor)) {
            pathGenerator = new NoiseWormPathGenerator(NormalNoise.create(new LegacyRandomSource(this.uuid.getMostSignificantBits()), -4, 1.0D), entityData.get(START).orElseThrow(), blockPos -> false, 500);
        }
    }

    public void setStartPos(BlockPos pos) {
        setPos(Vec3.atCenterOf(pos));
        entityData.set(START, Optional.of(pos));
        pathGenerator = new NoiseWormPathGenerator(NormalNoise.create(new LegacyRandomSource(this.uuid.getMostSignificantBits()), -4, 1.0D), pos, blockPos -> false, 500);
    }


    @Override
    public void tick() {
        super.tick();

        if (entityData.get(START).isEmpty() && !level().isClientSide) {
            setStartPos(new BlockPos((int) position().x, (int) position().y, (int) position().z));
        }

        if (entityData.get(START).isEmpty() && level().isClientSide) {
            return;
        }


        int nodeIdx = getNodeIdx();
        NoiseWormPathGenerator.Node node = this.pathGenerator.getNodes().get(nodeIdx);


        Vec3 nodePos = Vec3.atCenterOf(node.getPos());

        Vec3 difference = nodePos.subtract(position());

        Vec3 velocity = difference.normalize().scale(1);
        this.setDeltaMovement(getDeltaMovement().add(velocity.x, 0, velocity.z));
        this.move(MoverType.SELF, getDeltaMovement());



        if ((level() instanceof ServerLevel serverLevel)) {
            for (NoiseWormPathGenerator.Node pathGeneratorNode : pathGenerator.getNodes()) {
                BlockPos pos = pathGeneratorNode.getPos();
                serverLevel.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 2, 0);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX(), pos.getY() + 20, pos.getZ(), 0, 0, 0, 0, 0);

            }
        }

        double hDistance = difference.horizontalDistance();
        if (!level().isClientSide && hDistance < 10) {
            if (pathGenerator.getNodes().size() - 1 > nodeIdx) {
                setNodeIdx(nodeIdx + 1);
            } else {
                discard();
            }
        }
    }

    public int getNodeIdx() {
        return entityData.get(NODE_IDX);
    }

    public void setNodeIdx(int idx) {
        entityData.set(NODE_IDX, idx);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.life = compoundTag.getInt("life");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("life", life);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public float getSize() {
        return Math.max(entityData.get(SIZE), 1);
    }

    @VisibleForTesting
    public void setSize(float pSize) {
        float i = Mth.clamp(pSize, 1F, Byte.MAX_VALUE);
        this.entityData.set(SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(getSize() * 0.5F);
    }
}
