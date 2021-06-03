package com.Udee.repository;

import com.Udee.models.User;
import com.Udee.models.projections.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> , JpaSpecificationExecutor<User>{

    Page<User> findAll(Specification<User> spec,Pageable pageable);

    Optional<UserProjection> findProjectedById(Integer id);

    Optional<User> findById(Integer id);

    User findByEmail(String email);
}
