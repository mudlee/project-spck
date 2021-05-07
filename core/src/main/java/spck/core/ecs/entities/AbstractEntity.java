package spck.core.ecs.entities;

import com.artemis.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spck.core.Spck;

import java.util.Optional;

public abstract class AbstractEntity {
  private static final Logger log = LoggerFactory.getLogger(AbstractEntity.class);
  protected final String name;
  protected Integer id;

  public AbstractEntity(String name) {
    this.name = name;
  }

  public void onCreated(int id) {
    this.id = id;
    onCreated();
    log.debug("Entity '{}' [{}] created", name, id);
  }

  public abstract void onCreated();

  public <T extends Component> Optional<T> addComponent(Class<T> componentClass) {
    if(id == null) {
      log.error("Entity '{}' is not in ECS, cannot add {}", name, componentClass.getSimpleName());
      return Optional.empty();
    }

    log.debug("Adding {} to entity {}", componentClass.getSimpleName(), id);
    return Optional.of(Spck.ecs.getWorld().getMapper(componentClass).create(id));
  }
}
