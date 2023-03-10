package com.example.currencyexchangeapp.domain.repository

import com.example.currencyexchangeapp.domain.model.states.Resource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

open class BaseRepository {

    open suspend fun <T> callOrError(func: Deferred<Response<T>>): Flow<Resource<T>> = flow {
        emit(Resource.Progress())
        try {
            func.await().let { resp ->
                resp.body().let { body ->
                    if (body != null) {
                        emit(Resource.Success(body))
                    } else {
                        resp.errorBody()?.let {
                            emit(
                                Resource.Error(it.string())
                            )
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                Resource.Error(
                    e.toString()
                )
            )
        }
    }
}