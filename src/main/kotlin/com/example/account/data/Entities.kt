package com.example.account.data

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Customer(
    var name: String,
    var surname: String,
    @OneToMany(mappedBy = "customer", cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
    var accounts: List<Account>,
    @Id @GeneratedValue var id:UUID? = null
)


@Entity
class Account(
    var balance: BigDecimal,
    @ManyToOne
    @JoinColumn(name="customer_id")
    var customer: Customer? = null,
    var accountType: AccountType = AccountType.DEFAULT,
    @Id @GeneratedValue var id: UUID? = null
)

enum class AccountType{
    DEFAULT,
    GROCERY,
    POCKET_MONEY,
    OTHER

}
