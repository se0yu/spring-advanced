package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    /*
    user를 lazy(지연로딩)으로 매핑 하고 있지만,
    fetch join을 사용해 연관 데이터를 한번에 호출하여 N+1 문제를 방지한 것으로 보인다.
    하지만 fetch join을 사용하면 페이징 기능에 문제가 발생한다.

    @EntityGraph를 사용하면 JPA 구현체가 내부적으로 fetch join과 유사한 쿼리를 생성하여 로딩해준다.
    단순 정렬이면 쿼리문이 필요하지 않을 수 있지만 아래에 명시해둠
    */
    @EntityGraph(attributePaths = {"user"})
//    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    @Query("SELECT t FROM Todo t ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}
