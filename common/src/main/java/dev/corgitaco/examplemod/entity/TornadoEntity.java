package dev.corgitaco.examplemod.entity;


import com.google.common.annotations.VisibleForTesting;
import dev.corgitaco.examplemod.core.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TornadoEntity extends Entity {
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);

    private int life;


    public TornadoEntity(EntityType<? extends TornadoEntity> $$0, Level level) {
        super($$0, level);
    }

    public TornadoEntity(Level level, Vec3 position, @Nullable Entity spawner) {
        super(ModEntities.TORNADO.get(), level);
        setPos(position);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(SIZE, 1F);
    }

    @Override
    public void tick() {
        super.tick();

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

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (SIZE.equals(pKey)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public EntityDimensions getDimensions(Pose pPose) {
        return super.getDimensions(pPose).scale(getSize() * 0.5F);
    }
}
