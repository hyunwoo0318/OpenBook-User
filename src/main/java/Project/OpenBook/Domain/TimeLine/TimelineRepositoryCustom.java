package Project.OpenBook.Domain.TimeLine;

import java.util.List;
import java.util.Optional;

public interface TimelineRepositoryCustom {

    public List<Timeline> queryTimelinesWithEra();

    public List<Timeline> queryTimelinesWithEraAndjjhList();

    public Optional<Timeline> queryTimelineWithEra(Long id);

    public Optional<Long> queryRandomTimeline();
}
