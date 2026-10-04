package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);

    @Query("select sum(e.amount) from Expense e where e.userId = :userId")
    BigDecimal sumAll(@Param("userId") Long userId);

    @Query("""
           select sum(e.amount) from Expense e
           where e.userId = :userId
             and e.expenseDate between :start and :end
           """)
    BigDecimal sumBetween(@Param("userId") Long userId,
                          @Param("start") LocalDate start,
                          @Param("end") LocalDate end);

    @Query("""
           select e.category, sum(e.amount) from Expense e
           where e.userId = :userId
             and e.expenseDate between :start and :end
           group by e.category
           order by sum(e.amount) desc
           """)
    List<Object[]> sumByCategoryBetween(@Param("userId") Long userId,
                                        @Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    @Query("""
           select year(e.expenseDate), month(e.expenseDate), sum(e.amount)
           from Expense e
           where e.userId = :userId
             and e.expenseDate between :start and :end
           group by year(e.expenseDate), month(e.expenseDate)
           """)
    List<Object[]> sumByMonthBetween(@Param("userId") Long userId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);
}