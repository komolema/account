package com.example.account.service

import java.math.BigDecimal
import java.util.*

data class CustomerDTO(val id: UUID, val name: String, val surname: String)

data class DepositEventDTO(val customerUUID: UUID, val oldBalance: BigDecimal, val newBalance: BigDecimal)
data class WithdrawalEventDTO(val customerUUID: UUID, val oldBalance: BigDecimal, val newBalance: BigDecimal)