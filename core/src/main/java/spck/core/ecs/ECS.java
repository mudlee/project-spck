package spck.core.ecs;

import com.artemis.BaseSystem;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.ecs.entities.AbstractEntity;

import java.util.List;

public class ECS {
  private static final Logger log = LoggerFactory.getLogger(ECS.class);
  private final World world;

  public ECS(List<BaseSystem> systems) {
    final var ecsBuilder = new WorldConfigurationBuilder();
    systems.forEach(system -> {
      log.debug("Registering ECS system: {}", system.getClass().getSimpleName());
      ecsBuilder.with(system);
    });
    this.world = new World(ecsBuilder.build());
  }

  public void addEntity(AbstractEntity entity) {
    final var id = world.create();
    entity.onCreated(id);
  }

  public void update(float delta) {
    world.setDelta(delta);
    world.process();
  }

  public World getWorld() {
    return world;
  }
}
