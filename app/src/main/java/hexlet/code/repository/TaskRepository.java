package hexlet.code.repository;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long>, QuerydslPredicateExecutor<Task> {
    Task findById(long id);

    Iterable<Task> findAll(Predicate predicate);
}
