package phoerion.genshinronovaboss.worldgen;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import phoerion.genshinronovaboss.GenshinRonovaBoss;

import java.util.List;

public class ModOrePlacement {
    public static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier){
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier);
    }

    public static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier){
        return List.of(CountPlacementModifier.of(count), heightModifier);
    }
    public static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier){
        return List.of(RarityFilterPlacementModifier.of(chance), heightModifier);
    }

}
