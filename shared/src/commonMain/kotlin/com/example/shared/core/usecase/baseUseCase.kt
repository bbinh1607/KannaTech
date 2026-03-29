package com.example.shared.core.usecase

abstract class UseCase<in Params, ResultType> {
    suspend operator fun invoke(params: Params): ResultType = execute(params)

    protected abstract suspend fun execute(params: Params): ResultType
}

abstract class UseCaseNoParam<ResultType> {
    suspend operator fun invoke(): ResultType = execute()

    protected abstract suspend fun execute(): ResultType
}
