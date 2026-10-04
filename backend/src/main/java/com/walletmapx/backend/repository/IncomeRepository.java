package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserId(Long userId);

    @Query("select sum(i.amount) from Income i where i.userId = :userId")
    BigDecimal sumAll(@Param("userId") Long userId);

    @Query("""
           select sum(i.amount) from Income i
           where i.userId = :userId
             and i.incomeDate between :start and :end
           """)
    BigDecimal sumBetween(@Param("userId") Long userId,
                          @Param("start") LocalDate start,
                          @Param("end") LocalDate end);

    /** rows: [category, total], biggest first */
    @Query("""
           select i.category, sum(i.amount) from Income i
           where i.userId = :userId
             and i.incomeDate between :start and :end
           group by i.category
           order by sum(i.amount) desc
           """)
    List<Object[]> sumByCategoryBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    /** rows: [year, month, total] */
    @Query("""
           select year(i.incomeDate), month(i.incomeDate), sum(i.amount)
           from Income i
           where i.userId = :userId
             and i.incomeDate between :start and :end
           group by year(i.incomeDate), month(i.incomeDate)
           """)
    List<Object[]> sumByMonthBetween(@Param("userId") Long userId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);
}