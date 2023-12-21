package ro.ubbcluj.map.socialnetwork.repository.paging;

import ro.ubbcluj.map.socialnetwork.domain.Entity;
import ro.ubbcluj.map.socialnetwork.repository.Repository;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}