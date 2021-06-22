package com.example.account.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
internal class CustomerServiceIntegrationTest(
    @Autowired
    val customerService: CustomerService
) {
    @ParameterizedTest
    @CsvSource(
        "abc,txz, 10",
        "aaa,bbb, 5",
        "xax,dad, -5"
    )
    fun createCustomer(name: String, surname: String, numberOfRecords: Int) {
        val t = customerService.createCustomer(name, surname,numberOfRecords)

        Assertions.assertEquals(name, t.get().name)
        Assertions.assertEquals(surname, t.get().surname)
    }

    @ParameterizedTest
    @CsvSource(
        "abc,txz, 10",
        "aaa,bbb, 5",
        "xax,dad, -5"
    )
    fun deleteCustomer(name: String, surname: String, numberOfRecords: Int) {
        val t = customerService.createCustomer(name, surname,numberOfRecords)

        Assertions.assertEquals(name, t.get().name)
        Assertions.assertEquals(surname, t.get().surname)

        customerService.deleteCustomer(t.get().id)

        val customer = customerService.getCustomer(t.get().id)

        if (customer != null) {
            Assertions.assertTrue(customer.isEmpty)
        }
    }

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

    @ParameterizedTest
    @CsvSource(
        "abc,txz, 1000, 1000",
        "aaa,bbb, 105, 100",
        "xax,dad, 105, 10"
    )
    fun withdraw(name: String, surname: String, amount: BigDecimal, withdrawalAmount:BigDecimal) {
        val t = customerService.createCustomer(name, surname,5)
        val depositEvent = customerService.deposit(t.get().id!!, amount)
        Assertions.assertEquals(depositEvent.get().newBalance, amount)

        val withdrawalEvent = customerService.withdraw(t.get().id, withdrawalAmount)

        Assertions.assertEquals(withdrawalEvent.get().newBalance, amount - withdrawalAmount)

    }

}