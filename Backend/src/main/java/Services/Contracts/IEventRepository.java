package Services.Contracts;

import Models.Event;
import org.springframework.lang.NonNull;

public interface IEventRepository {
    void saveEvent(@NonNull Event event);

    void deleteEvent(String id);
}
