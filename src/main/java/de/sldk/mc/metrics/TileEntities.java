package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

/**
 * Get current count of all tile entities.
 *
 * Tile Entities are labelled by
 * <ol>
 *     <li> world,
 *     <li> type ({@link BlockState#getType()} and {@link BlockState#getData()}),
 * </ol>
 */
public class TileEntities extends WorldMetric {

    private static final Gauge TILE_ENTITIES = Gauge.build()
            .name(prefix("tileentities_total"))
            .help("Tile entities loaded per world")
            .labelNames("world", "type")
            .create();

    public TileEntities(Plugin plugin) {
        super(plugin, TILE_ENTITIES);
    }

    @Override
    public void collect(World world) {
        Map<BlockState, Long> mapTileEntityTypesToCounts = Arrays.stream(world.getLoadedChunks())
                .flatMap(s-> Arrays.stream(s.getTileEntities()))
                .collect(Collectors.groupingBy(s->s, Collectors.counting()));

        mapTileEntityTypesToCounts
                .forEach((tileEntity, count) ->
                        TILE_ENTITIES
                                .labels(world.getName(),
                                        tileEntity.getType()+":"+tileEntity.getData())
                                .set(count)
                );
    }

}
