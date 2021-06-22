package com.example.account

import com.example.account.data.Account
import com.example.account.data.AccountType
import com.example.account.data.Customer
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

interface CustomerRepository : CrudRepository<Customer, UUID>

interface AccountRepository : CrudRepository<Account, UUID> {

    @Transactional
    @Modifying
    @Query(
        "UPDATE Account SET balance = balance + :amount WHERE customer_id = :customerUUID AND account_type = 0",
        nativeQuery = true
    )
    fun depositAmountByCustomer(customerUUID: UUID, amount: BigDecimal)

    @Transactional
    @Modifying
    @Query(
        "UPDATE Account SET balance = balance - :amount WHERE customer_id = :customerUUID AND account_type = 0",
        nativeQuery = true
    )
    fun withdrawAmountByCustomer(customerUUID: UUID, amount: BigDecimal)

    @Query(
        "SELECT * FROM Account WHERE customer_id = :customerUUID AND account_type = :#{#accountType.ordinal}",
        nativeQuery = true
    )
    fun accountByCustomer(customerUUID: UUID, accountType: AccountType): Account

}