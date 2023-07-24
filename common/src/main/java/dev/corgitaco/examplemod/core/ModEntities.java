package dev.corgitaco.examplemod.core;

import corgitaco.corgilib.reg.RegistrationProvider;
import corgitaco.corgilib.reg.RegistryObject;
import dev.corgitaco.examplemod.Constants;
import dev.corgitaco.examplemod.entity.TornadoEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    private static final RegistrationProvider<EntityType<?>> ENTITIES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MOD_ID);
    public static final RegistryObject<EntityType<TornadoEntity>> TORNADO = createEntity("tornado", EntityType.Builder.<TornadoEntity>of(TornadoEntity::new, MobCategory.MISC).sized(25, 25));

    private static <T extends Entity> RegistryObject<EntityType<T>> createEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build("%s:%s".formatted(Constants.MOD_ID, name)));
    }

    public static void init() {}
}
