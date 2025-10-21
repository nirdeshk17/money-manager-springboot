package com.alienCoders.moneymanger.repository;

import com.alienCoders.moneymanger.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {

    //select * from tbl_category where profile_id=?
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from tbl_category where id=? and profile_id=?
    Optional<CategoryEntity> findByIdAndProfileId(Long id,Long profileId);

    //select * from tbl_category where type=? and profile_id=?
    List<CategoryEntity> findByTypeAndProfileId(String type,Long profileId);

    // select CASE WHEN COUNT(*)>0 THEN TRUE ELSE FALSE END from tbl_category where name=? AND profile_id=?
    Boolean existsByNameAndProfileId(String name,Long profileId);
}

