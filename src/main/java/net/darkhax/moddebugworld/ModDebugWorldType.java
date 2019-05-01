package net.darkhax.moddebugworld;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.moddebugworld.debugworld.WorldTypeDebugModded;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(modid = "moddebugworld", name = "ModDebugWorldType", version = "@VERSION@", dependencies = "required-after:bookshelf", certificateFingerprint = "@FINGERPRINT@")
@EventBusSubscriber
public class ModDebugWorldType {

    public static final Logger LOG = LogManager.getLogger("ModDebugWorldType");

    @EventHandler
    public void onPreInit (FMLLoadCompleteEvent event) {

        try {

            // Get the debug world field so we can change the value. We need to replace this
            // field specifically, because vanilla hardcodes for it everywhere.
            final Field debugWorldField = ObfuscationReflectionHelper.findField(WorldType.class, "field_180272_g");

            // Sets the world type to null. This is needed to get around a hardcoded limit on
            // world type name. If this is not null, the new world type can not be created with
            // the same ID.
            setFinalStatic(debugWorldField, null);

            // Sets the world type to the new type.
            setFinalStatic(debugWorldField, new WorldTypeDebugModded());
        }

        catch (final Exception e) {

            LOG.catching(e);
        }
    }

    public static void setFinalStatic (Field field, Object newValue) throws Exception {

        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }
}