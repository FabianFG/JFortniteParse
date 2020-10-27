package me.fungames.jfortniteparse.ue4.io.al2

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

fun <T> Future<T>.await(): T {
    try {
        return get()
    } catch (e: ExecutionException) {
        throw e.cause!!
    }
}

fun <T> CompletableFuture<T>.complete(result: Result<T>) =
    result.fold({ complete(it) }, { completeExceptionally(it) })