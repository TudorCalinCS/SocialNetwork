package com.socialnetwork.socialnetwork.repository;

import com.socialnetwork.socialnetwork.domain.Entity ;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAll(Pageable pageable);

    Page<E> findAllFriends(Pageable pageable,Entity E);
}

