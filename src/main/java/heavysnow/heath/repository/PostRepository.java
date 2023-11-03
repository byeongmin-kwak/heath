package heavysnow.heath.repository;

import heavysnow.heath.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {


    @Query("select p from Post p where p.member.id = :memberId order by p.createdDate desc")
    Page<Post> findPageByMember(@Param("memberId") Long memberId, Pageable pageable);

}
