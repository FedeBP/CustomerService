package com.pinapp.customerservice.repository;

import com.pinapp.customerservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT AVG(c.age) FROM Customer c")
    Double findAverageAge();

    @Query("SELECT STDDEV(c.age) FROM Customer c")
    Double findAgeStandardDeviation();

    List<Customer> findByLastName(String lastName);

    List<Customer> findByAgeGreaterThanEqual(Integer age);

    List<Customer> findByAgeLessThanEqual(Integer age);
}