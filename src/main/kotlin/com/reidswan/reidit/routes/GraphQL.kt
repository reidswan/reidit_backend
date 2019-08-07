package com.reidswan.reidit.routes

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.annotations.GraphQLName
import com.expedia.graphql.toSchema
import com.reidswan.reidit.graphql.AccountMutations
import com.reidswan.reidit.graphql.AccountQueries
import graphql.ExecutionInput
import graphql.GraphQL
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.pipeline.Pipeline
import io.ktor.util.pipeline.PipelinePhase

data class GraphQLRequest(val query: String, val operationName: String, val variables: Map<String, Any>?)

fun Application.graphQL() {
    val config = SchemaGeneratorConfig(listOf("com.reidswan"))

    /** the GraphQL schema objects **/
    val queries = listOf(TopLevelObject(AccountQueries()))
    val mutations = listOf(TopLevelObject(AccountMutations()))

    /** the GraphQL schemas */
    val querySchema = toSchema(config=config, queries=queries)
    val queryAndMutationSchema = toSchema(config=config, queries=queries, mutations=mutations)

    /** the GraphQL engines */
    val queryGraphQL = GraphQL.newGraphQL(querySchema).build()
    val queryAndMutationGraphQL = GraphQL.newGraphQL(queryAndMutationSchema).build()

    /** the graphiql 'IDE' content page */
    val graphiql by lazy { GraphQLRequest::class.java.getResource("/static/graphiql.html").readText() }

    suspend fun ApplicationCall.executeQuery(graphQLSource: GraphQL) {
        val request = receive<GraphQLRequest>()
        val executionInput = ExecutionInput.newExecutionInput()
            .context(this)
            .query(request.query)
            .operationName(request.operationName)
            .variables(request.variables ?: mapOf())
            .build()
        respond(graphQLSource.execute(executionInput))
    }

    routing {
        authenticate(optional = true) {
            get("/graphql") {
                call.executeQuery(queryGraphQL)
            }
            post("/graphql") {
                call.executeQuery(queryAndMutationGraphQL)
            }
        }

        get("/graphiql") {
            call.respondText(graphiql, contentType=ContentType.Text.Html)
        }
    }
}