package hexlet.code.repository;

import hexlet.code.domain.TaskStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {
    TaskStatus findById(long id);
}
