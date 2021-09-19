package com.example.account.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.client.RestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import java.util.*

inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class AuditService(val restTemplate: RestTemplate) {
    val AUDIT_GRAPHQL_URL = "http://localhost:9090/graphql"
    val ACCESS_TOKEN_REQUEST_URL = "http://localhost:8080/auth/realms/example2/protocol/openid-connect/token"

    val KEYCLOAK_CLIENT_ID = "auditexample"
    val KEYCLOAK_USERNAME = "employee1"
    val KEYCLOAK_PASSWORD = "123456"
    val KEYCLOAK_GRANT_TYPE = "password"
    val KEYCLOAK_CLIENT_SECRET = "1f2f760f-03d6-423a-93fa-0d47ee4b1ec2"

    fun saveTransactionMutation(transactionType: TransactionType, amount: BigDecimal, accountUUID: UUID): String {

        return "{\"operationName\":null,\"variables\":{},\"query\":\"mutation {\\n  saveTransaction(transactionType: ${transactionType.name}, amount: $amount, accountUUID: \\\"$accountUUID\\\") {\\n    amount\\n    id\\n    transactionType\\n  }\\n}\\n\"}"
    }

    fun saveTransaction(transactionType: TransactionType, amount: BigDecimal, accountUUID: UUID) {
        val headers = createHeader()

        val transactionGraqhlPostData = this.saveTransactionMutation(transactionType, amount, accountUUID)

        restTemplate.exchange(
            AUDIT_GRAPHQL_URL,
            HttpMethod.POST,
            HttpEntity(transactionGraqhlPostData, headers),
            typeRef<TransactionResponseDTO>()
        )
    }

    fun createHeader(): HttpHeaders {
        val headerKeycloak = HttpHeaders()
        headerKeycloak.contentType =  MediaType.APPLICATION_FORM_URLENCODED

        val keycloakPostData = ""
        val map = LinkedMultiValueMap<String, String>()
        map.add("client_id", KEYCLOAK_CLIENT_ID)
        map.add("username", KEYCLOAK_USERNAME)
        map.add("password", KEYCLOAK_PASSWORD)
        map.add("client_secret", KEYCLOAK_CLIENT_SECRET)
        map.add("grant_type", KEYCLOAK_GRANT_TYPE)

        val keycloakTokenResponseDTO = restTemplate.postForObject(
            ACCESS_TOKEN_REQUEST_URL,
            HttpEntity(map, headerKeycloak),
            KeycloakTokenResponseDTO::class.java
        )

        val authorizationHeader =  (keycloakTokenResponseDTO?.access_token ?: "")

        val headerWithAuth = HttpHeaders()
        headerWithAuth.contentType =MediaType.APPLICATION_JSON
        headerWithAuth.setBearerAuth( authorizationHeader)
        return headerWithAuth
    }
}

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL
}

data class TransactionResponseDTO(
    @JsonProperty("amount") val amount: BigDecimal?,
    @JsonProperty("id") val id: UUID?,
    @JsonProperty("transactionType") val transactionType: TransactionType?
)

data class KeycloakTokenResponseDTO(@JsonProperty("access_token") val access_token: String)
