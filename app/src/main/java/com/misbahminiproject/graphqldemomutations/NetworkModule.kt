package com.misbahminiproject.graphqldemomutations

import com.apollographql.apollo3.ApolloClient

object NetworkModule {
    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl("https://graphql.postman-echo.com/graphql")
            .build()
    }
}
