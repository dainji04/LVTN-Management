package group_10.group._0.repository;

import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.BinhLuan;
import group_10.group._0.entity.YeuCauThamGiaNhom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinhLuanRepository extends JpaRepository<BinhLuan, Integer> {
//    List<BinhLuan> findByMaBaiDangOrderByNgayTaoAsc(BaiViet maBaiDang);
    Slice<BinhLuan> findByMaBaiDangOrderByNgayTaoAsc(BaiViet maBaiDang, Pageable pageable);

    //Bình luận trong bình luận
    Slice<BinhLuan> findByMaBinhLuanChaOrderByNgayTaoAsc(BinhLuan maBinhLuanCha, Pageable pageable);


    List<BinhLuan> findByMaBinhLuanChaOrderByNgayTaoAsc(BinhLuan maBinhLuanCha);

    void deleteByMaBaiDang_Id(Integer maBaiDangId);


}