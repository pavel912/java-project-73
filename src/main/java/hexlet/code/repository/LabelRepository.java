package hexlet.code.repository;

import hexlet.code.domain.Label;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {
    Label findById(long id);
    @Override
    Iterable<Label> findAllById(Iterable<Long> longs);
}
