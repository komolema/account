package com.example.account.service

import com.example.account.AccountRepository
import com.example.account.CustomerRepository
import com.example.account.data.Account
import com.example.account.data.AccountType
import com.example.account.data.Customer
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
@GraphQLApi
class CustomerService(
    val customerRepository: CustomerRepository,
    val accountRepository: AccountRepository,
    val auditService: AuditService
) {

    @GraphQLQuery
    fun getCustomer(@GraphQLArgument(name = "uuid") uuid: UUID): Optional<CustomerDTO>? {
        val customerDTOOption = customerRepository
            .findById(uuid)
            .map { c -> CustomerDTO(c.id!!, c.name, c.surname) }

        return customerDTOOption
    }

    @GraphQLMutation
    fun createCustomer(
        @GraphQLArgument(name = "name") name: String,
        @GraphQLArgument(name = "surname") surname: String,
        @GraphQLArgument(name = "numberOfAccounts") numberOfAccounts: Int
    ): Optional<CustomerDTO> {

        val customer = Customer(name, surname, emptyList())
        val savedCustomer = customerRepository.save(customer)
        val accounts = (0..numberOfAccounts).map { index ->
            if (index > 0) {
                Account(BigDecimal(0), savedCustomer, AccountType.OTHER)
            } else {
                Account(BigDecimal(0), savedCustomer)
            }
        }
        accountRepository.saveAll(accounts)

        return Optional.of(CustomerDTO(savedCustomer.id!!, savedCustomer.name, savedCustomer.surname))
    }

    @GraphQLMutation
    fun deleteCustomer(@GraphQLArgument(name = "uuid") customerUUID: UUID) {
        customerRepository.deleteById(customerUUID)
    }

    @GraphQLMutation
    fun deposit(
        @GraphQLArgument(name = "customerUUID") customerUUID: UUID,
        @GraphQLArgument(name = "amount") amount: BigDecimal
    ): Optional<DepositEventDTO> {
        val oldAccountBalance = accountRepository.accountByCustomer(customerUUID, AccountType.DEFAULT).balance
        accountRepository.depositAmountByCustomer(customerUUID, amount)
        val updatedAccount = accountRepository.accountByCustomer(customerUUID, AccountType.DEFAULT)

        auditService.saveTransaction(TransactionType.DEPOSIT, amount, updatedAccount.id!!)
        return Optional.of(DepositEventDTO(customerUUID, oldAccountBalance, updatedAccount.balance))
    }

    @GraphQLMutation
    fun withdraw(
        @GraphQLArgument(name = "customerUUID") customerUUID: UUID,
        @GraphQLArgument(name = "amount") amount: BigDecimal
    ): Optional<WithdrawalEventDTO> {
        val oldAccountBalance = accountRepository.accountByCustomer(customerUUID, AccountType.DEFAULT).balance
        if (oldAccountBalance - amount >= BigDecimal(0)) {
            accountRepository.withdrawAmountByCustomer(customerUUID, amount)
            val updatedAccount = accountRepository.accountByCustomer(customerUUID, AccountType.DEFAULT)

            auditService.saveTransaction(TransactionType.DEPOSIT, amount, updatedAccount.id!!)

            return Optional.of(WithdrawalEventDTO(customerUUID, oldAccountBalance, updatedAccount.balance))
        }
        return Optional.empty<WithdrawalEventDTO>()
    }
}


