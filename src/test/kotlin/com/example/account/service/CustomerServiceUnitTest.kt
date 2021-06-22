package com.example.account.service

import com.example.account.AccountRepository
import com.example.account.CustomerRepository
import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal


class CustomerServiceUnitTest(
    @Autowired
    val customerService: CustomerService,
    @MockBean
    val auditService: AuditService,
    @MockBean
    val accountRepository: AccountRepository,
    @MockBean
    val customerRepository: CustomerRepository
) {



    @ParameterizedTest
    @CsvSource(
        "abc,txz, 1000",
        "aaa,bbb, 105",
        "xax,dad, 105"
    )
    fun deposit(name: String, surname: String, amount: BigDecimal) {
        val t = customerService.createCustomer(name, surname,5)
        val depositEvent = customerService.deposit(t.get().id!!, amount)
        Assertions.assertEquals(depositEvent.get().newBalance, amount)
    }
}