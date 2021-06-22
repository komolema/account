package com.example.account

import com.example.account.data.Account
import com.example.account.data.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriesIntegrationTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val accountRepository: AccountRepository,
) {
    fun createCustomer(): Customer {
        val customer = Customer("karabo", "molema", emptyList());
        entityManager.persist(customer)
        entityManager.flush()
        return customer
    }

    @Test
    fun `When findByIdOrNull then return Account`() {
        val customer = createCustomer();
        val account = Account(BigDecimal(1000.00), customer)
        entityManager.persistAndFlush(account)
        val found = accountRepository.findByIdOrNull(account.id!!)
        Assertions.assertThat(found).isEqualTo(account)
    }

}